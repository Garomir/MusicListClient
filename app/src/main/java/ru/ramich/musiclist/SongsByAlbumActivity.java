package ru.ramich.musiclist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.lang.Thread.sleep;

public class SongsByAlbumActivity extends AppCompatActivity {

    public ProgressDialog dialog;

    ImageView iv;
    Bitmap logo;

    ListView lvSongsByAlbum;
    int albumId;

    Retrofit retrofit = new  Retrofit.Builder()
            .baseUrl("http://garomir.hopto.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    JSONPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JSONPlaceHolderApi.class);
    private static final String TOKEN_PREF_NAME = "savedToken";

    private List<Song> mySongs = new ArrayList<>();
    private SongsAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_by_album);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            albumId = extras.getInt("albumId");
        }

        iv = findViewById(R.id.imageView);
        lvSongsByAlbum = findViewById(R.id.lvSongsByAlbum);

        lvSongsByAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song someSong = (Song) myAdapter.getItem(position);
                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                intent.putExtra("songId", someSong.getId());
                intent.putExtra("songName", someSong.getName());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mySongs.clear();
        createDialog();
        new Thread(() -> {
            getImg(albumId);
            getSongsByAlbum(albumId);
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

    //метод использующий retrofit для загрузки с сервера списка песен по ID альбома
    private void getSongsByAlbum(int albumId) {
        Call<List<Song>> songs = jsonPlaceHolderApi.getSongsByAlbum(getTokenFromPref(), albumId);

        songs.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                mySongs.clear();
                mySongs = response.body();
                fillListView(mySongs);
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    //метод использующий retrofit для загрузки с сервера картинки альбома
    private void getImg(int imgId) {
        Call<ResponseBody> img = jsonPlaceHolderApi.getImg(getTokenFromPref(), imgId);

        img.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                InputStream is = response.body().byteStream();
                logo = BitmapFactory.decodeStream(is);
                iv.setImageBitmap(logo);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    //заполняем ListView через адаптер из списка песен List<Song>
    public void fillListView(List<Song> mySongs){
        myAdapter = new SongsAdapter(mySongs);
        lvSongsByAlbum.setAdapter(myAdapter);
    }

    //вытаскиваем наш токен("token") из существующего SharedPreferences (TOKEN_PREF_NAME)
    public String getTokenFromPref(){
        SharedPreferences sharedPreferences = getSharedPreferences(TOKEN_PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.getString("token", "token");
    }
}