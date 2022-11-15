package me.drex.itemdex.mixin;

import me.drex.itemdex.util.ItemDexManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Inject(method = "saveEverything", at = @At("HEAD"))
    public void onSave(boolean bl, boolean bl2, boolean bl3, CallbackInfoReturnable<Boolean> cir) {
        ItemDexManager.save();
    }

    @Inject(method = "stopServer", at = @At("HEAD"))
    public void onStop(CallbackInfo ci) {
        ItemDexManager.save();
    }

}
