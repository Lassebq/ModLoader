package io.github.lassebq.modloader.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import io.github.lassebq.modloader.ModLoader;
import net.minecraft.block.Block;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mixin(FurnaceBlockEntity.class)
public class FurnaceBlockEntityMixin {

    @Overwrite
    private int getFuelTime(ItemStack stack) {
        if (stack == null) {
            return 0;
        } else {
            int var2 = stack.getItem().id;
            if (var2 < 256 && Block.BY_ID[var2].material == Material.WOOD) {
                return 300;
            } else if (var2 == Item.STICK.id) {
                return 100;
            } else if (var2 == Item.COAL.id) {
                return 1600;
            } else if (var2 == Item.LAVA_BUCKET.id) {
                return 20000;
            } else {
                return var2 == Block.SAPLING.id ? 100 : ModLoader.AddAllFuel(var2);
            }
        }
    }
}
