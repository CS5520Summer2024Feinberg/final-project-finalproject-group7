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

public class SignUpActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button signUpButton = findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            if (!username.isEmpty() && !password.isEmpty()) {
                checkUsernameExists(username, password);
            } else {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUsernameExists(String username, String password) {
        mDatabase.child("users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Username already exists
                    Toast.makeText(SignUpActivity.this, "Username already taken. Please choose another.", Toast.LENGTH_SHORT).show();
                } else {
                    // Username does not exist, proceed with sign-up
                    signUp(username, password);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                Toast.makeText(SignUpActivity.this, "Error checking username. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signUp(String username, String password) {
        // Create a new user with a 'process' subclass
        User user = new User(password);
        mDatabase.child("users").child(username).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Sign up failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onLoginLinkClicked(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    // User class with a 'process' subclass
    public static class User {
        public String password;
        public Process process;

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
