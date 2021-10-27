package io.github.dediamondpro.pixelparty.spotify;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.dediamondpro.pixelparty.PixelParty;
import io.github.dediamondpro.pixelparty.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.codec.digest.DigestUtils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;

public class Authenticator {
    private static final String clientId = "1918e087473c4c0382e4133a232e92b9";
    private static String codeChallenge;
    public static boolean triedRefresh = false;

    public static void authorize() {
        int length = (int) Math.floor(43 + Math.random() * 85);
        char[] charSet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(charSet[(int) (Math.random() * charSet.length)]);
        }
        codeChallenge = builder.toString();
        byte[] bytes = DigestUtils.sha256(codeChallenge);
        String codeChallengeSha = Base64.getEncoder().encodeToString(bytes).replace("+", "-").replace("/", "_")
                .replace("=", "");
        ServerHandler.init();
        String spotifyLink = "https://accounts.spotify.com/authorize?client_id=" + clientId +
                "&response_type=code&redirect_uri=http%3A%2F%2Flocalhost:7853%2Fcallback%2F&scope=user-modify-playback-state" +
                "&code_challenge_method=S256&code_challenge=" + codeChallengeSha;
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(spotifyLink));
            } catch (IOException | URISyntaxException ignored) {
            }
        }
        if (Minecraft.getMinecraft().thePlayer != null)
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Please click here to link spotify.")
                    .setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, spotifyLink)).setChatHoverEvent(
                            new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to open!")))));
    }

    public static void getTokens(String code) {
        String data = "grant_type=authorization_code&code=" + code + "&redirect_uri=http%3A%2F%2Flocalhost:7853%2Fcallback%2F&client_id=" + clientId +
                "&code_verifier=" + codeChallenge;
        JsonObject json = makeRequest(data);
        if (json == null) {
            System.out.println("Failed to get tokens");
            return;
        }
        System.out.println("Got tokens");
        PixelParty.spotifyApi = new SpotifyApi(json.get("access_token").getAsString());
        Config.refreshToken = json.get("refresh_token").getAsString();
        PixelParty.config.markDirty();
        PixelParty.config.writeData();

        if (Minecraft.getMinecraft().thePlayer != null)
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW
                    + "Successfully linked your spotify account!"));
    }

    public static void refresh(String refreshToken) {
        if(triedRefresh) return;
        triedRefresh = true;
        String data = "grant_type=refresh_token&refresh_token=" + refreshToken + "&client_id=" + clientId;
        JsonObject json = makeRequest(data);
        if (json == null)
            return;
        triedRefresh = false;
        PixelParty.spotifyApi = new SpotifyApi(json.get("access_token").getAsString());
        if (json.has("refresh_token")) {
            Config.refreshToken = json.get("refresh_token").getAsString();
            PixelParty.config.markDirty();
            PixelParty.config.writeData();
        }
    }

    private static JsonObject makeRequest(String data) {
        try {
            URL url = new URL("https://accounts.spotify.com/api/token");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("charset", "utf-8");
            con.setRequestProperty("Content-Length", Integer.toString(data.length()));
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(data.getBytes());
            }
            if (con.getResponseCode() != 200) {
                System.out.println(con.getResponseCode());
                System.out.println(con.getResponseMessage());
                return null;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            JsonParser parser = new JsonParser();
            return parser.parse(content.toString()).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
