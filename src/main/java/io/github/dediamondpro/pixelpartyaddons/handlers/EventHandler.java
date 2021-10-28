package io.github.dediamondpro.pixelpartyaddons.handlers;

import io.github.dediamondpro.pixelpartyaddons.PixelPartyAddons;
import io.github.dediamondpro.pixelpartyaddons.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Locale;
import java.util.regex.Pattern;

public class EventHandler {
    public static boolean isInPixelParty = false;
    public static boolean playing = false;
    public static boolean started = false;
    private static final Pattern startPattern = Pattern.compile(" +Pixel Party");

    @SubscribeEvent
    public void OnTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().theWorld == null) return;
        ScoreObjective scoreObjective = Minecraft.getMinecraft().theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
        if (scoreObjective != null) {
            String title = scoreObjective.getDisplayName().replaceAll("(?i)\\u00A7.", "").toLowerCase(Locale.ROOT);
            isInPixelParty = title.equals("pixel party");
        }
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (!isInPixelParty || PixelPartyAddons.spotifyApi == null) return;
        switch (event.type) {
            case 0:
                if (startPattern.matcher(event.message.getUnformattedText()).matches() && !started && Config.autoSkip) {
                    PixelPartyAddons.spotifyApi.skip();
                    started = true;
                    System.out.println("skip");
                }
                break;
            case 2:
                if (event.message.getUnformattedText().contains("FREEZE") && playing && Config.playPause) {
                    PixelPartyAddons.spotifyApi.pause();
                    playing = false;
                    System.out.println("pause");
                } else if (!playing && Config.playPause) {
                    PixelPartyAddons.spotifyApi.play();
                    playing = true;
                    System.out.println("play");
                }
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        started = false;
    }
}
