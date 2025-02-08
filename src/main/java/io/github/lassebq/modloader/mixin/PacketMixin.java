package io.github.lassebq.modloader.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.lassebq.modloader.ModLoaderMp;
import net.minecraft.network.packet.Packet;

@Mixin(Packet.class)
public class PacketMixin {
    @Inject(
        method = "<clinit>()V",
        at = @At("TAIL")
    )
    private static void mlMpInit(CallbackInfo ci) {
        ModLoaderMp.Init();
    }
}
