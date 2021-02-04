package ru.ramich.musiclist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chibde.visualizer.CircleVisualizer;
import com.chibde.visualizer.LineVisualizer;
import com.chibde.visualizer.SquareBarVisualizer;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.lang.Thread.sleep;

public class PlayerActivity extends AppCompatActivity {

    public ProgressDialog dialog;

    TextView tvSongName;
    String songName;
    int songId;

    private SeekBar seekbar;
    Handler seekHandler = new Handler();

    private static final int PERMISSION_STORAGE_CODE = 1000;
    private long downloadId;
    Uri uri;
    String url = "http://garomir.hopto.org/songs/";

    MediaPlayer mPlayer = new MediaPlayer();

    SquareBarVisualizer squareBarVisualizer;
    LineVisualizer lineVisualizer;
    CircleVisualizer circleVisualizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        tvSongName = findViewById(R.id.tvSongName);
        seekbar = findViewById(R.id.seekBar);

        squareBarVisualizer = findViewById(R.id.bar_visualizer);
        lineVisualizer = findViewById(R.id.line_visualizer);
        circleVisualizer = findViewById(R.id.circle_visualizer);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            songId = extras.getInt("songId");
            songName = extras.getString("songName");
        }

        tvSongName.setText(songName);

        init(songId);
        requestPermission();
        seekUpdation();
    }

    public void createDialog(){
        dialog = new ProgressDialog(this);
        dialog.setMessage("Загрузка");
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.show();
    }

    private void init(int songId){
        uri = Uri.parse(url + songId);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mPlayer.setDataSource(getApplicationContext(), uri);
            mPlayer.prepareAsync();
            seekbar.setMax(mPlayer.getDuration());
        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }
    }

    private void requestPermission(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                            startAudioVisualizer();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    private void startAudioVisualizer() {
        if (squareBarVisualizer.getVisibility() == View.VISIBLE){
            squareBarVisualizer.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
            squareBarVisualizer.setDensity(65);
            squareBarVisualizer.setGap(2);
            squareBarVisualizer.setPlayer(mPlayer.getAudioSessionId());
        } else if (lineVisualizer.getVisibility() == View.VISIBLE){
            lineVisualizer.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
            lineVisualizer.setStrokeWidth(1);
            lineVisualizer.setPlayer(mPlayer.getAudioSessionId());
        } else if (circleVisualizer.getVisibility() == View.VISIBLE){
            circleVisualizer.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
            circleVisualizer.setRadiusMultiplier(1.2f);
            circleVisualizer.setStrokeWidth(1);
            circleVisualizer.setPlayer(mPlayer.getAudioSessionId());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.stop();
    }

    Runnable run = () -> seekUpdation();

    public void seekUpdation() {
        seekbar.setProgress(mPlayer.getCurrentPosition());
        seekHandler.postDelayed(run, 1000);
    }

    public void onPlay(View view) {
        createDialog();
        new Thread(() -> {
            mPlayer.start();
            dialog.dismiss();
        }).start();
    }

    public void onPause(View view) {
        mPlayer.pause();
    }

    public void onNext(View view) {
    }

    public void onBack(View view) {
    }

    public void onBar(View view) {
        squareBarVisualizer.setVisibility(View.GONE);
        lineVisualizer.setVisibility(View.VISIBLE);
        circleVisualizer.setVisibility(View.GONE);
        startAudioVisualizer();
    }

    public void onLine(View view) {
        squareBarVisualizer.setVisibility(View.GONE);
        lineVisualizer.setVisibility(View.GONE);
        circleVisualizer.setVisibility(View.VISIBLE);
        startAudioVisualizer();
    }

    public void onCircle(View view) {
        squareBarVisualizer.setVisibility(View.VISIBLE);
        lineVisualizer.setVisibility(View.GONE);
        circleVisualizer.setVisibility(View.GONE);
        startAudioVisualizer();
    }

    private void startDownloading() {
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        request.setTitle(songName);
        request.setDescription("Download Image");
        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "" + songName + ".mp3");

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadId = downloadManager.enqueue(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startDownloading();
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.song_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.download:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_STORAGE_CODE);
                    } else {
                        startDownloading();
                    }
                } else {
                    startDownloading();
                }
                break;
            case R.id.cancel:
                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                assert downloadManager != null;
                downloadManager.remove(downloadId);
                break;
        }
        return true;
    }
}