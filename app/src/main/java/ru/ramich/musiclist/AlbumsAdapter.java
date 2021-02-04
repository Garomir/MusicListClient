package ru.ramich.musiclist;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class AlbumsAdapter extends BaseAdapter {

    private List<Album> myAlbums;

    public AlbumsAdapter(List<Album> myAlbums) {
        super();
        this.myAlbums = myAlbums;
    }

    @Override
    public int getCount() {
        return myAlbums.size();
    }

    @Override
    public Object getItem(int position) {
        return myAlbums.get(position);
    }

    @Override
    public long getItemId(int position) {
        return myAlbums.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) v = View.inflate(parent.getContext(), R.layout.list_item, null);
        TextView txt1 = v.findViewById(R.id.tvListItem);
        txt1.setText(myAlbums.get(position).getName());
        return v;
    }
}
