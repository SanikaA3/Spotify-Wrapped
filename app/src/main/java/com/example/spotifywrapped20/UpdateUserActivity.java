package com.example.spotifywrapped20;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class UpdateUserActivity extends AppCompatActivity {

    private EditText newEmailEditText, newPasswordEditText;
    private Button updateButton;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        user = FirebaseAuth.getInstance().getCurrentUser();
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        updateButton = findViewById(R.id.updateButton);

        updateButton.setOnClickListener(v -> updateCredentials());
    }

    private void updateCredentials() {
        String newPassword = newPasswordEditText.getText().toString().trim();

        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.isEmpty() || newPassword.length() < 6) {
            newPasswordEditText.setError("Password must be at least 6 characters");
            return;
        }
        updatePassword(newPassword);
    }

    private void updatePassword(String newPassword) {
        user.updatePassword(newPassword).addOnCompleteListener(passwordTask -> {
            if (passwordTask.isSuccessful()) {
                Toast.makeText(UpdateUserActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateUserActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(UpdateUserActivity.this, "Failed to update password: " + passwordTask.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



}
