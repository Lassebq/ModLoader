package io.github.lassebq.modloader.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.lassebq.modloader.PortalDimensionSetter;
import net.minecraft.block.PortalBlock;
import net.minecraft.client.entity.living.player.InputPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

@Mixin(PortalBlock.class)
public class PortalBlockMixin {
    
    @Inject(
        method = "onEntityCollision(Lnet/minecraft/world/World;IIILnet/minecraft/entity/Entity;)V", 
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;m_2002568()V"
        )
    )
    public void onEntityCollision(World world, int x, int y, int z, Entity entity, CallbackInfo ci) {
        if(entity instanceof InputPlayerEntity) {
            ((PortalDimensionSetter)entity).setDimension(getDimNumber());
        }
    }


    public int getDimNumber() {
        return -1;
    }
}
