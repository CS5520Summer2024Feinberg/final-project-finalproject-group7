package edu.neu.pixelpainter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            if (!username.isEmpty() && !password.isEmpty()) {
                login(username, password);
            } else {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login(String username, String password) {
        mDatabase.child("users").child(username).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DataSnapshot userSnapshot = task.getResult();
                String storedPassword = userSnapshot.child("password").getValue(String.class);
                if (storedPassword.equals(password)) {
                    // Retrieve all data belonging to the user
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("password", user.password);
                        intent.putExtra("processData", user.process.data);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Username does not exist", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onSignUpLinkClicked(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
        finish();
    }

    // User class with a 'process' subclass
    public static class User {
        public String password;
        public Process process;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String password) {
            this.password = password;
            this.process = new Process();
        }
    }

    // Process subclass
    public static class Process {
        public String data;

        public Process() {
            this.data = "default data"; // Initialize with some default data
        }
    }
}
