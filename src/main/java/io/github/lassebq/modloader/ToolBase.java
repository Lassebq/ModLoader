package io.github.lassebq.modloader;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.AxeItem;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;

public class ToolBase {
    public static final ToolBase Pickaxe;
    public static final ToolBase Shovel;
    public static final ToolBase Axe;
    public ArrayList<BlockHarvestPower> mineBlocks = new ArrayList<>();
    public ArrayList<Material> mineMaterials = new ArrayList<>();

    static {
        SAPI.showText();
        Pickaxe = new ToolBase();
        Shovel = new ToolBase();
        Axe = new ToolBase();

        for(Block block : PickaxeItem.EFFECTIVE_BLOCKS) {
            Pickaxe.mineBlocks.add(new BlockHarvestPower(block.id, 20.0F));
        }
        for(Block block : AxeItem.EFFECTIVE_BLOCKS) {
            Axe.mineBlocks.add(new BlockHarvestPower(block.id, 20.0F));
        }
        for(Block block : ShovelItem.EFFECTIVE_BLOCKS) {
            Shovel.mineBlocks.add(new BlockHarvestPower(block.id, 20.0F));
        }

    }

    public ToolBase() {
    }

    public boolean canHarvest(Block block, float currentPower) {
        for(Material material : mineMaterials) {
            if (material == block.material) {
                return true;
            }
        }

        for(BlockHarvestPower power : mineBlocks) {
            if(block.id == power.blockID || currentPower >= power.percentage) {
                return true;
            }
        }
        return false;
    }
}
