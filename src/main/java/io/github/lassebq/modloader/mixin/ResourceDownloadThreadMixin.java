package io.github.lassebq.modloader.mixin;

import java.io.File;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.resource.ResourceDownloadThread;

@Mixin(ResourceDownloadThread.class)
public abstract class ResourceDownloadThreadMixin {
    @Shadow public File workingDirectory;
    @Shadow public abstract void m_9704077(File file, String string);

    @Inject(
        method = "run()V",
        at = @At("HEAD")
    )
    public void run(CallbackInfo ci) {
        m_9704077(new File(workingDirectory, "mod"), "");
    }
}
