package io.github.lassebq.modloader;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.util.Random;

import net.minecraft.item.ItemStack;

public class DungeonLoot {
    public final ItemStack loot;
    public final int min;
    public final int max;

    public DungeonLoot(ItemStack stack) {
        this.loot = new ItemStack(stack.itemId, 1, stack.getMetadata());
        this.min = this.max = stack.size;
    }

    public DungeonLoot(ItemStack stack, int min, int max) {
        this.loot = new ItemStack(stack.itemId, 1, stack.getMetadata());
        this.min = min;
        this.max = max;
    }

    public ItemStack getStack() {
        int damage = 0;
        if (this.loot.itemId <= 255) {
            // Whatever this is, it doesn't make sense. SAPI dev must've been high
            // if (Block.BY_ID[this.loot.itemId].getColor(1) != 1) {
            //     damage = this.loot.getMetadata();
            // } else if (!this.loot.getItem().handheld) {
            //     damage = this.loot.getMetadata();
            // }
            damage = this.loot.getMetadata();
        }

        return new ItemStack(this.loot.itemId, this.min + (new Random()).nextInt(this.max - this.min + 1), damage);
    }
}
