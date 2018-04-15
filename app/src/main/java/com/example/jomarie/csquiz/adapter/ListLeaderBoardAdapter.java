package com.example.jomarie.csquiz.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.jomarie.csquiz.R;
import com.example.jomarie.csquiz.model.Leaderboardmodel;

import java.util.List;

/**
 * Created by jomarie on 7/24/2017.
 */

public class ListLeaderBoardAdapter extends BaseAdapter {
    private Context mContext;
    private List<Leaderboardmodel> mLeaderboardList;

    public ListLeaderBoardAdapter(Context mContext, List<Leaderboardmodel> mLeaderboardList) {
        this.mContext = mContext;
        this.mLeaderboardList = mLeaderboardList;
    }

    @Override
    public int getCount() {
        if(mLeaderboardList.size() > 10){
            return 10;
        } else{
            return mLeaderboardList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return mLeaderboardList.get(position);
    }

    @Override
    public long getItemId(int positon) {
        return mLeaderboardList.get(positon).getId();
    }

    @Override
    public View getView(int position, View covertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.normal_listview, null);
        TextView name = (TextView)v.findViewById(R.id.player_name);
        TextView date = (TextView)v.findViewById(R.id.date);
        TextView score = (TextView)v.findViewById(R.id.player_score);
        ImageView place = (ImageView)v.findViewById(R.id.place);

        name.setText(mLeaderboardList.get(position).getName());
        date.setText(mLeaderboardList.get(position).getDate());
        score.setText(String.valueOf(mLeaderboardList.get(position).getScore()));

        switch (position) {
            case 0:
                place.setImageResource(R.drawable.img_first);
                break;
            case 1:
                place.setImageResource(R.drawable.img_second);
                break;
            case 2:
                place.setImageResource(R.drawable.img_third);
                break;
            case 3:
                place.setImageResource(R.drawable.img_fourth);
                break;
            case 4:
                place.setImageResource(R.drawable.img_fifth);
                break;
            case 5:
                place.setImageResource(R.drawable.img_sixth);
                break;
            case 6:
                place.setImageResource(R.drawable.img_seventh);
                break;
            case 7:
                place.setImageResource(R.drawable.img_eigth);
                break;
            case 8:
                place.setImageResource(R.drawable.img_ninth);
                break;
            case 9:
                place.setImageResource(R.drawable.img_tenth);
                break;
            default:

        }
        return v;
    }
}
