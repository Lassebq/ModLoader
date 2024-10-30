package io.github.lassebq.modloader.mixin;

import java.util.Collection;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.lassebq.modloader.ModLoader;
import net.minecraft.client.render.entity.EntityRenderDispatcher;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Shadow private Map renderers;

    @Redirect(
        // target = @Desc(
        //     owner = EntityRenderDispatcher.class,
        //     value = "<init>"
        // ),
        method = "<init>()V",
        // TODO enable once Micromixin supports :LAST and :FIRST
        // at = @At(value = "INVOKE:LAST",
        //     desc = @Desc(
        //         owner = Map.class,
        //         value = "put",
        //         args = {Object.class, Object.class},
        //         ret = Object.class
        //     )
        // )
        at = @At(value = "INVOKE",
            // desc = @Desc(
            //     owner = Map.class,
            //     value = "values",
            //     ret = Collection.class
            // )
            target = "Ljava/util/Map;values()Ljava/util/Collection;"
        )
    )
    private static Collection addRenderers(Map renderers) {
        ModLoader.AddAllRenderers(renderers);
        return renderers.values();
    }
}
