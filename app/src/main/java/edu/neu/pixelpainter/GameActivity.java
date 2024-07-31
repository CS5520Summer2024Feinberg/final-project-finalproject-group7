package edu.neu.pixelpainter;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private PixelCanvasView pixelCanvasView;
    private View colorDisplay;
    private int[] colors = {Color.RED, 0xFF21e01f, Color.BLUE, 0xFFebb914, Color.CYAN, 0xFFdc939d, Color.LTGRAY, Color.DKGRAY, Color.BLACK, Color.GRAY};
    private int[] colorNumbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}; // Corresponding numbers for each color
    private boolean eraseMode = false;
    private int level;
    private String username;

    private int processing;
    private boolean isMusicEnabled;
    private static final String KEY_ERASE_MODE = "erase_mode";
    private static final String KEY_SELECTED_COLOR = "selected_color";
    private static final String KEY_LEVEL = "level";

    private MusicService musicService;
    private boolean isBound = false;

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    private void updateProcessingField(String username, int newLevel) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(username);
        databaseReference.child("processing").setValue(newLevel);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Get the level from the intent
        Intent intent = getIntent();
        level = intent.getIntExtra("level", 1);
        username = intent.getStringExtra("username");
        processing = intent.getIntExtra("processing", 1);
        int maxLevel = intent.getIntExtra("maxLevel", 3);

        isMusicEnabled = intent.getBooleanExtra("isMusicEnabled", false);

        Log.i("onCreate", String.valueOf(level));
        pixelCanvasView = findViewById(R.id.pixelCanvas);
        pixelCanvasView.setLevel(level);

        colorDisplay = findViewById(R.id.colorDisplay);
        TextView tv = findViewById(R.id.level);
        tv.setText("Level: " + level);
        LinearLayout paletteLayout = findViewById(R.id.paletteLayout);

        for (int i = 0; i < colors.length; i++) {
            final int color = colors[i];
            final int number = colorNumbers[i];
            Button colorButton = new Button(this);
            colorButton.setBackgroundColor(color);
            colorButton.setText(String.valueOf(number));
            colorButton.setTextColor(Color.WHITE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100); // Fixed size for buttons
            params.setMargins(10, 10, 10, 10); // Margin around buttons
            colorButton.setLayoutParams(params);
            colorButton.setOnClickListener(v -> {
                pixelCanvasView.setSelectedColor(color);
                colorDisplay.setBackgroundColor(color);
                pixelCanvasView.setEraseMode(false);
            });
            paletteLayout.addView(colorButton);
        }

        Button eraseButton = findViewById(R.id.eraseButton);
        eraseButton.setOnClickListener(v -> {
            eraseMode = true;
            pixelCanvasView.setEraseMode(true);
            colorDisplay.setBackgroundResource(R.drawable.erase);
        });

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            float correctRatio = pixelCanvasView.getCorrectColorRatio(colorNumbers, colors);

            if (correctRatio >= 0.01) {
                int newLevel = level + 1;

                // Update the processing field in Firebase if the username is not null and newLevel is greater than current processing
                if (username != null && newLevel > processing) {
                    updateProcessingField(username, newLevel);
                }

                if (newLevel > maxLevel && username != null) {
                    // Show congratulations message and return to previous menu
                    AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                    builder.setTitle("Congratulations")
                            .setMessage("You have completed all levels!")
                            .setPositiveButton("OK", (dialog, which) -> {
                                dialog.dismiss();
                                // Return to previous menu
                                Intent mainMenuIntent = new Intent(GameActivity.this, MainActivity.class);
                                mainMenuIntent.putExtra("username", username);
                                mainMenuIntent.putExtra("processing", newLevel > processing ? newLevel : processing);
                                mainMenuIntent.putExtra("isMusicEnabled", isMusicEnabled);
                                startActivity(mainMenuIntent);
                                finish();
                            })
                            .show();
                }
                // use not login
                else if (username == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                    builder.setTitle("Congratulations!");
                    builder.setMessage("You have completed this level!Please login to continue.");

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(GameActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
               else {
                    // Proceed to the next level or random level in freestyle mode
                    Intent gameIntent = new Intent(GameActivity.this, GameActivity.class);
                    gameIntent.putExtra("level",newLevel);
                    gameIntent.putExtra("username", username); // Pass the username
                    gameIntent.putExtra("processing", newLevel > processing ? newLevel : processing); // Pass the updated processing value
                    gameIntent.putExtra("maxLevel", maxLevel); // passing the max level of the game

                    gameIntent.putExtra("isMusicEnabled", isMusicEnabled);
                    startActivity(gameIntent);
                    finish();
               }

            } else {
                Toast.makeText(GameActivity.this, "Nice Job! Try to do better!", Toast.LENGTH_SHORT).show();

                // Add this block to handle vibration
                SharedPreferences preferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
                boolean isVibrationEnabled = preferences.getBoolean("vibration", false);
                if (isVibrationEnabled) {
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator != null && vibrator.hasVibrator()) {
                        vibrator.vibrate(500); // Vibrate for 500 milliseconds
                    }
                }
            }
        });

        if (savedInstanceState != null) {
            eraseMode = savedInstanceState.getBoolean(KEY_ERASE_MODE, false);
            int selectedColor = savedInstanceState.getInt(KEY_SELECTED_COLOR, Color.WHITE);

            pixelCanvasView.setSelectedColor(selectedColor);
            pixelCanvasView.setEraseMode(eraseMode);
            pixelCanvasView.setLevel(savedInstanceState.getInt(KEY_LEVEL, 1));

            if (eraseMode) {
                colorDisplay.setBackgroundResource(R.drawable.erase);
            } else {
                colorDisplay.setBackgroundColor(selectedColor);
            }
        }

        // Add this block to handle background music
        if (isMusicEnabled) {
            Intent musicIntent = new Intent(this, MusicService.class);
            bindService(musicIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(musicIntent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_ERASE_MODE, eraseMode);
        outState.putInt(KEY_SELECTED_COLOR, pixelCanvasView.getSelectedColor());
        outState.putInt(KEY_LEVEL, pixelCanvasView.getLevel());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(musicConnection);
            isBound = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isBound) {
            unbindService(musicConnection);
            isBound = false;
        }
        stopService(new Intent(this, MusicService.class));
    }

    @Override
    public void onBackPressed() {
        // Create a new Intent to launch MainActivity
        Intent intent = new Intent(GameActivity.this, MainActivity.class);
        intent.putExtra("username", username); // Pass the username
        intent.putExtra("processing", processing); // Pass the updated processing value
        intent.putExtra("isMusicEnabled", isMusicEnabled); // Pass the music setting
        intent.putExtra("level", level);

        // Start the new activity
        startActivity(intent);
        // Optionally, you can call the super method if you want the default back button behavior
        super.onBackPressed();
    }
}
