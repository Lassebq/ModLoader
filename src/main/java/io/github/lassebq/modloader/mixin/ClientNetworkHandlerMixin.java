package io.github.lassebq.modloader.mixin;

import java.lang.reflect.Field;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.lassebq.modloader.ModLoader;
import io.github.lassebq.modloader.ModLoaderMp;
import io.github.lassebq.modloader.NetClientHandlerEntity;
import net.minecraft.block.Block;
import net.minecraft.client.network.handler.ClientNetworkHandler;
import net.minecraft.client.world.MultiplayerWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.FishingBobberEntity;
import net.minecraft.entity.PrimedTntEntity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.EggEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.network.packet.AddEntityPacket;
import net.minecraft.world.World;

@Mixin(ClientNetworkHandler.class)
public abstract class ClientNetworkHandlerMixin {
    @Shadow
    private MultiplayerWorld world;

    @Shadow
    public abstract Entity getEntity(int networkId);

    public void handleAddEntity(AddEntityPacket packet) {
        double var2 = (double) packet.x / 32.0;
        double var4 = (double) packet.y / 32.0;
        double var6 = (double) packet.z / 32.0;
        Entity var8 = null;
        if (packet.type == 10) {
            var8 = new MinecartEntity(this.world, var2, var4, var6, 0);
        }

        if (packet.type == 11) {
            var8 = new MinecartEntity(this.world, var2, var4, var6, 1);
        }

        if (packet.type == 12) {
            var8 = new MinecartEntity(this.world, var2, var4, var6, 2);
        }

        if (packet.type == 90) {
            var8 = new FishingBobberEntity(this.world, var2, var4, var6);
        }

        if (packet.type == 60) {
            var8 = new ArrowEntity(this.world, var2, var4, var6);
        }

        if (packet.type == 61) {
            var8 = new SnowballEntity(this.world, var2, var4, var6);
        }

        if (packet.type == 63) {
            var8 = new ProjectileEntity(this.world, var2, var4, var6, (double) packet.velocityX / 8000.0,
                    (double) packet.velocityY / 8000.0, (double) packet.velocityZ / 8000.0);
            packet.data = 0;
        }

        if (packet.type == 62) {
            var8 = new EggEntity(this.world, var2, var4, var6);
        }

        if (packet.type == 1) {
            var8 = new BoatEntity(this.world, var2, var4, var6);
        }

        if (packet.type == 50) {
            var8 = new PrimedTntEntity(this.world, var2, var4, var6);
        }

        if (packet.type == 70) {
            var8 = new FallingBlockEntity(this.world, var2, var4, var6, Block.SAND.id);
        }

        if (packet.type == 71) {
            var8 = new FallingBlockEntity(this.world, var2, var4, var6, Block.GRAVEL.id);
        }

        NetClientHandlerEntity netclienthandlerentity = ModLoaderMp.HandleNetClientHandlerEntities(packet.type);
        if (netclienthandlerentity != null) {
            try {
                var8 = netclienthandlerentity.entityClass.getConstructor(World.class, Double.TYPE, Double.TYPE, Double.TYPE).newInstance(this.world, var2, var4, var6);
                if (netclienthandlerentity.entityHasOwner) {
                    Field field = netclienthandlerentity.entityClass.getField("owner");
                    if (!Entity.class.isAssignableFrom(field.getType())) {
                        throw new Exception(String.format(
                                "Entity's owner field must be of type Entity, but it is of type %s.", field.getType()));
                    }

                    Entity entity1 = this.getEntity(packet.data);
                    if (entity1 == null) {
                        ModLoaderMp.Log("Received spawn packet for entity with owner, but owner was not found.");
                    } else {
                        if (!field.getType().isAssignableFrom(entity1.getClass())) {
                            throw new Exception(String.format(
                                    "Tried to assign an entity of type %s to entity owner, which is of type %s.",
                                    entity1.getClass(), field.getType()));
                        }

                        field.set(var8, entity1);
                    }
                }
            } catch (Exception var12) {
                Exception exception = var12;
                ModLoader.getLogger().throwing("NetClientHandler", "handleVehicleSpawn", exception);
                ModLoader.ThrowException(String.format("Error initializing entity of type %s.", packet.type),
                        exception);
                return;
            }
        }

        if (var8 != null) {
            var8.packetX = packet.x;
            var8.packetY = packet.y;
            var8.packetZ = packet.z;
            var8.yaw = 0.0F;
            var8.pitch = 0.0F;
            var8.networkId = packet.id;
            this.world.addEntity(packet.id, var8);
            if (packet.data > 0) {
                if (packet.type == 60) {
                    Entity var9 = this.getEntity(packet.data);
                    if (var9 instanceof LivingEntity) {
                        ((ArrowEntity) var8).shooter = (LivingEntity) var9;
                    }
                }

                var8.setVelocity((double) packet.velocityX / 8000.0, (double) packet.velocityY / 8000.0,
                        (double) packet.velocityZ / 8000.0);
            }
        }

    }
}
