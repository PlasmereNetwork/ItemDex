package me.drex.itemdex.config;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.drex.itemdex.Itemdex;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;

import static me.drex.itemdex.Itemdex.getReader;
import static me.drex.itemdex.Itemdex.getWriter;

public class ConfigManager {

    public static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();


    private static final File CONFIG = new File(FabricLoader.getInstance().getConfigDir().toFile(), "itemdex.json");

    public static void load() {
        if (!CONFIG.exists()) {
            save();
            return;
        }

        try (BufferedReader reader = getReader(CONFIG)) {
            Config.INSTANCE = GSON.fromJson(reader, Config.class);

        } catch (Exception ex) {
            Itemdex.LOGGER.error("Failed to load config!", ex);
        }

    }

    private static void save() {
        try (BufferedWriter writer = getWriter(CONFIG)) {
            writer.write(GSON.toJson(Config.INSTANCE));
        } catch (Exception ex) {
            Itemdex.LOGGER.error("Failed to save config!", ex);
        }
    }
}
