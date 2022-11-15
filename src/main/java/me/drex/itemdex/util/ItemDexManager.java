package me.drex.itemdex.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.drex.itemdex.Itemdex;
import me.drex.itemdex.config.Config;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.lang.reflect.Type;
import java.util.*;

import static me.drex.itemdex.Itemdex.getReader;
import static me.drex.itemdex.Itemdex.getWriter;

public class ItemDexManager {

    private static final File DATA = new File(FabricLoader.getInstance().getConfigDir().toFile(), "itemdex-data.json");

    public static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(Item.class, new RegistrySerializer<>(BuiltInRegistries.ITEM))
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    private static final Type DATA_TYPE = new TypeToken<Map<UUID, Set<Item>>>() {
    }.getType();
    public static Map<UUID, Set<Item>> data = new HashMap<>();
    public static List<ItemDexEntry> leaderboard = new ArrayList<>();
    private static boolean dirty = true;

    public static boolean appraise(ServerPlayer player, Item item) {
        Set<Item> items = data.getOrDefault(player.getUUID(), new HashSet<>());
        if (items.contains(item)) return false;
        dirty = true;
        items.add(item);
        int newCount = items.size();
        List<String> commands = Config.INSTANCE.perks.get(newCount);
        if (commands != null) {
            MinecraftServer server = player.getServer();
            for (String command : commands) {
                server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), command.replaceAll("\\$\\{player}", player.getScoreboardName()));
            }
        }
        data.put(player.getUUID(), items);
        return true;
    }

    public static void save() {
        try (BufferedWriter writer = getWriter(DATA)) {
            writer.write(GSON.toJson(data));
        } catch (Exception ex) {
            Itemdex.LOGGER.error("Failed to save data!", ex);
        }
    }

    public static void load() {
        if (!DATA.exists()) {
            return;
        }
        dirty = true;

        try (BufferedReader reader = getReader(DATA)) {
            data = GSON.fromJson(reader, DATA_TYPE);

        } catch (Exception ex) {
            Itemdex.LOGGER.error("Failed to load data!", ex);
        }
    }

    public static List<ItemDexEntry> getLeaderboard() {
        if (dirty) {
            dirty = false;
            return leaderboard = data.entrySet().stream().map(uuidSetEntry -> new ItemDexEntry(uuidSetEntry.getKey(), uuidSetEntry.getValue().size())).sorted(Comparator.comparingInt(ItemDexEntry::amount)).toList();
        } else {
            return leaderboard;
        }
    }


    public record ItemDexEntry(UUID uuid, int amount) {
    }

    private record RegistrySerializer<T>(Registry<T> registry) implements JsonSerializer<T>, JsonDeserializer<T> {
        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                return this.registry.get(ResourceLocation.tryParse(json.getAsString()));
            }
            return null;
        }

        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive("" + this.registry.getKey(src));
        }
    }

}
