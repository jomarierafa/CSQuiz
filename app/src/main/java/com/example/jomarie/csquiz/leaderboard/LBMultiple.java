package com.example.jomarie.csquiz.leaderboard;

/**
 * Created by jomarie on 7/23/2017.
 */

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


import com.example.jomarie.csquiz.MainActivity;
import com.example.jomarie.csquiz.R;
import com.example.jomarie.csquiz.adapter.ListLeaderBoardAdapter;
import com.example.jomarie.csquiz.model.Leaderboardmodel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class LBMultiple extends Fragment{
    private ListView lvLeaderboardlist;
    private TextView emptyNoif;
    private ListLeaderBoardAdapter adapter;
    private ArrayList<Leaderboardmodel> mLeaderboardlist = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.tab_multiple, container, false);
        lvLeaderboardlist = rootView.findViewById(R.id.listview_normal);
        emptyNoif = (TextView) rootView.findViewById(R.id.empty);

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getActivity().getApplicationContext());
        Gson gson = new Gson();
        String tmp = "";
        if(MainActivity.leaderboardType.equalsIgnoreCase("survival")){
            tmp = "MyObject";
        }else{
            tmp = "time";
        }
        String json = appSharedPrefs.getString(tmp, "");
        if(json.isEmpty()){
            mLeaderboardlist = new ArrayList<Leaderboardmodel>();
        }else {
            Type type = new TypeToken<ArrayList<Leaderboardmodel>>() {
            }.getType();
            mLeaderboardlist = gson.fromJson(json, type);
        }
        Collections.sort(mLeaderboardlist);
        adapter = new ListLeaderBoardAdapter(getActivity(), mLeaderboardlist);
        lvLeaderboardlist.setAdapter(adapter);

        if(adapter.isEmpty()){
            Typeface textFont = Typeface.createFromAsset(this.getActivity().getAssets(), "freedom.ttf");
            emptyNoif.setTypeface(textFont);
            emptyNoif.setVisibility(View.VISIBLE);
        }

        return rootView;


    }



}
