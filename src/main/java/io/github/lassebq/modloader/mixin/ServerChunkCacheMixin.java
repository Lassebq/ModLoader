package io.github.lassebq.modloader.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.lassebq.modloader.ModLoader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSource;
import net.minecraft.world.chunk.ServerChunkCache;

@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin {
   @Shadow private ChunkSource generator;
   @Shadow private World world;

   @Inject(
        // target = @Desc(
        //     owner = ServerChunkCache.class,
        //     value = "populateChunk",
        //     args = {ChunkSource.class, int.class, int.class}
        // ),
        method = "populateChunk(Lnet/minecraft/world/chunk/ChunkSource;II)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/ChunkSource;populateChunk(Lnet/minecraft/world/chunk/ChunkSource;II)V",
            // desc = @Desc(
            //     owner = ChunkSource.class,
            //     value = "populateChunk",
            //     args = {ChunkSource.class, int.class, int.class}
            // ),
            shift = Shift.AFTER
        )
    )
    public void populateChunk(ChunkSource source, int chunkX, int chunkZ, CallbackInfo ci) {
        ModLoader.PopulateChunk(this.generator, chunkX, chunkZ, this.world);
    }
}