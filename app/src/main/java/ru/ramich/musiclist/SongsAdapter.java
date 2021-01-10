package ru.ramich.musiclist;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SongsAdapter extends BaseAdapter {

    private List<Song> mySongs;

    public SongsAdapter(List<Song> mySongs) {
        super();
        this.mySongs = mySongs;
    }

    @Override
    public int getCount() {
        return mySongs.size();
    }

    @Override
    public Object getItem(int position) {
        return mySongs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mySongs.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) v = View.inflate(parent.getContext(), R.layout.list_item, null);
        TextView txt1 = v.findViewById(R.id.tvListItem);
        txt1.setText(mySongs.get(position).getName());
        return v;
    }
}
