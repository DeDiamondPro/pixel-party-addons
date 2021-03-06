package io.github.dediamondpro.pixelpartyaddons.commands;

import gg.essential.api.EssentialAPI;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import io.github.dediamondpro.pixelpartyaddons.PixelPartyAddons;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;

public class ConfigCommand extends Command {
    @Nullable
    @Override
    public Set<Alias> getCommandAliases() {
        Set<Alias> aliases = new LinkedHashSet<>();
        aliases.add(new Alias("ppa"));
        return aliases;
    }

    @DefaultHandler
    public void handler() {
        EssentialAPI.getGuiUtil().openScreen(PixelPartyAddons.config.gui());
    }

    public ConfigCommand() {
        super("pixelpartyaddons");
    }
}
