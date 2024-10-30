package io.github.lassebq.modloader.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.lassebq.modloader.DimensionBase;
import io.github.lassebq.modloader.PortalDimensionSetter;
import io.github.lassebq.modloader.PlayerAPI;
import io.github.lassebq.modloader.PlayerBase;
import io.github.lassebq.modloader.PlayerBaseAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Session;
import net.minecraft.client.entity.living.player.InputPlayerEntity;
import net.minecraft.client.player.input.PlayerInput;
import net.minecraft.client.sound.system.SoundEngine;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.living.player.PlayerEntity__SleepAllowedStatus;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

@Mixin(InputPlayerEntity.class)
public abstract class InputPlayerEntityMixin extends PlayerEntity implements PlayerBaseAccessor, PortalDimensionSetter {

    public InputPlayerEntityMixin(World world) {
        super(world);
    }
    @Shadow public PlayerInput input;
    @Shadow protected Minecraft minecraft;

    public int portal;

    @Redirect(
        method = "tickAi()V",
        at = @At(
            value ="INVOKE",
            target = "Lnet/minecraft/client/sound/system/SoundEngine;play(Ljava/lang/String;FF)V"
        )
    )
    public void playSound(SoundEngine soundEngine, String sound, float vol, float pitch) {
        DimensionBase dim = DimensionBase.getDimByNumber(portal);
        if(sound.equals("portal.trigger")) {
            soundEngine.play(dim.soundTrigger, vol, pitch);
        } else if(sound.equals("portal.travel")) {
            soundEngine.play(dim.soundTravel, vol, pitch);
        } else {
            soundEngine.play(sound, vol, pitch);
        }
    }

    @Redirect(
        method = "tickAi()V",
        at = @At(
            value ="INVOKE",
            target = "Lnet/minecraft/client/Minecraft;changeDimension()V"
        )
    )
    public void changeDimension(Minecraft minecraft) {
        DimensionBase.usePortal(this.portal);
    }

    public void setDimension(int i) {
        portal = i;
    }

    public List<PlayerBase> playerBases;

    public List<PlayerBase> getPlayerBases() {
        return playerBases;
    }

    @Inject(
        // target = @Desc(
        //     owner = InputPlayerEntity.class,
        //     value = "<init>",
        //     args = {Minecraft.class, World.class, Session.class, int.class}
        // ),
        method = "<init>(Lnet/minecraft/client/Minecraft;Lnet/minecraft/world/World;Lnet/minecraft/client/Session;I)V",
        at = @At("TAIL")
    )
    public void injectPlayerBases(Minecraft mc, World world, Session session, int worldId, CallbackInfo ci) {
        this.playerBases = PlayerAPI.playerInit((InputPlayerEntity)(Object)this);
    }

    @Override
    public boolean damage(Entity entity, int i) {
        if (PlayerAPI.attackEntityFrom((InputPlayerEntity)(Object)this, entity, i)) {
            return false;
        }
        return super.damage(entity, i);
    }

    @Override
    public void onKilled(Entity entity) {
        if (PlayerAPI.onDeath((InputPlayerEntity)(Object)this, entity)) {
            return;
        }
        super.onKilled(entity);
    }

    @Inject(
        // target = @Desc(
        //     owner = InputPlayerEntity.class,
        //     value = "tickDespawn"
        // ),
        method = "tickDespawn()V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void tickDespawn(CallbackInfo ci) {
        if (PlayerAPI.updatePlayerActionState((InputPlayerEntity)(Object)this)) {
            ci.cancel();
        }
        super.tickDespawn();
    }

    public void superUpdatePlayerActionState() {
        super.tickDespawn();
    }

    @Inject(
        // target = @Desc(
        //     owner = InputPlayerEntity.class,
        //     value = "tickAi"
        // ),
        method = "tickAi()V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void tickAi(CallbackInfo ci) {
        if (PlayerAPI.onLivingUpdate((InputPlayerEntity)(Object)this)) {
            ci.cancel();
        }
    }

    public void superOnLivingUpdate() {
        super.tickAi();
    }

    public void superOnUpdate() {
        super.tick();
    }

    public void updateVelocity(float f, float f1, float f2) {
        if (PlayerAPI.moveFlying((InputPlayerEntity)(Object)this, f, f1, f2)) {
            return;
        }
        super.updateVelocity(f, f1, f2);
    }

    @Override
    protected boolean canClimb() {
        return PlayerAPI.canTriggerWalking((InputPlayerEntity)(Object)this, super.canClimb());
    }

    @Inject(
        // target = @Desc(
        //     owner = InputPlayerEntity.class,
        //     value = "m_3741133",
        //     args = {int.class, boolean.class}
        // ),
        method = "m_3741133(IZ)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void m_3741133(int i, boolean flag, CallbackInfo ci) {
        if (PlayerAPI.handleKeyPress((InputPlayerEntity)(Object)this, i, flag)) {
            ci.cancel();
        }
    }

