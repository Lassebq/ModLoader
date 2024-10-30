package io.github.lassebq.modloader;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.world.World;

public interface IInterceptHarvest {
    boolean canIntercept(World var1, PlayerEntity var2, Loc var3, int var4, int var5);

    void intercept(World var1, PlayerEntity var2, Loc var3, int var4, int var5);
}

