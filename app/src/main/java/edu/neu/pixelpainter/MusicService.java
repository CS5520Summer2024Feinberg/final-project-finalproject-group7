package edu.neu.pixelpainter;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import androidx.annotation.Nullable;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.background_music); // Make sure you have a background_music.mp3 file in res/raw
            mediaPlayer.setLooping(true);
        }
        mediaPlayer.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onDestroy();
    }
}
