package me.drex.itemdex.util.gui;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import me.drex.itemdex.util.ItemDexManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashSet;
import java.util.List;

public class ItemDexGui extends SimpleGui {

    public static final GuiElement EMPTY = new GuiElement(ItemStack.EMPTY, GuiElementInterface.EMPTY_CALLBACK);
    public static final GuiElement FILLER = new GuiElementBuilder(Items.WHITE_STAINED_GLASS_PANE).build();
    public static final int PAGE_SIZE = 9 * 5;
    private int page = 0;
    List<Item> items = ItemDexManager.data.getOrDefault(player.getUUID(), new HashSet<>()).stream().toList();

    public ItemDexGui(ServerPlayer player) {
        super(MenuType.GENERIC_9x6, player, false);
        this.setTitle(Component.literal("ItemDex"));
    }

    protected void nextPage() {
        this.page = Math.min(this.getPageAmount() - 1, this.page + 1);
        this.updateDisplay();
    }

    protected boolean canNextPage() {
        return this.getPageAmount() > this.page + 1;
    }

    protected void previousPage() {
        this.page = Math.max(0, this.page - 1);
        this.updateDisplay();
    }

    protected boolean canPreviousPage() {
        return this.page - 1 >= 0;
    }

    public int getPage() {
        return this.page;
    }

    public void updateDisplay() {
        var offset = this.page * PAGE_SIZE;

        for (int i = 0; i < PAGE_SIZE; i++) {
            int id = offset + i;
            GuiElement element;
            if (items.size() > id) {
                element = new GuiElementBuilder(items.get(id)).build();
                //element = items.get(id);/*asDisplayElement(elements.get(id));*/
            } else {
                element = EMPTY;
            }
            this.setSlot(i, element);
        }

        for (int i = 0; i < 9; i++) {
            this.setSlot(i + PAGE_SIZE, this.getNavElement(i));
        }
    }

    protected int getPageAmount() {
        return (int) Math.ceil(items.size() / (double) PAGE_SIZE);
    }

    protected GuiElement getNavElement(int id) {
        return switch (id) {
            case 3 -> previousPageElement();
            case 4 -> pageInfo();
            case 5 -> nextPageElement();
            case 8 -> new GuiElementBuilder(Items.BARRIER)
                    .setName(Component.literal("Close!").withStyle(ChatFormatting.RED))
                    .hideFlags()
                    .setCallback((x, y, z) -> {
                        playClickSound(this.player);
                        this.close();
                    }).build();
            default -> FILLER;
        };
    }

    public GuiElement previousPageElement() {
        if (canPreviousPage()) {
            return new GuiElementBuilder(Items.PLAYER_HEAD)
                    .setName(Component.translatable("spectatorMenu.previous_page").withStyle(ChatFormatting.GRAY))
                    .hideFlags()
                    .setSkullOwner(GuiTextures.GUI_PREVIOUS_PAGE)
                    .setCallback((x, y, z) -> {
                        playClickSound(player);
                        previousPage();
                    }).build();
        } else {
            return new GuiElementBuilder(Items.PLAYER_HEAD)
                    .setName(Component.translatable("spectatorMenu.previous_page").withStyle(ChatFormatting.WHITE))
                    .hideFlags()
                    .setSkullOwner(GuiTextures.GUI_PREVIOUS_PAGE_BLOCKED)
                    .setCallback((x, y, z) -> {
                        playFailSound(player);
                    }).build();
            //return FILLER;
        }
    }

    public GuiElement nextPageElement() {
        if (canNextPage()) {
            return new GuiElementBuilder(Items.PLAYER_HEAD)
                    .setName(Component.translatable("spectatorMenu.next_page").withStyle(ChatFormatting.GRAY))
                    .hideFlags()
                    .setSkullOwner(GuiTextures.GUI_NEXT_PAGE)
                    .setCallback((x, y, z) -> {
                        playClickSound(player);
                        nextPage();
                    }).build();
        } else {
            return new GuiElementBuilder(Items.PLAYER_HEAD)
                    .setName(Component.translatable("spectatorMenu.next_page").withStyle(ChatFormatting.WHITE))
                    .hideFlags()
                    .setSkullOwner(GuiTextures.GUI_NEXT_PAGE_BLOCKED)
                    .setCallback((x, y, z) -> {
                        playFailSound(player);
                    }).build();
            //return FILLER;
        }
    }

    public GuiElement pageInfo() {
        if (getPageAmount() != 0) {
            return new GuiElementBuilder(Items.WRITABLE_BOOK)
                    .setName(Component.literal("Page " + (getPage() + 1) + "/" + getPageAmount()).withStyle(ChatFormatting.WHITE))
                    .hideFlags().build();
        } else {
            return FILLER;
        }
    }


    public static void playClickSound(ServerPlayer player) {
        player.playNotifySound(SoundEvents.UI_BUTTON_CLICK, SoundSource.MASTER, 1, 1);
    }

    public static void playFailSound(ServerPlayer player) {
        player.playNotifySound(SoundEvents.VILLAGER_NO, SoundSource.MASTER, 1, 1);
    }

}