    @Inject(
        // target = @Desc(
        //     owner = InputPlayerEntity.class,
        //     value = "writeCustomNbt",
        //     args = {NbtCompound.class}
        // ),
        method = "writeCustomNbt(Lnet/minecraft/nbt/NbtCompound;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void writeCustomNbt(NbtCompound nbttagcompound, CallbackInfo ci) {
        if (PlayerAPI.writeEntityToNBT((InputPlayerEntity)(Object)this, nbttagcompound)) {
            ci.cancel();
        }
    }

    @Inject(
        // target = @Desc(
        //     owner = InputPlayerEntity.class,
        //     value = "readCustomNbt",
        //     args = {NbtCompound.class}
        // ),
        method = "readCustomNbt(Lnet/minecraft/nbt/NbtCompound;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void readCustomNbt(NbtCompound nbttagcompound, CallbackInfo ci) {
        if (PlayerAPI.readEntityFromNBT((InputPlayerEntity)(Object)this, nbttagcompound)) {
            ci.cancel();
        }
    }

    @Inject(
        // target = @Desc(
        //     owner = InputPlayerEntity.class,
        //     value = "closeMenu"
        // ),
        method = "closeMenu()V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void closeMenu(CallbackInfo ci) {
        if (PlayerAPI.onExitGUI((InputPlayerEntity)(Object)this)) {
            ci.cancel();
        }
    }

    @Inject(
        // target = @Desc(
        //     owner = InputPlayerEntity.class,
        //     value = "openSignEditor",
        //     args = {SignBlockEntity.class}
        // ),
        method = "openSignEditor(Lnet/minecraft/block/entity/SignBlockEntity;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void openSignEditor(SignBlockEntity tileentitysign, CallbackInfo ci) {
        if (PlayerAPI.displayGUIEditSign((InputPlayerEntity)(Object)this, tileentitysign)) {
            ci.cancel();
        }
    }

    @Inject(
        // target = @Desc(
        //     owner = InputPlayerEntity.class,
        //     value = "openInventoryMenu",
        //     args = {Inventory.class}
        // ),
        method = "openInventoryMenu(Lnet/minecraft/inventory/Inventory;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void openInventoryMenu(Inventory iinventory, CallbackInfo ci) {
        if (PlayerAPI.displayGUIChest((InputPlayerEntity)(Object)this, iinventory)) {
            ci.cancel();
        }
    }

    @Inject(
        // target = @Desc(
        //     owner = InputPlayerEntity.class,
        //     value = "openCraftingTableMenu",
        //     args = {int.class, int.class, int.class}
        // ),
        method = "openCraftingTableMenu(III)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void openCraftingTableMenu(int i, int j, int k, CallbackInfo ci) {
        if (PlayerAPI.displayWorkbenchGUI((InputPlayerEntity)(Object)this, i, j, k)) {
            ci.cancel();
        }
    }

    @Inject(
        // target = @Desc(
        //     owner = InputPlayerEntity.class,
        //     value = "openFurnaceMenu",
        //     args = {FurnaceBlockEntity.class}
        // ),
        method = "openFurnaceMenu(Lnet/minecraft/block/entity/FurnaceBlockEntity;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void openFurnaceMenu(FurnaceBlockEntity tileentityfurnace, CallbackInfo ci) {
        if (PlayerAPI.displayGUIFurnace((InputPlayerEntity)(Object)this, tileentityfurnace)) {
            ci.cancel();
        }
    }

    @Inject(
        // target = @Desc(
        //     owner = InputPlayerEntity.class,
        //     value = "openDispenserMenu",
        //     args = {DispenserBlockEntity.class}
        // ),
        method = "openDispenserMenu(Lnet/minecraft/block/entity/DispenserBlockEntity;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void openDispenserMenu(DispenserBlockEntity tileentitydispenser, CallbackInfo ci) {
        if (PlayerAPI.displayGUIDispenser((InputPlayerEntity)(Object)this, tileentitydispenser)) {
            ci.cancel();
        }
    }

    @Overwrite
    public int m_1513977() {
        return PlayerAPI.getPlayerArmorValue((InputPlayerEntity)(Object)this, this.inventory.getArmorProtectionValue());
    }

    @Override
    public void remove() {
        if (PlayerAPI.setEntityDead((InputPlayerEntity)(Object)this)) {
            return;
        }
        super.remove();
    }

    @Override
    public double getSquaredDistanceTo(double d, double d1, double d2) {
        return PlayerAPI.getDistanceSq((InputPlayerEntity)(Object)this, d, d1, d2, super.getSquaredDistanceTo(d, d1, d2));
    }

