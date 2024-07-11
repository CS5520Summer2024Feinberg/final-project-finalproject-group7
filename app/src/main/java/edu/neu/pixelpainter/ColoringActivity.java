package edu.neu.pixelpainter;

import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ColoringActivity extends AppCompatActivity {

    private SeekBar colorSlider;
    private GridView gridCanvas;
    private Button buttonUndo;
    private Button buttonRedo;
    private Button buttonEraser;
    private Button buttonSave;
    private Button buttonClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coloring);

        // Initialize views
        colorSlider = findViewById(R.id.color_slider);
        gridCanvas = findViewById(R.id.grid_canvas);
        buttonUndo = findViewById(R.id.button_undo);
        buttonRedo = findViewById(R.id.button_redo);
        buttonEraser = findViewById(R.id.button_eraser);
        buttonSave = findViewById(R.id.button_save);
        buttonClear = findViewById(R.id.button_clear);

        // Placeholder functionality for buttons
        buttonUndo.setOnClickListener(v -> showToast("Undo clicked"));
        buttonRedo.setOnClickListener(v -> showToast("Redo clicked"));
        buttonEraser.setOnClickListener(v -> showToast("Eraser clicked"));
        buttonSave.setOnClickListener(v -> showToast("Save clicked"));
        buttonClear.setOnClickListener(v -> showToast("Clear clicked"));

        // Placeholder functionality for color slider
        colorSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Placeholder for color change
                showToast("Color changed to " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do something when touch starts
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do something when touch stops
            }
        });

        // Placeholder for grid canvas setup
        // You can set up an adapter or other logic for the grid view here
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
