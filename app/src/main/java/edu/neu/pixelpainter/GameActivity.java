package edu.neu.pixelpainter;

import android.content.Intent;
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
    private int level;

    private static final String KEY_ERASE_MODE = "erase_mode";
    private static final String KEY_SELECTED_COLOR = "selected_color";
    private static final String KEY_LEVEL = "level";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // Get the level from the intent
        level = getIntent().getIntExtra("level", 1);
        Log.i("onCreate",String.valueOf(level));
        pixelCanvasView = findViewById(R.id.pixelCanvas);
        pixelCanvasView.setLevel(level);

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
                float correctRatio = pixelCanvasView.getCorrectColorRatio(colorNumbers, colors);
                Log.i("correctRatio", String.valueOf(correctRatio));
                if (correctRatio >= 0.9){
                    Toast.makeText(GameActivity.this, "Pass!", Toast.LENGTH_SHORT).show();
                    Intent gameIntent = new Intent(GameActivity.this, GameActivity.class);
                    gameIntent.putExtra("level", level+1);
                    startActivity(gameIntent);

                }else {
                    Toast.makeText(GameActivity.this, "Nice Job! Try to do better!", Toast.LENGTH_SHORT).show();
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

        Toast.makeText(this, "Welcome to Level " + level, Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_ERASE_MODE, eraseMode);
        outState.putInt(KEY_SELECTED_COLOR, pixelCanvasView.getSelectedColor());
        outState.putInt(KEY_LEVEL, pixelCanvasView.getLevel());

    }
}
