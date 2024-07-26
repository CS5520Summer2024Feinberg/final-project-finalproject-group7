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
        // Create a new user with a 'processing' field
        User user = new User(password, 1); // Initialize with default processing value 0
        mDatabase.child("users").child(username).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                intent.putExtra("username", username); // Pass the username
                intent.putExtra("processing", 1); // Pass the updated processing value
                // Start the new activity
                startActivity(intent);
            } else {
                Toast.makeText(this, "Sign up failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onLoginLinkClicked(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    // User class with a 'processing' field
    public static class User {
        public String password;
        public int processing;

        public User(String password, int processing) {
            this.password = password;
            this.processing = processing;
        }
    }
}
