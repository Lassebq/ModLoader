package io.github.lassebq.modloader.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.lassebq.modloader.DimensionBase;
import net.minecraft.world.dimension.Dimension;

@Mixin(Dimension.class)
public class DimensionMixin {

    @Inject(
        method = "fromId(I)Lnet/minecraft/world/dimension/Dimension;",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void fromId(int id, CallbackInfoReturnable<Dimension> ci) {
        DimensionBase localDimensionBase = DimensionBase.getDimByNumber(id);
        if(localDimensionBase != null) {
            ci.setReturnValue(localDimensionBase.getWorldProvider());
        } else {
            ci.setReturnValue(null);
        }
    }
}
