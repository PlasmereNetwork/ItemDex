package me.drex.itemdex.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.drex.itemdex.config.ConfigManager;
import me.drex.itemdex.util.gui.ItemDexGui;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class ItemDexCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("itemdex")
                        .then(
                                Commands.literal("reload")
                                        .executes(ctx -> {
                                            ConfigManager.load();
                                            return 1;
                                        })
                        )
                        .executes(ctx -> execute(ctx.getSource()))
        );
    }

    public static int execute(CommandSourceStack src) throws CommandSyntaxException {
        ServerPlayer player = src.getPlayerOrException();
        ItemDexGui itemDexGui = new ItemDexGui(player);
        itemDexGui.open();
        itemDexGui.updateDisplay();
        return 1;
    }

}
