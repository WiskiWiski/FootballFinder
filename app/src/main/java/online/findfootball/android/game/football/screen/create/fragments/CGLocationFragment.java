package online.findfootball.android.game.football.screen.create.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import online.findfootball.android.R;
import online.findfootball.android.app.App;
import online.findfootball.android.game.GameObj;
import online.findfootball.android.game.football.screen.create.BaseCGFragment;
import online.findfootball.android.location.LocationObj;
import online.findfootball.android.location.gmaps.fragments.LocationSelectFragment;


public class CGLocationFragment extends BaseCGFragment {

    private static final String TAG = App.G_TAG + ":CGLocationFrg";

    private LocationSelectFragment locationSelectFragment;

    public CGLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.cg_fragment_location, container, false);
        locationSelectFragment = (LocationSelectFragment) getChildFragmentManager().findFragmentById(R.id.gmap_fragment);
        return rootView;
    }

    private LocationObj getLocation() {
        LocationObj location = null;
        if (locationSelectFragment != null) {
            location = locationSelectFragment.getMarkerLocation();
        } else {
            Log.e(TAG, "getLocation: LocationSelectFragment is null!");
        }
        return location;
    }

    @Override
    public void saveResult(@NonNull Object game) {
        LocationObj l = getLocation();
        ((GameObj) game).setLocation(l);
    }

    @Override
    public void updateView(@NonNull Object game) {
        if (locationSelectFragment != null) {
            locationSelectFragment.setMarkerPosition(((GameObj) game).getLocation());
        }
    }

    @Override
    public boolean verifyData(boolean notifyUser) {
        LocationObj location = getLocation();
        if (location == null) {
            if (notifyUser) {
                Toast.makeText(getContext(), getString(R.string.cg_game_location_frg_location_not_selected),
                        Toast.LENGTH_SHORT).show();
                vibrate();
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean swipeble() {
        return false;
    }
}
