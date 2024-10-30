package io.github.lassebq.modloader.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.lassebq.modloader.DimensionBase;
import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(
        method = "changeDimension()V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void changeDimension(CallbackInfo ci) {
        DimensionBase.usePortal(0, true);
        ci.cancel();
    }

}