    @Override
    public boolean isInWater() {
        return PlayerAPI.isInWater((InputPlayerEntity)(Object)this, super.isInWater());
    }

    @Inject(
        // target = @Desc(
        //     owner = InputPlayerEntity.class,
        //     value = "isSneaking",
        //     ret = boolean.class
        // ),
        method = "isSneaking()Z",
        at = @At("HEAD"),
        cancellable = true
    )
    public void isSneaking(CallbackInfoReturnable<Boolean> ci) {
        ci.setReturnValue(PlayerAPI.isSneaking((InputPlayerEntity)(Object)this, this.input.sneaking && !this.sleeping));
    }

    @Override
    public float getMiningSpeed(Block block) {
        return PlayerAPI.getCurrentPlayerStrVsBlock((InputPlayerEntity)(Object)this, block, super.getMiningSpeed(block));
    }

    @Override
    public void heal(int i) {
        if (PlayerAPI.heal((InputPlayerEntity)(Object)this, i)) {
            return;
        }
        super.heal(i);
    }

    @Inject(
        // target = @Desc(
        //     owner = InputPlayerEntity.class,
        //     value = "tryRespawn"
        // ),
        method = "tryRespawn()V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void tryRespawn(CallbackInfo ci) {
        DimensionBase.respawn(false, 0);

        if (PlayerAPI.respawn((InputPlayerEntity)(Object)this)) {
        }
        ci.cancel();
    }

    @Inject(
        // target = @Desc(
        //     owner = InputPlayerEntity.class,
        //     value = "pushAwayFrom",
        //     args = { double.class, double.class, double.class },
        //     ret = boolean.class
        // ),
        method = "pushAwayFrom(DDD)Z",
        at = @At("HEAD"),
        cancellable = true
    )
    protected void pushAwayFrom(double d, double d1, double d2, CallbackInfoReturnable<Boolean> ci) {
        if (PlayerAPI.pushOutOfBlocks((InputPlayerEntity)(Object)this, d, d1, d2)) {
            ci.setReturnValue(false);
        }
    }

    public PlayerEntity__SleepAllowedStatus superSleepInBedAt(int i, int j, int k) {
        return super.trySleep(i, j, k);
    }

    public Minecraft getMc() {
        return this.minecraft;
    }

    public void superMoveEntity(double d, double d1, double d2) {
        super.move(d, d1, d2);
    }

    public void setMoveForward(float f) {
        this.forwardSpeed = f;
    }

    public void setMoveStrafing(float f) {
        this.sidewaysSpeed = f;
    }

    public void setIsJumping(boolean flag) {
        this.jumping = flag;
    }

    @Override
    public float getBrightness(float f) {
        return PlayerAPI.getEntityBrightness((InputPlayerEntity)(Object)this, f, super.getBrightness(f));
    }

    @Override
    public void tick() {
        PlayerAPI.beforeUpdate((InputPlayerEntity)(Object)this);
        if (!PlayerAPI.onUpdate((InputPlayerEntity)(Object)this)) {
            super.tick();
        }
        PlayerAPI.afterUpdate((InputPlayerEntity)(Object)this);
    }

    public void superMoveFlying(float f, float f1, float f2) {
        super.updateVelocity(f, f1, f2);
    }

    @Override
    public void move(double d, double d1, double d2) {
        PlayerAPI.beforeMoveEntity((InputPlayerEntity)(Object)this, d, d1, d2);
        if (!PlayerAPI.moveEntity((InputPlayerEntity)(Object)this, d, d1, d2)) {
            super.move(d, d1, d2);
        }
        PlayerAPI.afterMoveEntity((InputPlayerEntity)(Object)this, d, d1, d2);
    }

    @Override
    public PlayerEntity__SleepAllowedStatus trySleep(int i, int j, int k) {
        PlayerAPI.beforeSleepInBedAt((InputPlayerEntity)(Object)this, i, j, k);
        PlayerEntity__SleepAllowedStatus enumstatus = PlayerAPI.sleepInBedAt((InputPlayerEntity)(Object)this, i, j, k);
        if (enumstatus == null) {
            return super.trySleep(i, j, k);
        }
        return enumstatus;
    }

    public void doFall(float fallDist) {
        super.applyFallDamage(fallDist);
    }

    public float getFallDistance() {
        return this.fallDistance;
    }

    public boolean getSleeping() {
        return this.sleeping;
    }

    public boolean getJumping() {
        return this.jumping;
    }

    public void doJump() {
        this.jump();
    }

    public Random getRandom() {
        return this.random;
    }

    public void setFallDistance(float f) {
        this.fallDistance = f;
    }

    public void setYSize(float f) {
        this.eyeHeightSneakOffset = f;
    }

