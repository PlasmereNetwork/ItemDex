package me.drex.itemdex.config;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Config {

    public static Config INSTANCE = new Config();

    public Map<Integer, List<String>> perks = new Int2ObjectArrayMap<>() {{
        put(5, new ArrayList<>() {{
            add("say GG ${player}");
            add("give ${player} minecraft:apple 5");
        }});
        put(10, new ArrayList<>() {{
            add("say GG ${player}");
            add("give ${player} minecraft:diamond 1");
            add("effect give ${player} minecraft:regeneration 30 0");
        }});
    }};

}
