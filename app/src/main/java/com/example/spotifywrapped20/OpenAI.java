package com.example.spotifywrapped20;
import okhttp3.*;

public class OpenAI {

    public static final String ID = "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";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    public void generateText(String prompt, Callback callback) {
        String url = "https://api.openai.com/v1/completions";
        String json = "{\"model\": \"text-davinci-003\", \"prompt\": \"" + prompt + "\"}";

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + ID)
                .build();

        client.newCall(request).enqueue(callback);
    }
}
