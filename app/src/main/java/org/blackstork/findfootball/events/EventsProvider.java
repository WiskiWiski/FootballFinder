package org.blackstork.findfootball.events;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.firebase.database.FBCompleteListener;
import org.blackstork.findfootball.firebase.database.FBFootballDatabase;
import org.blackstork.findfootball.firebase.database.FBUserDatabase;
import org.blackstork.findfootball.objects.GameObj;
import org.blackstork.findfootball.time.TimeProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by WiskiW on 12.04.2017.
 */

public class EventsProvider {

    public static final String TAG = App.G_TAG + ":EventsProvider";

    private Context context;
    private String uid;
    private FBCompleteListener callback;

    private EVENTS_TYPE requestType;

    private enum EVENTS_TYPE {
        Upcoming, Archived
    }

    private List<GameObj> gameList;

    public EventsProvider(Context context, String uid, FBCompleteListener callback) {
        this.context = context;
        this.uid = uid;
        this.callback = callback;
    }

    private Iterator<DataSnapshot> gamesIterator;

    private void getGames() {
        gameList = new ArrayList<>();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gamesIterator = dataSnapshot.getChildren().iterator();
                if (gamesIterator.hasNext()) {
                    processData(gamesIterator.next());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                callback.onFailed();
            }
        };
        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference();
        mPostReference = mPostReference.child(FBUserDatabase.USERS_PATH).child(uid).child("events").child("football");
        mPostReference.addListenerForSingleValueEvent(postListener);
    }

    public void getUpcomingGames() {
        requestType = EVENTS_TYPE.Upcoming;
        getGames();
    }


    public void getArchivedGames() {
        requestType = EVENTS_TYPE.Archived;
        getGames();
    }


    private void processData(DataSnapshot postSnapshot) {
        final String eid = postSnapshot.getKey();
        final FBFootballDatabase footballDatabase = FBFootballDatabase.newInstance(context);
        footballDatabase.readGame(new FBCompleteListener() {
            @Override
            public void onSuccess(Object object) {
                if (object != null) {
                    GameObj game = (GameObj) object;

                    Log.d(TAG, "game: " + game.getTitle() + " [" + game.getDescription() + "]");
                    if (game.getEventTime() > TimeProvider.getUtcTime() - 1000) { // - 1000 - просто так
                        if (requestType == EVENTS_TYPE.Upcoming) {
                            gameList.add(game);
                        }
                    } else {
                        if (requestType == EVENTS_TYPE.Archived) {
                            gameList.add(game);
                        }
                    }
                } else {
                    FBUserDatabase userDatabase = FBUserDatabase.newInstance(context, uid);
                    userDatabase.removeFootballEvent(eid);
                    Log.d(TAG, "game has been removed: " + eid);
                }
                if (gamesIterator.hasNext()) {
                    processData(gamesIterator.next());
                } else {
                    callback.onSuccess(gameList);
                }
            }

            @Override
            public void onFailed() {

            }
        }, eid);
    }


}
