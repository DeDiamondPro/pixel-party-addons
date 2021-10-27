package io.github.dediamondpro.pixelparty.config;

import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;
import io.github.dediamondpro.pixelparty.spotify.Authenticator;

import java.io.File;

public class Config extends Vigilant {
    @Property(
            type = PropertyType.BUTTON,
            name = "Link Spotify",
            category = "Settings",
            description = "Link your spotify account to the mod so the mod can pause, play and skip songs.")
    private static void execute() {
        Authenticator.authorize();
    }

    @Property(
            type = PropertyType.TEXT,
            hidden = true,
            name = "refresh token",
            category = "settings",
            protectedText = true
    )
    public static String refreshToken = "";

    @Property(
            type = PropertyType.SWITCH,
            name = "Play/Pause",
            category = "Settings",
            description = "Automatically play and pause the current playing song when the blocks appear/disappear."
    )
    public static boolean playPause = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Skip at start",
            category = "Settings",
            description = "Automatically skip the current song and start playing the next one at the start of a game."
    )
    public static boolean autoSkip = true;

    public Config() {
        super(new File("./config/PixelPartyAddons.toml"), "Pixel Party Addons");
        initialize();
    }
}
