package com.example.spotifywrapped20;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "e2a23e0823054898a5a37bb17c0bbe0c";
    public static final String REDIRECT_URI = "spotifywrappedricky://auth";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken;
    private Call mCall;

    private LinearLayout loginLayout;
    private TextView profileTextView;
    private RecyclerView rvTopTracks;
    private RecyclerView rvTopArtists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvTopTracks = findViewById(R.id.rvTopTracks);
        rvTopTracks.setLayoutManager(new LinearLayoutManager(this));
        rvTopArtists = findViewById(R.id.rvTopArtists);
        rvTopArtists.setLayoutManager(new LinearLayoutManager(this));
        loginLayout = findViewById(R.id.login_layout);
        profileTextView = findViewById(R.id.response_text_view);

        Button loginButton = findViewById(R.id.login_button);


        loginButton.setOnClickListener(v -> {
            getToken();
        });
    }


    public void getToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
            if (mAccessToken != null) {
                loginLayout.setVisibility(View.GONE);
                fetchUserTopTracks();
                fetchUserTopArtists();
            }
        }
    }


    private void fetchUserTopTracks() {
        if (mAccessToken == null) {
            Toast.makeText(this, "Token is null", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://api.spotify.com/v1/me/top/tracks?limit=5";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("MainActivity", "Failed to fetch data: ", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    final String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONArray items = jsonObject.getJSONArray("items");
                        final List<String> trackNames = new ArrayList<>();
                        for (int i = 0; i < 5; i++) {
                            JSONObject track = items.getJSONObject(i);
                            String name = track.getString("name");
                            trackNames.add(name);
                        }
                        runOnUiThread(() -> updateUI(trackNames));
                    } catch (Exception e) {
                        Log.e("MainActivity", "Failed to parse data: ", e);
                    }
                }
            }
        });
    }

    private void updateUI(List<String> trackNames) {
        TopTracksAdapter adapter = new TopTracksAdapter(trackNames);
        rvTopTracks.setAdapter(adapter);
    }

    private void fetchUserTopArtists() {
    if (mAccessToken == null) {
        Toast.makeText(this, "Token is null", Toast.LENGTH_SHORT).show();
        return;
    }

    String url = "https://api.spotify.com/v1/me/top/artists?limit=5";
    Request request = new Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer " + mAccessToken)
            .build();

    mOkHttpClient.newCall(request).enqueue(new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            Log.e("MainActivity", "Failed to fetch data: ", e);
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            } else {
                final String responseData = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONArray items = jsonObject.getJSONArray("items");
                    final List<String> artistNames = new ArrayList<>();
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject artist = items.getJSONObject(i);
                        String name = artist.getString("name");
                        artistNames.add(name);
                    }
                    runOnUiThread(() -> updateUIWithArtists(artistNames));
                } catch (Exception e) {
                    Log.e("MainActivity", "Failed to parse data: ", e);
                }
            }
        }
    });
}
    private void updateUIWithArtists(List<String> artistNames) {
        ArtistsAdapter adapter = new ArtistsAdapter(artistNames);
        rvTopArtists.setAdapter(adapter);
    }



    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, REDIRECT_URI)
                .setScopes(new String[]{"user-read-email", "user-top-read", "user-read-recently-played"})
                .build();
    }

    @Override
    protected void onDestroy() {
        cancelCall();
        super.onDestroy();
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }
}
