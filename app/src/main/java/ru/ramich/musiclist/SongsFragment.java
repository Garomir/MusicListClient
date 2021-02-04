package ru.ramich.musiclist;

import android.app.ProgressDialog;
import android.content.Context;
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

public class SongsFragment extends Fragment {

    public ProgressDialog dialog;

    ListView listView;

    Retrofit retrofit = new  Retrofit.Builder()
            .baseUrl("http://garomir.hopto.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    JSONPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JSONPlaceHolderApi.class);

    private List<Song> mySongs = new ArrayList<>();
    private SongsAdapter myAdapter;
    ArrayList<Integer> listId = new ArrayList<>();
    ArrayList<String> listName = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.songs_fragment, container, false);

        listView = view.findViewById(R.id.lvSongs);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Song someSong = (Song) myAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), PlayerActivity.class);
            intent.putExtra("songId", someSong.getId());
            intent.putExtra("songName", someSong.getName());
            intent.putIntegerArrayListExtra("listId", listId);
            intent.putStringArrayListExtra("listName", listName);
            startActivity(intent);
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mySongs.clear();
        createDialog();
        new Thread(() -> {
            getAllSongs();
            dialog.dismiss();
        }).start();
    }

    public void createDialog(){
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Загрузка");
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.show();
    }

    private void getAllSongs(){
        Call<List<Song>> notes = jsonPlaceHolderApi.getAllSongs();

        notes.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                mySongs.clear();
                mySongs = response.body();
                fillListView(mySongs);
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void fillListView(List<Song> mySongs){
        myAdapter = new SongsAdapter(mySongs);
        listView.setAdapter(myAdapter);
        listId = getListId(mySongs);
        listName = getListName(mySongs);
    }

    public ArrayList<Integer> getListId(List<Song> mySongs){
        for (int i = 0; i < mySongs.size(); i++){
            listId.add(mySongs.get(i).getId());
        }
        return listId;
    }

    public ArrayList<String> getListName(List<Song> mySongs){
        for (int i = 0; i < mySongs.size(); i++){
            listName.add(mySongs.get(i).getName());
        }
        return listName;
    }
}
