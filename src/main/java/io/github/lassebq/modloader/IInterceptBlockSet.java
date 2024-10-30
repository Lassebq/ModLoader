package io.github.lassebq.modloader;

import net.minecraft.world.World;

public interface IInterceptBlockSet {
    boolean canIntercept(World var1, Loc var2, int var3);

    int intercept(World var1, Loc var2, int var3);
}
