package ru.ramich.musiclist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.Thread.sleep;

public class ArtistsFragment extends Fragment {

    ListView lvArtists;

    Retrofit retrofit = new  Retrofit.Builder()
            .baseUrl("http://garomir.hopto.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    JSONPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JSONPlaceHolderApi.class);

    List<Artist> myArtists = new ArrayList<>();
    ArtistsAdapter myAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.artists_fragment, container, false);

        lvArtists = view.findViewById(R.id.lvArtists);
        lvArtists.setOnItemClickListener((parent, view1, position, id) -> {
            Artist artist = (Artist) myAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), AlbumsByArtistActivity.class);
            intent.putExtra("artistId", artist.getId());
            startActivity(intent);
        });

        getAllArtists();

        return view;
    }

    public void getAllArtists(){
        Call<List<Artist>> notes = jsonPlaceHolderApi.getAllArtists();

        notes.enqueue(new Callback<List<Artist>>() {
            @Override
            public void onResponse(Call<List<Artist>> call, Response<List<Artist>> response) {
                myArtists.clear();
                myArtists = response.body();
                fillListView(myArtists);
            }

            @Override
            public void onFailure(Call<List<Artist>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void fillListView(List<Artist> myArtists){
        myAdapter = new ArtistsAdapter(myArtists);
        lvArtists.setAdapter(myAdapter);
    }
}
