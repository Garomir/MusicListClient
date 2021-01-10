package ru.ramich.musiclist;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface JSONPlaceHolderApi {

    @GET("songs/{id}")
    public Call<ResponseBody> getSongWithId(@Header("Authorization") String authToken, @Path("id") int id);

    @GET("songs")
    public Call<List<Song>> getAllSongs(@Header("Authorization") String authToken);

    @GET("artist")
    public Call<List<Artist>> getAllArtists(@Header("Authorization") String authToken);

    @GET("artist/{id}")
    public Call<List<Song>> getSongsByArtist(@Path("id") int id);

    @GET("songs/album/{id}")
    public Call<List<Song>> getSongsByAlbum(@Header("Authorization") String authToken, @Path("id") int id);

    @GET("albums/{id}")
    public Call<List<Album>> getAlbumsByArtist(@Header("Authorization") String authToken, @Path("id") int id);

    @GET("albums/img/{id}")
    public Call<ResponseBody> getImg(@Header("Authorization") String authToken, @Path("id") int id);

}

