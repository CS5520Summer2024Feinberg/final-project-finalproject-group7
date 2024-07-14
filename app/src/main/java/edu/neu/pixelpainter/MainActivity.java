package edu.neu.pixelpainter;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    private Button buttonLogin;
    private Button buttonSignup;
    private Button buttonSignout;

    private String username;
    private String password;
    private String processData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        buttonLogin = findViewById(R.id.button_login);
        buttonSignup = findViewById(R.id.button_signup);
        buttonSignout = findViewById(R.id.button_signout);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        processData = intent.getStringExtra("processData");

        // Check if the username is null or empty and display appropriate message
        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Please log in for a better experience", Toast.LENGTH_SHORT).show();
            buttonLogin.setVisibility(View.VISIBLE);
            buttonSignup.setVisibility(View.VISIBLE);
            buttonSignout.setVisibility(View.GONE);
        } else {
            Toast.makeText(this, "Welcome, " + username, Toast.LENGTH_SHORT).show();
            buttonLogin.setVisibility(View.GONE);
            buttonSignup.setVisibility(View.GONE);
            buttonSignout.setVisibility(View.VISIBLE);
        }

        // Set up button click listeners
        buttonLogin.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
        buttonSignup.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, SignUpActivity.class)));
        buttonSignout.setOnClickListener(view -> {
            // Signout logic
            username = null;
            password = null;
            processData = null;
            Toast.makeText(MainActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
            buttonLogin.setVisibility(View.VISIBLE);
            buttonSignup.setVisibility(View.VISIBLE);
            buttonSignout.setVisibility(View.GONE);
        });
        findViewById(R.id.button_adventure_mode).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, GameActivity.class)));
    }
}
