package io.github.lassebq.modloader.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.lassebq.modloader.DimensionBase;
import net.minecraft.world.World;
import net.minecraft.world.WorldData;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.WorldStorage;

@Mixin(World.class)
public class WorldMixin {
    @Shadow protected WorldData data;

    @Redirect(
        method = "<init>(Lnet/minecraft/world/storage/WorldStorage;Ljava/lang/String;JLnet/minecraft/world/dimension/Dimension;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/dimension/Dimension;fromId(I)Lnet/minecraft/world/dimension/Dimension;"
        )
    )
    public Dimension worldInit(int i, WorldStorage storage, String name, long seed, Dimension dimension) {
        if(this.data != null) {
            DimensionBase localDimensionBase = DimensionBase.getDimByNumber(this.data.getDimensionId());
            return localDimensionBase.getWorldProvider();
        }
        return Dimension.fromId(i);
    }
}
