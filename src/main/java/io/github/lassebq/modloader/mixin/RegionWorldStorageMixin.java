package io.github.lassebq.modloader.mixin;

import java.io.File;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import io.github.lassebq.modloader.DimensionBase;
import net.minecraft.world.chunk.storage.ChunkStorage;
import net.minecraft.world.chunk.storage.RegionChunkStorage;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.AlphaWorldStorage;
import net.minecraft.world.storage.RegionWorldStorage;

@Mixin(RegionWorldStorage.class)
public class RegionWorldStorageMixin extends AlphaWorldStorage {
    public RegionWorldStorageMixin(File file, String string, boolean bl) {
       super(file, string, bl);
    }

    @Overwrite
    public ChunkStorage getChunkStorage(Dimension dimension) {
        File var2 = this.getDir();
        DimensionBase localDimensionBase = DimensionBase.getDimByProvider(dimension.getClass());
        if (localDimensionBase.number != 0) {
            File var3 = new File(var2, "DIM" + localDimensionBase.number);
            var3.mkdirs();
            return new RegionChunkStorage(var3);
        } else {
            return new RegionChunkStorage(var2);
        }
    }

}
