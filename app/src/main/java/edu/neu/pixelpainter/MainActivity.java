package edu.neu.pixelpainter;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");
        String processData = intent.getStringExtra("processData");

        // Log the retrieved data
        Log.d(TAG, "Username: " + username);
        Log.d(TAG, "Password: " + password);
        Log.d(TAG, "Process Data: " + processData);

        // Display a welcome message
        Toast.makeText(this, "Welcome, " + username, Toast.LENGTH_SHORT).show();
        // Set up button click listeners
        findViewById(R.id.button_login).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
        findViewById(R.id.button_signup).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, SignUpActivity.class)));
    }
}
