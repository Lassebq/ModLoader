package io.github.lassebq.modloader.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import io.github.lassebq.modloader.SAPI;
import net.minecraft.client.interaction.SurvivalInteractionManager;

@Mixin(SurvivalInteractionManager.class)
public class SurvivalInteractionManagerMixin {
    // @Inject(
    //     method = "finishMiningBlock(IIII)Z",
    //     at = @At(
    //         value = "INVOKE",
    //         target = "Lnet/minecraft/block/Block;afterMinedByPlayer(Lnet/minecraft/world/World;Lnet/minecraft/entity/living/player/PlayerEntity;IIII)V",
    //         shift = Shift.BEFORE
    //     ),
    //     cancellable = true
    // )
    // public void interceptHarvest(int x, int y, int z, int face, CallbackInfoReturnable<Boolean> ci) {
    //     if()
    // }

    @Overwrite
    public float getReach() {
        return SAPI.reachGet();
    }
}
