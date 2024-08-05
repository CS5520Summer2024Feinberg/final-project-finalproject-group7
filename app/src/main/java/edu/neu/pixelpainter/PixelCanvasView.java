package edu.neu.pixelpainter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PixelCanvasView extends View implements SensorEventListener{

    private Paint paint;
    private int[][] pixels;
    private int[][] backgroundNumbers;
    private int selectedColor = Color.WHITE; // Default color black
    private int gridSize = 16;
    private Paint borderPaint;
    private Paint textPaint;

    private boolean eraseMode = false;

    private Context context;

    private int level;

    private long downTime;
    private static final long CLICK_THRESHOLD = 500; // Threshold for a click in milliseconds
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private int startX = -1;
    private int startY = -1;

    private static final int MAX_TOUCHES = 3;

    private int touches = 0;

    private long lastUpdateTime = 0;


    private Paint ballPaint;
    private int ballRadius = 20;



    public PixelCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        paint = new Paint();
        borderPaint = new Paint();
        borderPaint.setColor(Color.GRAY);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(30);
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            textPaint.setColor(Color.WHITE);
        } else {
            textPaint.setColor(Color.BLACK);
        }
        pixels = new int[gridSize][gridSize];
        backgroundNumbers = new int[gridSize][gridSize];

        init();



    }
    private void init() {
        ballPaint = new Paint();
        ballPaint.setColor(Color.WHITE);  // Color of the ball
        ballPaint.setStyle(Paint.Style.FILL);
    }
    private void generateBackgroundNumbers() {
        Log.i("generateBackgroundNumbers", String.valueOf(this.level));
        int resId = context.getResources().getIdentifier("level" + this.level, "raw", context.getPackageName());
        try {
            InputStream inputStream = context.getResources().openRawResource(resId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null && row < gridSize) {
                String[] numbers = line.split(",");
                for (int col = 0; col < numbers.length && col < gridSize; col++) {
                    backgroundNumbers[col][row] = Integer.parseInt(numbers[col]);
                }
                row++;
            }
            reader.close();
        } catch (IOException e) {
            Log.e("FileReadError", "Error reading file level" + this.level, e);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int pixelSize = Math.min(width, height) / gridSize;

        // Centering calculations
        int offsetX = (width - pixelSize * gridSize) / 2;
        int offsetY = (height - pixelSize * gridSize) / 2;

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                paint.setColor(pixels[i][j]);
                Rect rect = new Rect(
                        offsetX + i * pixelSize,
                        offsetY + j * pixelSize,
                        offsetX + (i + 1) * pixelSize,
                        offsetY + (j + 1) * pixelSize
                );
                canvas.drawRect(rect, paint);

                // Draw border
                canvas.drawRect(rect, borderPaint);

                // Draw number
                if (backgroundNumbers[i][j] != 0) {
                    canvas.drawText(
                            String.valueOf(backgroundNumbers[i][j]),
                            offsetX + (i + 0.5f) * pixelSize,
                            offsetY + (j + 0.75f) * pixelSize,
                            textPaint
                    );
                }
            }
        }

        // Draw the ball at (startX, startY)
        if (startX != -1 && startY != -1) {
            canvas.drawCircle(offsetX + startX * pixelSize + pixelSize / 2, offsetY + startY * pixelSize + pixelSize / 2, ballRadius, ballPaint);
        }


    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downTime = System.currentTimeMillis();
                return true;

            case MotionEvent.ACTION_UP:
                long upTime = System.currentTimeMillis();
                if (upTime - downTime < CLICK_THRESHOLD) {
                    // This is a click
                    int width = getWidth();
                    int height = getHeight();
                    int pixelSize = Math.min(width, height) / gridSize;

                    int x = (int) (event.getX() / pixelSize);
                    int y = (int) (event.getY() / pixelSize);

                    if (x >= 0 && x < gridSize && y >= 0 && y < gridSize) {
                        if (eraseMode) {
                            pixels[x][y] = 0; // Reset to default (show number)
                        } else {
                            if (selectedColor == Color.WHITE) {
                                Toast.makeText(PixelCanvasView.this.getContext(), "Please select a color.", Toast.LENGTH_SHORT).show();
                            } else {
                                if (this.level >= 6){
                                    //final level
                                    touches += 1;
                                    if (touches == 1){
                                        Toast.makeText(PixelCanvasView.this.getContext(), "Tilt your phone to color pixels!", Toast.LENGTH_SHORT).show();
                                    }
                                    if (touches <= MAX_TOUCHES){
                                        if (getContext() instanceof GameActivity) {
                                            ((GameActivity) getContext()).updateRemainingTouchesNumber(MAX_TOUCHES - touches);
                                        }
                                        pixels[x][y] = selectedColor;
                                        startX = x;
                                        startY = y;
                                    }else {
                                        Toast.makeText(PixelCanvasView.this.getContext(), "Reached the Maximum Touches!\n Tilt your phone to color or SAVE!", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    pixels[x][y] = selectedColor;
                                }
                            }
                        }
                        invalidate();
                    }
                    return true;
                }
                break;

            default:
                break;
        }
        return false;
    }

    public void setSelectedColor(int color) {
        this.selectedColor = color;
    }
    public int getSelectedColor() {
        return selectedColor;
    }

    public void setEraseMode(boolean eraseMode) {
        this.eraseMode = eraseMode;
    }

    public void setLevel(int level) {
        this.level = level;
        generateBackgroundNumbers();
        startSensor();
        invalidate();
    }

    private void startSensor() {
        Log.i("level",String.valueOf(this.level));
        if (this.level >= 6){
            AlertDialog.Builder builder = new AlertDialog.Builder(PixelCanvasView.this.getContext());
            builder.setTitle("Final Challenge!");
            builder.setMessage("Color a pixel to start your trip.");
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public int getLevel() {
        return level;
    }



    public float getCorrectColorRatio(int[] colorNumbers, int[] colors) {
        float count = 0f;
        float correctCount = 0f;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                int pixelColor = pixels[i][j];
                int backgroundNumber = backgroundNumbers[i][j];
                if (backgroundNumber == 0){
                    continue;
                }
                count ++;
                int index = backgroundNumber - 1; // Background numbers are 1-based, array is 0-based
                if (pixelColor == colors[index]) {
                    correctCount++;
                }
            }
        }
        Log.i("correctCount", String.valueOf(correctCount));
        Log.i("count", String.valueOf(count));
        return correctCount /count;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.pixels = pixels;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        pixels = savedState.pixels;
        invalidate();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double x = event.values[0];
        double y = event.values[1];
        double z = event.values[2];

        long currentTime = System.currentTimeMillis();
        double tiltX = Math.atan2(y, z) * (180 / Math.PI);
        double tiltY = Math.atan2(x, z) * (180 / Math.PI);

        // Check if the angle is greater than 30 degrees
        if (Math.abs(tiltX) > 30 || Math.abs(tiltY) > 30) {
            // Check if more than 1 second has passed since the last update
            if (currentTime - lastUpdateTime > 500) {
                lastUpdateTime = currentTime;
                if (startX != -1 && startY != -1) {
                    if (Math.abs(x) > Math.abs(y)) {
                        if (x < 0 && startX < gridSize - 1) {
                            startX++;
                        } else if (x > 0 && startX > 0) {
                            startX--;
                        }
                    } else {
                        if (y < 0 && startY < gridSize - 1) {
                            startY--;
                        } else if (y > 0 && startY > 0) {
                            startY++;
                        }
                    }
                    pixels[startX][startY] = selectedColor;
                    invalidate();
                }
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void stopSensor() {
        sensorManager.unregisterListener(this);

    }


    static class SavedState extends BaseSavedState {
        int[][] pixels;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            pixels = (int[][]) in.readSerializable();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeSerializable(pixels);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}

