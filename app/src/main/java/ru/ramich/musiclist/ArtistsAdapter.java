package ru.ramich.musiclist;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ArtistsAdapter extends BaseAdapter {

    private List<Artist> myArtists;

    public ArtistsAdapter(List<Artist> myArtists) {
        super();
        this.myArtists = myArtists;
    }

    @Override
    public int getCount() {
        return myArtists.size();
    }

    @Override
    public Object getItem(int position) {
        return myArtists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return myArtists.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) v = View.inflate(parent.getContext(), R.layout.list_item, null);
        TextView txt1 = v.findViewById(R.id.tvListItem);
        txt1.setText(myArtists.get(position).getName());
        return v;
    }
}
