package com.example.spotifywrapped20;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AccountManagementActivity extends AppCompatActivity {

    private Button updateInfoButton, deleteAccountButton, goBack;
    private RecyclerView wrappedSummariesRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);

        updateInfoButton = findViewById(R.id.updateInfoButton);
        deleteAccountButton = findViewById(R.id.deleteAccountButton);
        goBack = findViewById(R.id.goBack);
        wrappedSummariesRecyclerView = findViewById(R.id.wrappedSummariesRecyclerView);

        wrappedSummariesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Assuming you have a method to fetch summaries
        fetchWrappedSummaries();

        updateInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccountManagementActivity.this, UpdateUserActivity.class);
            startActivity(intent);
        });

        deleteAccountButton.setOnClickListener(v -> {
            deleteUserAccount();
        });

        goBack.setOnClickListener(v ->{
            Intent intent = new Intent(AccountManagementActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void fetchWrappedSummaries() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.getUid())
                    .collection("wrappedSummaries")
                    .orderBy("timestamp", Query.Direction.DESCENDING) // Assuming there's a "year" field
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<WrappedSummary> summaries = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                WrappedSummary summary = document.toObject(WrappedSummary.class);
                                summaries.add(summary);
                            }
                            updateUIWithWrappedSummaries(summaries);
                        } else {
                            Log.e("AccountManagement", "Error getting documents: ", task.getException());
                        }
                    });
        }
    }

    private void updateUIWithWrappedSummaries(List<WrappedSummary> summaries) {
        WrappedSummaryAdapter adapter = new WrappedSummaryAdapter(this, summaries);
        wrappedSummariesRecyclerView.setAdapter(adapter);
    }




    private void deleteUserAccount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(AccountManagementActivity.this, "Account deleted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AccountManagementActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(AccountManagementActivity.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
