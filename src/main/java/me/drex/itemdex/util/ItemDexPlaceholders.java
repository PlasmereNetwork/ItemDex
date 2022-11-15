package me.drex.itemdex.util;

import com.mojang.authlib.GameProfile;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class ItemDexPlaceholders {

    public static void register() {
        Placeholders.register(new ResourceLocation("itemdex", "top"), (context, argument) -> {
            if (argument != null) {
                try {
                    int i = Integer.parseInt(argument);
                    List<ItemDexManager.ItemDexEntry> leaderboard = ItemDexManager.getLeaderboard();
                    if (i <= leaderboard.size() - 1) {
                        ItemDexManager.ItemDexEntry itemDexEntry = leaderboard.get(i);
                        Optional<GameProfile> gameProfile = context.server().getProfileCache().get(itemDexEntry.uuid());
                        Component name = gameProfile.map(ComponentUtils::getDisplayName).orElseGet(() -> Component.literal("Unknown"));
                        return PlaceholderResult.value(name);
                    } else {
                        return PlaceholderResult.value("Doesn't exist!");
                    }
                } catch (IllegalArgumentException e) {
                    return PlaceholderResult.invalid("\"" + argument + "\" is not an integer!");
                }
            } else {
                return PlaceholderResult.invalid("No argument!");
            }
        });

        Placeholders.register(new ResourceLocation("itemdex", "count"), (context, argument) -> {
            if (context.hasPlayer()) {
                int count = ItemDexManager.data.getOrDefault(context.player().getUUID(), new HashSet<>()).size();
                return PlaceholderResult.value(String.valueOf(count));
            } else {
                return PlaceholderResult.invalid("No player!");

            }
        });
    }

}
