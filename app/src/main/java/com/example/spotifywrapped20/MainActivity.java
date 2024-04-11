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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.media.MediaPlayer;

public class MainActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "e2a23e0823054898a5a37bb17c0bbe0c";
    public static final String REDIRECT_URI = "spotifywrappedricky://auth";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken;
    private boolean isTopTrackFetched = false;
    private boolean isTopArtistFetched = false;
    private Call mCall;

    private LinearLayout loginLayout;
    private TextView profileTextView;
    private RecyclerView rvTopTracks;
    private RecyclerView rvTopArtists;
    private int currentTrackIndex = 0;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Button logoutButton;
    private Button accountButton;
    private String topTrack;
    private String topArtist;

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

        logoutButton = findViewById(R.id.logoutButton);
        accountButton = findViewById(R.id.accountButton);

        logoutButton.setOnClickListener(v -> logoutUser());
        accountButton.setOnClickListener(v -> navigateToUpdateUser());


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
                storeSpotifyToken(mAccessToken);
                fetchUserTopTracks();
                fetchUserTopArtists();
            }
        }
    }


    private void storeWrappedSummary(String topTrack, String topArtist) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (user != null) {
            WrappedSummary summary = new WrappedSummary(topTrack, topArtist, new Date());

            db.collection("users")
                    .document(user.getUid())
                    .collection("wrappedSummaries")
                    .add(summary)
                    .addOnSuccessListener(documentReference -> Log.d("MainActivity", "Wrapped Summary stored successfully"))
                    .addOnFailureListener(e -> Log.e("MainActivity", "Error storing Wrapped Summary", e));
        }
    }


    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }



    private void navigateToUpdateUser() {
        Intent intent = new Intent(MainActivity.this, AccountManagementActivity.class);
        startActivity(intent);
    }


    private void fetchUserTopTracks() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Firebase user is null", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore.getInstance().collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String token = documentSnapshot.getString("spotifyToken");
                    if (token == null) {
                        Toast.makeText(MainActivity.this, "Spotify token is null", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String url = "https://api.spotify.com/v1/me/top/tracks?limit=5";
                    Request request = new Request.Builder()
                            .url(url)
                            .addHeader("Authorization", "Bearer " + token)
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
                                    final List<SpotifyItem> trackItems = new ArrayList<>();
                                    for (int i = 0; i < items.length(); i++) {
                                        JSONObject track = items.getJSONObject(i);
                                        String name = track.getString("name");
                                        String previewUrl = track.optString("preview_url", "");

                                        JSONObject album = track.getJSONObject("album");
                                        JSONArray images = album.getJSONArray("images");
                                        String imageUrl = "";
                                        if (images.length() > 0) {
                                            imageUrl = images.getJSONObject(0).getString("url");
                                        }

                                        trackItems.add(new SpotifyItem(name, imageUrl, previewUrl));
                                    }
                                    runOnUiThread(() -> {
                                        updateUI(trackItems);
                                        autoPlayTrackPreviews(trackItems);
                                    });
                                    topTrack = trackItems.get(0).getName();
                                    isTopTrackFetched = true;
                                    tryStoreWrappedSummary();
                                } catch (JSONException e) {
                                    Log.e("MainActivity", "Failed to parse data: ", e);
                                }
                            }
                        }
                    });
                })
                .addOnFailureListener(e -> Log.e("MainActivity", "Error fetching Spotify token", e));
    }


    private void tryStoreWrappedSummary() {
        if (isTopTrackFetched && isTopArtistFetched) {
            storeWrappedSummary(topTrack, topArtist);
        }
    }



    private void updateUI(List<SpotifyItem> trackNames) {
        TopTracksAdapter adapter = new TopTracksAdapter(this, trackNames);
        rvTopTracks.setAdapter(adapter);
    }



    private void playTrackPreview(List<SpotifyItem> trackItems, int trackIndex) {
        if (trackIndex >= trackItems.size()) {
            Log.i("MainActivity", "End of track list reached.");
            return;
        }

        SpotifyItem track = trackItems.get(trackIndex);
        if (track.getPreviewUrl() != null && !track.getPreviewUrl().isEmpty()) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(track.getPreviewUrl());
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());
                mediaPlayer.setOnCompletionListener(mp -> {
                    currentTrackIndex++;
                    playTrackPreview(trackItems, currentTrackIndex);
                });
            } catch (IOException e) {
                Log.e("MainActivity", "Could not play track preview", e);
            }
        } else {
            currentTrackIndex++;
            playTrackPreview(trackItems, currentTrackIndex);
        }
    }

    private void autoPlayTrackPreviews(List<SpotifyItem> trackItems) {
        currentTrackIndex = 0;
        playTrackPreview(trackItems, currentTrackIndex);
    }


    private void storeSpotifyToken(String token) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Map<String, Object> tokenMap = new HashMap<>();
            tokenMap.put("spotifyToken", token);
            FirebaseFirestore.getInstance().collection("users")
                    .document(user.getUid())
                    .set(tokenMap, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d("MainActivity", "Spotify token stored successfully"))
                    .addOnFailureListener(e -> Log.e("MainActivity", "Error storing Spotify token", e));
        }
    }



    private void fetchUserTopArtists() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Firebase user is null", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore.getInstance().collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String token = documentSnapshot.getString("spotifyToken");
                    if (token == null) {
                        Toast.makeText(MainActivity.this, "Spotify token is null", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String url = "https://api.spotify.com/v1/me/top/artists?limit=5";
                    Request request = new Request.Builder()
                            .url(url)
                            .addHeader("Authorization", "Bearer " + token)
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
                                    final List<SpotifyItem> artistItems = new ArrayList<>();
                                    for (int i = 0; i < items.length(); i++) {
                                        JSONObject artist = items.getJSONObject(i);
                                        String name = artist.getString("name");
                                        if (i == 0) {
                                            topArtist = name;
                                        }
                                        JSONArray images = artist.getJSONArray("images");
                                        String imageUrl = images.getJSONObject(0).getString("url");
                                        artistItems.add(new SpotifyItem(name, imageUrl, ""));
                                    }
                                    runOnUiThread(() -> updateUIWithArtists(artistItems));
                                    isTopArtistFetched = true;
                                    tryStoreWrappedSummary();
                                } catch (Exception e) {
                                    Log.e("MainActivity", "Failed to parse data: ", e);
                                }
                            }
                        }
                    });
                })
                .addOnFailureListener(e -> Log.e("MainActivity", "Error fetching Spotify token", e));
    }

    private void updateUIWithArtists(List<SpotifyItem> artistNames) {
        ArtistsAdapter adapter = new ArtistsAdapter(this, artistNames);
        rvTopArtists.setAdapter(adapter);
    }



    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, REDIRECT_URI)
                .setScopes(new String[]{"user-read-email", "user-top-read", "user-read-recently-played"})
                .setShowDialog(true)
                .build();
    }


    @Override
    protected void onDestroy() {
        cancelCall();
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }
}
