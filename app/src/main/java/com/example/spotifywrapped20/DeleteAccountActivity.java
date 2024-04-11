package com.example.spotifywrapped20;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DeleteAccountActivity extends AppCompatActivity {

    private Button deleteAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        deleteAccountButton = findViewById(R.id.deleteAccountButton);
        deleteAccountButton.setOnClickListener(v -> deleteAccount());
    }

    private void deleteAccount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(DeleteAccountActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(DeleteAccountActivity.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
