package io.github.lassebq.modloader.mixin;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.Lighting;

import io.github.lassebq.modloader.SAPI;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.menu.AchievementsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.resource.language.I18n;
import net.minecraft.stat.PlayerStats;
import net.minecraft.stat.achievement.AchievementStat;
import net.minecraft.stat.achievement.Achievements;
import net.minecraft.util.math.MathHelper;

@Mixin(AchievementsScreen.class)
public class AchievementsScreenMixin extends Screen {
   @Shadow private static int MIN_COLUMN;
   @Shadow private static int MIN_ROW;
   @Shadow private static int MAX_COLUMN;
   @Shadow private static int MAX_ROW;
   @Shadow protected int iconWidth;
   @Shadow protected int iconHeight;
   @Shadow protected double mouseX;
   @Shadow protected double mouseY;
   @Shadow protected double scaledMouseDx;
   @Shadow protected double scaledMouseDy;
   @Shadow private PlayerStats stats;

    @Inject(method = "init()V", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        this.buttons.add(new OptionButtonWidget(11, this.width / 2 - 113, this.height / 2 + 74, 20, 20, "<"));
        this.buttons.add(new OptionButtonWidget(12, this.width / 2 - 93, this.height / 2 + 74, 20, 20, ">"));
    }

    @Inject(method = "buttonClicked(Lnet/minecraft/client/gui/widget/ButtonWidget;)V", at = @At("HEAD"))
    protected void buttonClicked(ButtonWidget button, CallbackInfo ci) {
        if (button.id == 11) {
            SAPI.acPagePrev();
        }
        if (button.id == 12) {
            SAPI.acPageNext();
        }
    }

    @Inject(method = "setTitle()V", at = @At("TAIL"))
    protected void setTitle(CallbackInfo ci) {
        this.textRenderer.draw(SAPI.acGetCurrentPageTitle(), this.width / 2 - 69, this.height / 2 + 80, 4210752);
    }

    public boolean isVisibleAchievement(AchievementStat achievement, int deep) {
        if (this.checkHidden(achievement)) {
            return false;
        } else {
            int tabID = SAPI.acGetPage(achievement).id;
            if (tabID == SAPI.acCurrentPage) {
                return true;
            } else {
                if (deep >= 1) {
                    ArrayList<Object> list = new ArrayList(Achievements.ALL);

                    int i;
                    AchievementStat tmpAc;
                    for (i = 0; i < list.size(); ++i) {
                        tmpAc = (AchievementStat) list.get(i);
                        if (tmpAc.id == achievement.id) {
                            list.remove(i--);
                        } else if (tmpAc.parent == null) {
                            list.remove(i--);
                        } else if (tmpAc.parent.id != achievement.id) {
                            list.remove(i--);
                        }
                    }

                    for (i = 0; i < list.size(); ++i) {
                        tmpAc = (AchievementStat) list.get(i);
                        if (this.isVisibleAchievement(tmpAc, deep - 1)) {
                            return true;
                        }
                    }
                }

                return false;
            }
        }
    }

    public boolean isVisibleLine(AchievementStat achievement) {
        return achievement.parent != null && this.isVisibleAchievement(achievement, 1)
                && this.isVisibleAchievement(achievement.parent, 1);
    }

    public boolean checkHidden(AchievementStat achievement) {
        if (this.minecraft.statHandler.hasAchievement(achievement)) {
            return false;
        } else if (SAPI.acIsHidden(achievement)) {
            return true;
        } else {
            return achievement.parent == null ? false : this.checkHidden(achievement.parent);
        }
    }

