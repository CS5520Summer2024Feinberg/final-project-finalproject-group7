package edu.neu.pixelpainter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchBackgroundMusic;
    private Switch switchVibration;
    private Button buttonSaveSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchBackgroundMusic = findViewById(R.id.switch_background_music);
        switchVibration = findViewById(R.id.switch_vibration);
        buttonSaveSettings = findViewById(R.id.button_save_settings);

        // Load saved settings
        SharedPreferences preferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
        switchBackgroundMusic.setChecked(preferences.getBoolean("backgroundMusic", false));
        switchVibration.setChecked(preferences.getBoolean("vibration", false));

        // Save settings when the save button is clicked
        buttonSaveSettings.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("backgroundMusic", switchBackgroundMusic.isChecked());
            editor.putBoolean("vibration", switchVibration.isChecked());
            editor.apply();
            Toast.makeText(SettingsActivity.this, "Settings saved", Toast.LENGTH_SHORT).show();
        });
    }
}
