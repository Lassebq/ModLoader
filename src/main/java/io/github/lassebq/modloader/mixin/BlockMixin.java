package io.github.lassebq.modloader.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.lassebq.modloader.Loc;
import io.github.lassebq.modloader.SAPI;
import net.minecraft.block.Block;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.world.World;

@Mixin(Block.class)
public class BlockMixin {
    @Shadow public int id;

    @Inject(
        method = "afterMinedByPlayer(Lnet/minecraft/world/World;Lnet/minecraft/entity/living/player/PlayerEntity;IIII)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void afterMinedByPlayer(World world, PlayerEntity player, int x, int y, int z, int metadata, CallbackInfo ci) {
        if(SAPI.interceptHarvest(world, player, new Loc(x,y,z), this.id, metadata)) {
            ci.cancel();
        }
    }
}