    @Overwrite
    protected void renderIcons(int mouseX, int mouseY, float tickdelta) {
        int var4 = MathHelper.floor(this.mouseX + (this.scaledMouseDx - this.mouseX) * (double) tickdelta);
        int var5 = MathHelper.floor(this.mouseY + (this.scaledMouseDy - this.mouseY) * (double) tickdelta);
        if (var4 < MIN_COLUMN) {
            var4 = MIN_COLUMN;
        }

        if (var5 < MIN_ROW) {
            var5 = MIN_ROW;
        }

        if (var4 >= MAX_COLUMN) {
            var4 = MAX_COLUMN - 1;
        }

        if (var5 >= MAX_ROW) {
            var5 = MAX_ROW - 1;
        }

        int var6 = this.minecraft.textureManager.load("/terrain.png");
        int var7 = this.minecraft.textureManager.load("/achievement/bg.png");
        int var8 = (this.width - this.iconWidth) / 2;
        int var9 = (this.height - this.iconHeight) / 2;
        int var10 = var8 + 16;
        int var11 = var9 + 17;
        this.drawOffset = 0.0F;
        GL11.glDepthFunc(518);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, -200.0F);
        GL11.glEnable(3553);
        GL11.glDisable(2896);
        GL11.glEnable(32826);
        GL11.glEnable(2903);
        this.minecraft.textureManager.bind(var6);
        int var12 = var4 + 288 >> 4;
        int var13 = var5 + 288 >> 4;
        int var14 = (var4 + 288) % 16;
        int var15 = (var5 + 288) % 16;
        Random var21 = new Random();

        for (int var22 = 0; var22 * 16 - var15 < 155; ++var22) {
            float var23 = 0.6F - (float) (var13 + var22) / 25.0F * 0.3F;
            GL11.glColor4f(var23, var23, var23, 1.0F);

            for (int var24 = 0; var24 * 16 - var14 < 224; ++var24) {
                var21.setSeed((long) (1234 + var12 + var24));
                var21.nextInt();
                int var26 = SAPI.acGetCurrentPage().bgGetSprite(var21, var12 + var24, var13 + var22);
                if (var26 != -1) {
                    this.drawTexture(var10 + var24 * 16 - var14, var11 + var22 * 16 - var15, var26 % 16 << 4,
                            var26 >> 4 << 4, 16, 16);
                }
            }
        }

        GL11.glEnable(2929);
        GL11.glDepthFunc(515);
        GL11.glDisable(3553);

        int var16;
        int var17;
        int var33;
        int var38;
        for (var12 = 0; var12 < Achievements.ALL.size(); ++var12) {
            AchievementStat var28 = (AchievementStat) Achievements.ALL.get(var12);
            if (var28.parent != null) {
                var14 = var28.column * 24 - var4 + 11 + var10;
                var15 = var28.row * 24 - var5 + 11 + var11;
                var16 = var28.parent.column * 24 - var4 + 11 + var10;
                var17 = var28.parent.row * 24 - var5 + 11 + var11;
                boolean var18 = false;
                boolean var19 = this.stats.hasAchievement(var28);
                boolean var20 = this.stats.hasParentAchievement(var28);
                var38 = Math.sin((double) (System.currentTimeMillis() % 600L) / 600.0 * Math.PI * 2.0) > 0.6 ? 255
                        : 130;
                if (var19) {
                    var33 = -9408400;
                } else if (var20) {
                    var33 = '\uff00' + (var38 << 24);
                } else {
                    var33 = -16777216;
                }
                if(!this.isVisibleLine(var28)) {
                    continue;
                }
                this.drawHorizontalLine(var14, var16, var15, var33);
                this.drawVerticalLine(var16, var15, var17, var33);
            }
        }

        AchievementStat var27 = null;
        ItemRenderer var29 = new ItemRenderer();
        GL11.glPushMatrix();
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        Lighting.turnOn();
        GL11.glPopMatrix();
        GL11.glDisable(2896);
        GL11.glEnable(32826);
        GL11.glEnable(2903);

