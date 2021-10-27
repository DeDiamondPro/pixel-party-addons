package io.github.dediamondpro.pixelparty.spotify;

import io.github.dediamondpro.pixelparty.config.Config;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;

public class SpotifyApi {
    private final String accessToken;

    public SpotifyApi(String accessToken) {
        this.accessToken = accessToken;
    }

    public void pause() {
        makeRequest("https://api.spotify.com/v1/me/player/pause", "PUT");
    }

    public void play() {
        makeRequest("https://api.spotify.com/v1/me/player/play", "PUT");
    }

    public void skip() {
        makeRequest("https://api.spotify.com/v1/me/player/next", "POST");
    }

    private void makeRequest(String site, String method) {
        new Thread(() -> {
            try {
                URL url = new URL(site);
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                con.setUseCaches(false);
                con.setDoOutput(true);
                con.setRequestMethod(method);
                con.setRequestProperty("Content-Length", "0");
                con.setRequestProperty("Authorization", "Bearer " + accessToken);
                con.setRequestProperty("Content-Type", "application/json");
                try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                    wr.write("".getBytes());
                }
                if (con.getResponseCode() >= 300) {
                    System.out.println(con.getResponseCode());
                    if (con.getResponseCode() == 401) {
                        System.out.println("attempting to refresh token.");
                        if (!Config.refreshToken.equals(""))
                            Authenticator.refresh(Config.refreshToken);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
