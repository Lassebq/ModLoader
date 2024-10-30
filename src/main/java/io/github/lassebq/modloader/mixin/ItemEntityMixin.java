package io.github.lassebq.modloader.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.lassebq.modloader.ModLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Shadow public ItemStack item;

    @Inject(
        // target = @Desc(
        //     owner = ItemEntity.class,
        //     value = "onPlayerCollision",
        //     args = {PlayerEntity.class}
        // ),
        method = "onPlayerCollision(Lnet/minecraft/entity/living/player/PlayerEntity;)V",
        at = @At(
            value = "INVOKE",
            desc = @Desc(
                owner = PlayerEntity.class,
                value = "sendPickup",
                args = {Entity.class, int.class}
            )
        )
    )
	public void onPlayerCollision(PlayerEntity player, CallbackInfo ci) {
        ModLoader.OnItemPickup(player, item);
    }
}
