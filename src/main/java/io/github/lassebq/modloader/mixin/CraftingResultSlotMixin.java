package io.github.lassebq.modloader.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.lassebq.modloader.ModLoader;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.slot.CraftingResultSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@Mixin(CraftingResultSlot.class)
public class CraftingResultSlotMixin {
    @Shadow private PlayerEntity player;
    
    @Inject(
        // target = @Desc(
        //     owner = CraftingResultSlot.class,
        //     value = "onStackRemovedByPlayer",
        //     args = {ItemStack.class}
        // ),
        method = "onStackRemovedByPlayer(Lnet/minecraft/item/ItemStack;)V",
        at = @At(
            value = "INVOKE",
            // desc = @Desc(
            //     owner = ItemStack.class,
            //     value = "onResult",
            //     args = {World.class, PlayerEntity.class}
            // )
            target = "Lnet/minecraft/item/ItemStack;onResult(Lnet/minecraft/world/World;Lnet/minecraft/entity/living/player/PlayerEntity;)V"
        )
    )
    public void onStackRemovedByPlayer(ItemStack stack, CallbackInfo ci) {
        ModLoader.TakenFromCrafting(this.player, stack);
    }

}
