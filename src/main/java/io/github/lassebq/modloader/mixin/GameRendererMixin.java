package io.github.lassebq.modloader.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.lassebq.modloader.ModLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.GameRenderer;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow private Minecraft minecraft;

    @Inject(
        // target = @Desc(
        //     owner = GameRenderer.class,
        //     value = "render",
        //     args = {float.class}
        // ),
        method = "render(F)V",
        at = @At("TAIL")
    )
    public void render(float f, CallbackInfo ci) {
		ModLoader.OnTick(minecraft);
    }

}