    @Override
    public void moveEntityWithVelocity(float f, float f1) {
        if (PlayerAPI.moveEntityWithHeading((InputPlayerEntity)(Object)this, f, f1)) {
            return;
        }
        super.moveEntityWithVelocity(f, f1);
    }

    @Override
    public boolean isClimbing() {
        return PlayerAPI.isOnLadder((InputPlayerEntity)(Object)this, super.isClimbing());
    }

    public void setActionState(float newMoveStrafing, float newMoveForward, boolean newIsJumping) {
        this.sidewaysSpeed = newMoveStrafing;
        this.forwardSpeed = newMoveForward;
        this.jumping = newIsJumping;
    }

    @Override
    public boolean isSubmergedIn(Material material) {
        return PlayerAPI.isInsideOfMaterial((InputPlayerEntity)(Object)this, material, super.isSubmergedIn(material));
    }

    @Override
    public void dropItem() {
        if (PlayerAPI.dropCurrentItem((InputPlayerEntity)(Object)this)) {
            return;
        }
        super.dropItem();
    }

    @Override
    public void dropItem(ItemStack itemstack) {
        if (PlayerAPI.dropPlayerItem((InputPlayerEntity)(Object)this, itemstack)) {
            return;
        }
        super.dropItem(itemstack);
    }

    public boolean superIsInsideOfMaterial(Material material) {
        return super.isSubmergedIn(material);
    }

    public float superGetEntityBrightness(float f) {
        return super.getBrightness(f);
    }

    @Override
    public void addMessage(String s) {
        PlayerAPI.sendChatMessage((InputPlayerEntity)(Object)this, s);
    }

    @Override
    protected String getHurtSound() {
        String result = PlayerAPI.getHurtSound((InputPlayerEntity)(Object)this);
        if (result != null) {
            return result;
        }
        return super.getHurtSound();
    }

    public String superGetHurtSound() {
        return super.getHurtSound();
    }

    public float superGetCurrentPlayerStrVsBlock(Block block) {
        return super.getMiningSpeed(block);
    }

    @Override
    public boolean canBreakBlock(Block block) {
        Boolean result = PlayerAPI.canHarvestBlock((InputPlayerEntity)(Object)this, block);
        if (result != null) {
            return result;
        }
        return super.canBreakBlock(block);
    }

    public boolean superCanHarvestBlock(Block block) {
        return super.canBreakBlock(block);
    }

    @Override
    protected void applyFallDamage(float f) {
        if (!PlayerAPI.fall((InputPlayerEntity)(Object)this, f)) {
            super.applyFallDamage(f);
        }
    }

    public void superFall(float f) {
        super.applyFallDamage(f);
    }

    protected void jump() {
        if (!PlayerAPI.jump((InputPlayerEntity)(Object)this)) {
            super.jump();
        }
    }

    public void superJump() {
        super.jump();
    }

    @Override
    protected void applyDamage(int i) {
        if (!PlayerAPI.damageEntity((InputPlayerEntity)(Object)this, i)) {
            super.applyDamage(i);
        }
    }

    protected void superDamageEntity(int i) {
        super.applyDamage(i);
    }

    @Override
    public double getSquaredDistanceTo(Entity entity) {
        Double result = PlayerAPI.getDistanceSqToEntity((InputPlayerEntity)(Object)this, entity);
        if (result != null) {
            return result;
        }
        return super.getSquaredDistanceTo(entity);
    }

    public double superGetDistanceSqToEntity(Entity entity) {
        return super.getSquaredDistanceTo(entity);
    }

    @Override
    public void attack(Entity entity) {
        if (!PlayerAPI.attackTargetEntityWithCurrentItem((InputPlayerEntity)(Object)this, entity)) {
            super.attack(entity);
        }
    }

    public void superAttackTargetEntityWithCurrentItem(Entity entity) {
        super.attack(entity);
    }

    @Override
    public boolean checkWaterCollisions() {
        Boolean result = PlayerAPI.handleWaterMovement((InputPlayerEntity)(Object)this);
        if (result != null) {
            return result;
        }
        return super.checkWaterCollisions();
    }

    public boolean superHandleWaterMovement() {
        return super.checkWaterCollisions();
    }

    @Override
    public boolean isInLava() {
        Boolean result = PlayerAPI.handleLavaMovement((InputPlayerEntity)(Object)this);
        if (result != null) {
            return result;
        }
        return super.isInLava();
    }

    public boolean superHandleLavaMovement() {
        return super.isInLava();
    }

    @Override
    public void dropItem(ItemStack itemstack, boolean flag) {
        if (!PlayerAPI.dropPlayerItemWithRandomChoice((InputPlayerEntity)(Object)this, itemstack, flag)) {
            super.dropItem(itemstack, flag);
        }
    }

    public void superDropPlayerItemWithRandomChoice(ItemStack itemstack, boolean flag) {
        super.dropItem(itemstack, flag);
    }
}