        int var34;
        for (var14 = 0; var14 < Achievements.ALL.size(); ++var14) {
            AchievementStat var30 = (AchievementStat) Achievements.ALL.get(var14);
            if (!this.isVisibleAchievement(var30, 1)) {
                continue;
            }
            var16 = var30.column * 24 - var4;
            var17 = var30.row * 24 - var5;
            if (var16 >= -24 && var17 >= -24 && var16 <= 224 && var17 <= 155) {
                float var35;
                if (this.stats.hasAchievement(var30)) {
                    var35 = 1.0F;
                    GL11.glColor4f(var35, var35, var35, 1.0F);
                } else if (this.stats.hasParentAchievement(var30)) {
                    var35 = Math.sin((double) (System.currentTimeMillis() % 600L) / 600.0 * Math.PI * 2.0) < 0.6 ? 0.6F
                            : 0.8F;
                    GL11.glColor4f(var35, var35, var35, 1.0F);
                } else {
                    var35 = 0.3F;
                    GL11.glColor4f(var35, var35, var35, 1.0F);
                }

                this.minecraft.textureManager.bind(var7);
                var33 = var10 + var16;
                var34 = var11 + var17;
                if (var30.isChallenge()) {
                    this.drawTexture(var33 - 2, var34 - 2, 26, 202, 26, 26);
                } else {
                    this.drawTexture(var33 - 2, var34 - 2, 0, 202, 26, 26);
                }

                if (!this.stats.hasParentAchievement(var30)) {
                    float var36 = 0.1F;
                    GL11.glColor4f(var36, var36, var36, 1.0F);
                    var29.useCustomDisplayColor = false;
                }

                GL11.glEnable(2896);
                GL11.glEnable(2884);
                var29.renderGuiItemWithEnchantmentGlint(this.minecraft.textRenderer, this.minecraft.textureManager,
                        var30.icon, var33 + 3, var34 + 3);
                GL11.glDisable(2896);
                if (!this.stats.hasParentAchievement(var30)) {
                    var29.useCustomDisplayColor = true;
                }

                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                if (mouseX >= var10 && mouseY >= var11 && mouseX < var10 + 224 && mouseY < var11 + 155
                        && mouseX >= var33 && mouseX <= var33 + 22 && mouseY >= var34 && mouseY <= var34 + 22) {
                    var27 = var30;
                }
            }
        }

        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.textureManager.bind(var7);
        this.drawTexture(var8, var9, 0, 0, this.iconWidth, this.iconHeight);
        GL11.glPopMatrix();
        this.drawOffset = 0.0F;
        GL11.glDepthFunc(515);
        GL11.glDisable(2929);
        GL11.glEnable(3553);
        super.render(mouseX, mouseY, tickdelta);
        if (var27 != null) {
            String var31 = var27.name;
            String var32 = var27.getDescription();
            var17 = mouseX + 12;
            var33 = mouseY - 4;
            if (this.stats.hasParentAchievement(var27)) {
                var34 = Math.max(this.textRenderer.getWidth(var31), 120);
                int var37 = this.textRenderer.splitAndGetHeight(var32, var34);
                if (this.stats.hasAchievement(var27)) {
                    var37 += 12;
                }

                this.fillGradient(var17 - 3, var33 - 3, var17 + var34 + 3, var33 + var37 + 3 + 12, -1073741824,
                        -1073741824);
                this.textRenderer.drawSplit(var32, var17, var33 + 12, var34, -6250336);
                if (this.stats.hasAchievement(var27)) {
                    this.textRenderer.drawWithShadow(I18n.translate("achievement.taken"), var17, var33 + var37 + 4,
                            -7302913);
                }
            } else {
                var34 = Math.max(this.textRenderer.getWidth(var31), 120);
                String var39 = I18n.translate("achievement.requires", new Object[] { var27.parent.name });
                var38 = this.textRenderer.splitAndGetHeight(var39, var34);
                this.fillGradient(var17 - 3, var33 - 3, var17 + var34 + 3, var33 + var38 + 12 + 3, -1073741824,
                        -1073741824);
                this.textRenderer.drawSplit(var39, var17, var33 + 12, var34, -9416624);
            }

            this.textRenderer.drawWithShadow(var31, var17, var33,
                    this.stats.hasParentAchievement(var27) ? (var27.isChallenge() ? -128 : -1)
                            : (var27.isChallenge() ? -8355776 : -8355712));
        }

        GL11.glEnable(2929);
        GL11.glEnable(2896);
        Lighting.turnOff();
    }
}
