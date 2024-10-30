package io.github.lassebq.modloader.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;

@Mixin(DisconnectedScreen.class)
public class DisconnectedScreenMixin extends Screen {
    @Shadow private String name;
    @Shadow private String reason;

    @Overwrite
    public void render(int mouseX, int mouseY, float tickDelta) {
        this.renderBackground();
        this.drawCenteredString(this.textRenderer, this.name, this.width / 2, this.height / 2 - 50, 16777215);
        String[] split = reason.split("\n");
        for(int k = 0; k < split.length; ++k) {
            this.drawCenteredString(this.textRenderer, split[k], this.width / 2, this.height / 2 - 10 + k * 10, 16777215);
        }
        super.render(mouseX, mouseY, tickDelta);
    }
}
