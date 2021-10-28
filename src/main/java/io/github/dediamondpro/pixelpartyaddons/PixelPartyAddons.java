package io.github.dediamondpro.pixelpartyaddons;

import gg.essential.api.EssentialAPI;
import io.github.dediamondpro.pixelpartyaddons.commands.ConfigCommand;
import io.github.dediamondpro.pixelpartyaddons.config.Config;
import io.github.dediamondpro.pixelpartyaddons.handlers.EventHandler;
import io.github.dediamondpro.pixelpartyaddons.spotify.Authenticator;
import io.github.dediamondpro.pixelpartyaddons.spotify.SpotifyApi;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "pixelpartyaddons", name = "Pixel Party Addons", version = "1.0")
public class PixelPartyAddons {
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
