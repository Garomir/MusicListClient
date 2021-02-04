package ru.ramich.musiclist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AlbumsByArtistActivity extends AppCompatActivity {

    public ProgressDialog dialog;

    ListView lvAlbumsByArtist;
    int artistId;

    Retrofit retrofit = new  Retrofit.Builder()
            .baseUrl("http://garomir.hopto.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    JSONPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JSONPlaceHolderApi.class);

    private List<Album> myAlbums = new ArrayList<>();
    private AlbumsAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums_by_artist);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            artistId = extras.getInt("artistId");
        }

        lvAlbumsByArtist = findViewById(R.id.lvAlbumsByArtist);

        lvAlbumsByArtist.setOnItemClickListener((parent, view, position, id) -> {
            Album someAlbum = (Album) myAdapter.getItem(position);
            Intent intent = new Intent(getApplicationContext(), SongsByAlbumActivity.class);
            intent.putExtra("albumId", someAlbum.getId());
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        myAlbums.clear();
        createDialog();
        new Thread(() -> {
            getAlbumsByArtist(artistId);
            dialog.dismiss();
        }).start();
    }

    public void createDialog(){
        dialog = new ProgressDialog(this);
        dialog.setMessage("Загрузка");
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.show();
    }

    private void getAlbumsByArtist(int artistId) {
        Call<List<Album>> albums = jsonPlaceHolderApi.getAlbumsByArtist(artistId);

        albums.enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(Call<List<Album>> call, Response<List<Album>> response) {
                myAlbums.clear();
                myAlbums = response.body();
                fillListView(myAlbums);
            }

            @Override
            public void onFailure(Call<List<Album>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void fillListView(List<Album> myAlbums){
        myAdapter = new AlbumsAdapter(myAlbums);
        lvAlbumsByArtist.setAdapter(myAdapter);
    }
}