package io.github.dediamondpro.pixelparty;

import gg.essential.api.EssentialAPI;
import io.github.dediamondpro.pixelparty.commands.ConfigCommand;
import io.github.dediamondpro.pixelparty.config.Config;
import io.github.dediamondpro.pixelparty.handlers.EventHandler;
import io.github.dediamondpro.pixelparty.spotify.Authenticator;
import io.github.dediamondpro.pixelparty.spotify.SpotifyApi;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "pixelparty", name = "Pixel Party", version = "1.0")
public class PixelParty {
    public static SpotifyApi spotifyApi = null;
    public static final Config config = new Config();

    @Mod.EventHandler
    public void onFMLInitialization(FMLInitializationEvent event) {
        config.preload();

        if(!Config.refreshToken.equals(""))
            Authenticator.refresh(Config.refreshToken);

        EssentialAPI.getCommandRegistry().registerCommand(new ConfigCommand());
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }
}
