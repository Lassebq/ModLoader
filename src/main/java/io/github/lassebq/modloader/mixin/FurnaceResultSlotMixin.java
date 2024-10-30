package io.github.lassebq.modloader.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.lassebq.modloader.ModLoader;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.slot.FurnaceResultSlot;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.item.ItemStack;

@Mixin(FurnaceResultSlot.class)
public class FurnaceResultSlotMixin {
    @Shadow private PlayerEntity player;

    @Inject(
        // target = @Desc(
        //     owner = FurnaceResultSlot.class,
        //     value = "onStackRemovedByPlayer",
        //     args = {ItemStack.class}
        // ),
        method = "onStackRemovedByPlayer(Lnet/minecraft/item/ItemStack;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/inventory/slot/InventorySlot;onStackRemovedByPlayer(Lnet/minecraft/item/ItemStack;)V"
            // desc = @Desc(
            //     owner = InventorySlot.class,
            //     value = "onStackRemovedByPlayer",
            //     args = {ItemStack.class}
            // )
        )
    )
    public void onPickupFromSlot(ItemStack itemStack, CallbackInfo ci) {
        ModLoader.TakenFromFurnace(this.player, itemStack);
    }
}
