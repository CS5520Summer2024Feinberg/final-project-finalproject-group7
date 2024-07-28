package edu.neu.pixelpainter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Button buttonLogin;
    private Button buttonSignup;
    private Button buttonSignout;

    private Button buttonAdvanture;

    private Button buttonFreestyle;
    private Button buttonSettings;

    private String username;

    private String password;

    private int processing;

    private ViewPager2 viewPager2;
    private ViewPagerAdapter viewPagerAdapter;
    private int maxLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        buttonLogin = findViewById(R.id.button_login);
        buttonSignup = findViewById(R.id.button_signup);
        buttonSignout = findViewById(R.id.button_signout);
        buttonAdvanture = findViewById(R.id.button_adventure_mode);
        buttonFreestyle = findViewById(R.id.button_freestyle_mode);
        buttonSettings = findViewById(R.id.button_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        processing = intent.getIntExtra("processing", 1);

        // Check if the username is null or empty and display appropriate message
        boolean isLoggedIn = username != null && !username.isEmpty();

        // password != null when previous activity is login
        if (password != null) {
            Toast.makeText(this, isLoggedIn ? "Welcome, " + username : "Please log in for a better experience", Toast.LENGTH_SHORT).show();
        }

        buttonLogin.setVisibility(isLoggedIn ? View.GONE : View.VISIBLE);
        buttonSignup.setVisibility(isLoggedIn ? View.GONE : View.VISIBLE);
        buttonSignout.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
        buttonAdvanture.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);

        // Set up button click listeners
        buttonLogin.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
        buttonSignup.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, SignUpActivity.class)));
        buttonSignout.setOnClickListener(view -> {
            // Signout logic
            Toast.makeText(MainActivity.this, "Goodbye, " + username, Toast.LENGTH_SHORT).show();
            username = null;
            buttonLogin.setVisibility(View.VISIBLE);
            buttonSignup.setVisibility(View.VISIBLE);
            buttonSignout.setVisibility(View.GONE);
            buttonAdvanture.setVisibility(View.GONE);
        });

        // Set up ViewPager2
        viewPager2 = findViewById(R.id.viewPager);

        List<Integer> images = Arrays.asList(R.drawable.a, R.drawable.b, R.drawable.c);
        List<String> headlines = Arrays.asList("Level1:Ice Cream", "Level2:Love", "Level3:Frog");
        maxLevel = headlines.size();
        List<ViewPagerItem> viewPagerArrayList = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            ViewPagerItem viewPagerItem = new ViewPagerItem(images.get(i), headlines.get(i));
            viewPagerArrayList.add(viewPagerItem);
        }
        viewPagerAdapter = new ViewPagerAdapter(viewPagerArrayList);
        viewPager2.setAdapter(viewPagerAdapter);

        // ViewPager item click listener
        viewPagerAdapter.setOnItemClickListener(new ViewPagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position + 1 > processing && username != null) {
                    Toast.makeText(MainActivity.this, "Need to complete level:" + processing, Toast.LENGTH_LONG).show();
                } else {
                    Intent gameIntent = new Intent(MainActivity.this, GameActivity.class);
                    gameIntent.putExtra("level", position + 1);
                    gameIntent.putExtra("username", username); // Pass username or null
                    gameIntent.putExtra("processing", processing); // Pass the current processing value
                    gameIntent.putExtra("maxLevel", maxLevel);
                    gameIntent.putExtra("isFreestyle", false);
                    SharedPreferences preferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
                    boolean isMusicEnabled = preferences.getBoolean("backgroundMusic", false);
                    gameIntent.putExtra("isMusicEnabled", isMusicEnabled);
                    startActivity(gameIntent);
                }
            }
        });

        // Freestyle button click listener
        buttonFreestyle.setOnClickListener(v -> {
            Intent gameIntent = new Intent(MainActivity.this, GameActivity.class);
            Random random = new Random();
            gameIntent.putExtra("level", random.nextInt(images.size()) + 1);
            gameIntent.putExtra("username", username); // Pass username or null
            gameIntent.putExtra("processing", processing); // Pass the current processing value
            gameIntent.putExtra("maxLevel", maxLevel);
            gameIntent.putExtra("isFreestyle", true);
            SharedPreferences preferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
            boolean isMusicEnabled = preferences.getBoolean("backgroundMusic", false);
            gameIntent.putExtra("isMusicEnabled", isMusicEnabled);
            startActivity(gameIntent);
        });

        // Adventure button click listener
        buttonAdvanture.setOnClickListener(v -> {
            Intent gameIntent = new Intent(MainActivity.this, GameActivity.class);
            gameIntent.putExtra("level", processing);
            gameIntent.putExtra("username", username); // Pass username or null
            gameIntent.putExtra("processing", processing); // Pass the current processing value
            gameIntent.putExtra("maxLevel", maxLevel);
            gameIntent.putExtra("isFreestyle", false);
            SharedPreferences preferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
            boolean isMusicEnabled = preferences.getBoolean("backgroundMusic", false);
            gameIntent.putExtra("isMusicEnabled", isMusicEnabled);
            startActivity(gameIntent);
        });

        buttonSettings.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));
    }
}
