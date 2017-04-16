package org.blackstork.findfootball.game.info.tabs;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.blackstork.findfootball.R;
import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.game.GameObj;
import org.blackstork.findfootball.game.info.BaseGITab;
import org.blackstork.findfootball.user.PublicUserObj;

/**
 * A simple {@link Fragment} subclass.
 */
public class GIPlayersTab extends BaseGITab {

    private static final String TAG = App.G_TAG + ":GIPlayersTab";


    private GameObj thisGameObj;
    private PublicUserObj thisGameOwner;

    public GIPlayersTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gi_tab_players, container, false);

        return rootView;
    }

    @Override
    public void setData(GameObj game, PublicUserObj gameOwner) {
        this.thisGameObj = game;
    }
}