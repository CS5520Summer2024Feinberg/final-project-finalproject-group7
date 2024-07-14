package edu.neu.pixelpainter;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity {

    private PixelCanvasView pixelCanvasView;
    private View colorDisplay;
    private int[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.LTGRAY, Color.DKGRAY, Color.BLACK, Color.GRAY};
    private int[] colorNumbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}; // Corresponding numbers for each color
    private boolean eraseMode = false;

    private static final String KEY_ERASE_MODE = "erase_mode";
    private static final String KEY_SELECTED_COLOR = "selected_color";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        pixelCanvasView = findViewById(R.id.pixelCanvas);
        colorDisplay = findViewById(R.id.colorDisplay);
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
            colorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pixelCanvasView.setSelectedColor(color);
                    colorDisplay.setBackgroundColor(color);
                    pixelCanvasView.setEraseMode(false);
                }
            });
            paletteLayout.addView(colorButton);
        }
        Button eraseButton = findViewById(R.id.eraseButton);
        eraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eraseMode = true;
                pixelCanvasView.setEraseMode(true);
                colorDisplay.setBackgroundResource(R.drawable.erase);
            }
        });

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int correctCount = pixelCanvasView.getCorrectColorCount(colorNumbers, colors);
                Toast.makeText(GameActivity.this, "Correctly colored pixels: " + correctCount, Toast.LENGTH_SHORT).show();
            }
        });

        if (savedInstanceState != null) {
            eraseMode = savedInstanceState.getBoolean(KEY_ERASE_MODE, false);
            Log.i("eraseMode",String.valueOf(eraseMode));
            int selectedColor = savedInstanceState.getInt(KEY_SELECTED_COLOR, Color.WHITE);
            pixelCanvasView.setSelectedColor(selectedColor);
            pixelCanvasView.setEraseMode(eraseMode);
            if (eraseMode) {
                colorDisplay.setBackgroundResource(R.drawable.erase);
            } else {
                colorDisplay.setBackgroundColor(selectedColor);
            }
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_ERASE_MODE, eraseMode);
        outState.putInt(KEY_SELECTED_COLOR, pixelCanvasView.getSelectedColor());
    }


}