package io.github.lassebq.modloader.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.lassebq.modloader.BlockHarvestPower;
import io.github.lassebq.modloader.ToolBase;
import net.minecraft.block.Block;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item__ToolMaterial;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.ToolItem;

@Mixin(ToolItem.class)
public class ToolItemMixin {
    @Shadow private Block[] effectiveBlocks;
    @Shadow private float miningSpeed;

    @Unique
    private ToolBase toolBase;

    @Inject(method = "<init>(IILnet/minecraft/item/Item__ToolMaterial;[Lnet/minecraft/block/Block;)V", at = @At("TAIL"))
    public void init(int id, int attackDamage, Item__ToolMaterial item__ToolMaterial, Block[] blocks, CallbackInfo ci) {
        toolBase = getToolBase();
        effectiveBlocks = new Block[blocks.length + (toolBase == null ? 0 : toolBase.mineBlocks.size())];
        int i = 0;
        for (Block b : blocks) {
            effectiveBlocks[i++] = b;
        }
        if(toolBase != null) {
            for (BlockHarvestPower harvestPower : toolBase.mineBlocks) {
                effectiveBlocks[i++] = Block.BY_ID[harvestPower.blockID];
            }
        }
    }

    @Inject(
        method = "getMiningSpeed(Lnet/minecraft/item/ItemStack;Lnet/minecraft/block/Block;)F",
        at = @At("HEAD"),
        cancellable = true
    )
    public void getMiningSpeed(ItemStack stack, Block block, CallbackInfoReturnable<Float> ci) {
        if(toolBase.canHarvest(block, miningSpeed)) {
            ci.setReturnValue(this.miningSpeed);
        }
        // for (Material m : toolBase.mineMaterials) {
        //     if (block.material == m) {
        //         ci.setReturnValue(this.miningSpeed);
        //     }
        // }
    }

    public ToolBase getToolBase() {
        ToolItem thisItem = (ToolItem) (Object) this;
        if (thisItem instanceof PickaxeItem) {
            return ToolBase.Pickaxe;
        } else if (thisItem instanceof AxeItem) {
            return ToolBase.Axe;
        } else {
            return thisItem instanceof ShovelItem ? ToolBase.Shovel : null;
        }
    }
}
