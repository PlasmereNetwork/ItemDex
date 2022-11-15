package me.drex.itemdex;

import me.drex.itemdex.command.AppraiseCommand;
import me.drex.itemdex.command.ItemDexCommand;
import me.drex.itemdex.config.ConfigManager;
import me.drex.itemdex.util.ItemDexManager;
import me.drex.itemdex.util.ItemDexPlaceholders;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Itemdex implements DedicatedServerModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("itemdex");

    @Override
    public void onInitializeServer() {
        ConfigManager.load();
        ItemDexPlaceholders.register();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            AppraiseCommand.register(dispatcher);
            ItemDexCommand.register(dispatcher);
        });
        ItemDexManager.load();
    }

    public static BufferedWriter getWriter(File file) throws FileNotFoundException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
    }

    public static BufferedReader getReader(File file) throws FileNotFoundException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
    }
}
