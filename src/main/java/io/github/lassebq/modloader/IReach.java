package io.github.lassebq.modloader;

import net.minecraft.item.ItemStack;

public interface IReach {
    boolean reachItemMatches(ItemStack var1);

    float getReach(ItemStack var1);
}
