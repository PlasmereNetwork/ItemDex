package me.drex.itemdex.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.drex.itemdex.util.ItemDexManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class AppraiseCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("appraise").executes(AppraiseCommand::execute)
        );
    }

    public static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ItemStack item = player.getMainHandItem();
        if (item.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("You need to hold an item!"));
            return 0;
        } else {
            boolean success = ItemDexManager.appraise(player, item.getItem());
            if (success) {
                ctx.getSource().sendSuccess(Component.literal("Successfully appraised ").append(item.getItem().getDescription()), false);
                item.shrink(1);
                return Command.SINGLE_SUCCESS;
            } else {
                ctx.getSource().sendFailure(Component.literal("You have already appraised ").append(item.getItem().getDescription()));
                return 0;
            }
        }
    }

}
