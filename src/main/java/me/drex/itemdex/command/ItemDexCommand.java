package me.drex.itemdex.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.sgui.api.gui.SimpleGui;
import me.drex.itemdex.util.ItemDexManager;
import me.drex.itemdex.util.gui.ItemDexGui;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

import java.util.HashSet;
import java.util.List;

public class ItemDexCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("itemdex")
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
