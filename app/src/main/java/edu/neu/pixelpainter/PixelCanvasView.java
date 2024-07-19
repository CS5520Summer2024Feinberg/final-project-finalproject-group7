package edu.neu.pixelpainter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PixelCanvasView extends View {

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

        pixels = new int[gridSize][gridSize];
        backgroundNumbers = new int[gridSize][gridSize];
//        init(attrs);
//        generateBackgroundNumbers();
    }
//    private void init(AttributeSet attrs) {
//        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PixelCanvasView);
//        level = a.getInt(R.styleable.PixelCanvasView_level, 0); // Default to 0 if not set
//        a.recycle();
//    }

//    private void generateBackgroundNumbers() {
//        for (int i = 0; i < gridSize; i++) {
//            for (int j = 0; j < gridSize; j++) {
//                backgroundNumbers[i][j] = (i * gridSize + j) % 10 + 1; // Assign numbers 1 to 10 cyclically
//            }
//        }
//    }

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

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                paint.setColor(pixels[i][j]);
                Rect rect = new Rect(i * pixelSize, j * pixelSize, (i + 1) * pixelSize, (j + 1) * pixelSize);
                canvas.drawRect(rect, paint);

                // Draw border
                canvas.drawRect(rect, borderPaint);

                // Draw number
//                if (pixels[i][j] == 0) { // Only draw number if the pixel is not colored
                if (backgroundNumbers[i][j] != 0){
                    canvas.drawText(String.valueOf(backgroundNumbers[i][j]),
                            (i + 0.5f) * pixelSize,
                            (j + 0.75f) * pixelSize,
                            textPaint);
                }

//                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            int width = getWidth();
            int height = getHeight();
            int pixelSize = Math.min(width, height) / gridSize;

            int x = (int) (event.getX() / pixelSize);
            int y = (int) (event.getY() / pixelSize);

            if (x >= 0 && x < gridSize && y >= 0 && y < gridSize) {
                if (eraseMode) {
                    pixels[x][y] = 0; // Reset to default (show number)
                } else {
                    if (selectedColor == Color.WHITE){
                        Toast.makeText(PixelCanvasView.this.getContext(), "Please select a color.", Toast.LENGTH_SHORT).show();
                    }else {
                        pixels[x][y] = selectedColor;
                    }

                }
                invalidate();
            }
            return true;
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
        invalidate();
    }

    public int getLevel() {
        return level;
    }



    public int getCorrectColorCount(int[] colorNumbers, int[] colors) {
        int correctCount = 0;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                int pixelColor = pixels[i][j];
                int backgroundNumber = backgroundNumbers[i][j];
                int index = backgroundNumber - 1; // Background numbers are 1-based, array is 0-based
                if (pixelColor == colors[index]) {
                    correctCount++;
                }
            }
        }
        return correctCount;
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

