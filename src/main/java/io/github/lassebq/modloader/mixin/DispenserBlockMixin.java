package io.github.lassebq.modloader.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import io.github.lassebq.modloader.ModLoader;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.EggEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {
    @Overwrite
    private void dispense(World world, int x, int y, int z, Random random) {
        int var6 = world.getBlockMetadata(x, y, z);
        byte var9 = 0;
        byte var10 = 0;
        if (var6 == 3) {
            var10 = 1;
        } else if (var6 == 2) {
            var10 = -1;
        } else if (var6 == 5) {
            var9 = 1;
        } else {
            var9 = -1;
        }

        DispenserBlockEntity var11 = (DispenserBlockEntity) world.getBlockEntity(x, y, z);
        ItemStack var12 = var11.pickNonEmptySlot();
        double var13 = (double) x + (double) var9 * 0.6 + 0.5;
        double var15 = (double) y + 0.5;
        double var17 = (double) z + (double) var10 * 0.6 + 0.5;
        if (var12 == null) {
            world.doEvent(1001, x, y, z, 0);
        } else {
			boolean handled = ModLoader.DispenseEntity(world, var13, var15, var17, var9, var10, var12);
			if(!handled) {
                if (var12.itemId == Item.ARROW.id) {
                    ArrowEntity var19 = new ArrowEntity(world, var13, var15, var17);
                    var19.m_6730880((double) var9, 0.10000000149011612, (double) var10, 1.1F, 6.0F);
                    var19.f_2094393 = true;
                    world.addEntity(var19);
                    world.doEvent(1002, x, y, z, 0);
                } else if (var12.itemId == Item.EGG.id) {
                    EggEntity var22 = new EggEntity(world, var13, var15, var17);
                    var22.m_4522800((double) var9, 0.10000000149011612, (double) var10, 1.1F, 6.0F);
                    world.addEntity(var22);
                    world.doEvent(1002, x, y, z, 0);
                } else if (var12.itemId == Item.SNOWBALL.id) {
                    SnowballEntity var23 = new SnowballEntity(world, var13, var15, var17);
                    var23.setVelocity((double) var9, 0.10000000149011612, (double) var10, 1.1F, 6.0F);
                    world.addEntity(var23);
                    world.doEvent(1002, x, y, z, 0);
                } else {
                    ItemEntity var24 = new ItemEntity(world, var13, var15 - 0.3, var17, var12);
                    double var20 = random.nextDouble() * 0.1 + 0.2;
                    var24.velocityX = (double) var9 * var20;
                    var24.velocityY = 0.20000000298023224;
                    var24.velocityZ = (double) var10 * var20;
                    var24.velocityX += random.nextGaussian() * 0.007499999832361937 * 6.0;
                    var24.velocityY += random.nextGaussian() * 0.007499999832361937 * 6.0;
                    var24.velocityZ += random.nextGaussian() * 0.007499999832361937 * 6.0;
                    world.addEntity(var24);
                    world.doEvent(1000, x, y, z, 0);
                }
            }

            world.doEvent(2000, x, y, z, var9 + 1 + (var10 + 1) * 3);
        }

    }
}
