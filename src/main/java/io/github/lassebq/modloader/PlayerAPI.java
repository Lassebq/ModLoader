package io.github.lassebq.modloader;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.living.player.InputPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity__SleepAllowedStatus;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class PlayerAPI {
    public static List<Class<PlayerBase>> playerBaseClasses = new ArrayList<Class<PlayerBase>>();

    public static void RegisterPlayerBase(Class<PlayerBase> pb) {
        playerBaseClasses.add(pb);
    }

    public static PlayerBase getPlayerBase(InputPlayerEntity player, Class<PlayerBase> pb) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!pb.isInstance(accessor.getPlayerBases().get(i))) continue;
            return accessor.getPlayerBases().get(i);
        }
        return null;
    }

    public static List<PlayerBase> playerInit(InputPlayerEntity player) {
        ArrayList<PlayerBase> playerBases = new ArrayList<PlayerBase>();
        for (int i = 0; i < playerBaseClasses.size(); ++i) {
            try {
                playerBases.add(playerBaseClasses.get(i).getDeclaredConstructor(InputPlayerEntity.class).newInstance(new Object[]{player}));
                continue;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return playerBases;
    }

    public static boolean onLivingUpdate(InputPlayerEntity player) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean override = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).onLivingUpdate()) continue;
            override = true;
        }
        return override;
    }

    public static boolean respawn(InputPlayerEntity player) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean override = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).respawn()) continue;
            override = true;
        }
        return override;
    }

    public static boolean moveFlying(InputPlayerEntity player, float x, float y, float z) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean override = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).moveFlying(x, y, z)) continue;
            override = true;
        }
        return override;
    }

    public static boolean updatePlayerActionState(InputPlayerEntity player) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean override = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).updatePlayerActionState()) continue;
            override = true;
        }
        return override;
    }

    public static boolean handleKeyPress(InputPlayerEntity player, int j, boolean flag) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean override = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).handleKeyPress(j, flag)) continue;
            override = true;
        }
        return override;
    }

    public static boolean writeEntityToNBT(InputPlayerEntity player, NbtCompound tag) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean override = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).writeEntityToNBT(tag)) continue;
            override = true;
        }
        return override;
    }

    public static boolean readEntityFromNBT(InputPlayerEntity player, NbtCompound tag) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean override = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).readEntityFromNBT(tag)) continue;
            override = true;
        }
        return override;
    }

    public static boolean onExitGUI(InputPlayerEntity player) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean override = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).onExitGUI()) continue;
            override = true;
        }
        return override;
    }

    public static boolean setEntityDead(InputPlayerEntity player) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean override = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).setEntityDead()) continue;
            override = true;
        }
        return override;
    }

    public static boolean onDeath(InputPlayerEntity player, Entity killer) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean override = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).onDeath(killer)) continue;
            override = true;
        }
        return override;
    }

    public static boolean attackEntityFrom(InputPlayerEntity player, Entity attacker, int damage) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean override = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).attackEntityFrom(attacker, damage)) continue;
            override = true;
        }
        return override;
    }

    public static double getDistanceSq(InputPlayerEntity player, double d, double d1, double d2, double answer) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            answer = accessor.getPlayerBases().get(i).getDistanceSq(d, d1, d2, answer);
        }
        return answer;
    }

    public static boolean isInWater(InputPlayerEntity player, boolean inWater) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            inWater = accessor.getPlayerBases().get(i).isInWater(inWater);
        }
        return inWater;
    }

    public static boolean canTriggerWalking(InputPlayerEntity player, boolean canTrigger) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            canTrigger = accessor.getPlayerBases().get(i).canTriggerWalking(canTrigger);
        }
        return canTrigger;
    }

    public static boolean heal(InputPlayerEntity player, int j) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean override = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).heal(j)) continue;
            override = true;
        }
        return override;
    }

    public static int getPlayerArmorValue(InputPlayerEntity player, int armor) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            armor = accessor.getPlayerBases().get(i).getPlayerArmorValue(armor);
        }
        return armor;
    }

    public static float getCurrentPlayerStrVsBlock(InputPlayerEntity player, Block block, float f) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            f = accessor.getPlayerBases().get(i).getCurrentPlayerStrVsBlock(block, f);
        }
        return f;
    }

    public static boolean moveEntity(InputPlayerEntity player, double d, double d1, double d2) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean override = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).moveEntity(d, d1, d2)) continue;
            override = true;
        }
        return override;
    }

    public static PlayerEntity__SleepAllowedStatus sleepInBedAt(InputPlayerEntity player, int x, int y, int z) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        PlayerEntity__SleepAllowedStatus status = null;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            status = accessor.getPlayerBases().get(i).sleepInBedAt(x, y, z, status);
        }
        return status;
    }

    public static float getEntityBrightness(InputPlayerEntity player, float f, float brightness) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            brightness = accessor.getPlayerBases().get(i).getEntityBrightness(f, brightness);
        }
        return brightness;
    }

    public static boolean pushOutOfBlocks(InputPlayerEntity player, double d, double d1, double d2) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean override = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).pushOutOfBlocks(d, d1, d2)) continue;
            override = true;
        }
        return override;
    }

    public static boolean onUpdate(InputPlayerEntity player) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean override = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).onUpdate()) continue;
            override = true;
        }
        return override;
    }

    public static void afterUpdate(InputPlayerEntity player) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            accessor.getPlayerBases().get(i).afterUpdate();
        }
    }

    public static boolean moveEntityWithHeading(InputPlayerEntity player, float f, float f1) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean override = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).moveEntityWithHeading(f, f1)) continue;
            override = true;
        }
        return override;
    }

    public static boolean isOnLadder(InputPlayerEntity player, boolean onLadder) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            onLadder = accessor.getPlayerBases().get(i).isOnLadder(onLadder);
        }
        return onLadder;
    }

    public static boolean isInsideOfMaterial(InputPlayerEntity player, Material material, boolean inMaterial) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            inMaterial = accessor.getPlayerBases().get(i).isInsideOfMaterial(material, inMaterial);
        }
        return inMaterial;
    }

    public static boolean isSneaking(InputPlayerEntity player, boolean sneaking) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            sneaking = accessor.getPlayerBases().get(i).isSneaking(sneaking);
        }
        return sneaking;
    }

    public static boolean dropCurrentItem(InputPlayerEntity player) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean override = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).dropCurrentItem()) continue;
            override = true;
        }
        return override;
    }

    public static boolean dropPlayerItem(InputPlayerEntity player, ItemStack itemstack) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean override = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).dropPlayerItem(itemstack)) continue;
            override = true;
        }
        return override;
    }

    public static boolean displayGUIEditSign(InputPlayerEntity player, SignBlockEntity sign) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean override = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).displayGUIEditSign(sign)) continue;
            override = true;
        }
        return override;
    }

    public static boolean displayGUIChest(InputPlayerEntity player, Inventory inventory) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean override = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).displayGUIChest(inventory)) continue;
            override = true;
        }
        return override;
    }

    public static boolean displayWorkbenchGUI(InputPlayerEntity player, int i, int j, int k) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean override = false;
        for (int n = 0; n < accessor.getPlayerBases().size(); ++n) {
            if (!accessor.getPlayerBases().get(n).displayWorkbenchGUI(i, j, k)) continue;
            override = true;
        }
        return override;
    }

    public static boolean displayGUIFurnace(InputPlayerEntity player, FurnaceBlockEntity furnace) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean override = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).displayGUIFurnace(furnace)) continue;
            override = true;
        }
        return override;
    }

    public static boolean displayGUIDispenser(InputPlayerEntity player, DispenserBlockEntity dispenser) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean override = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).displayGUIDispenser(dispenser)) continue;
            override = true;
        }
        return override;
    }

    public static boolean sendChatMessage(InputPlayerEntity player, String s) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean flag = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).sendChatMessage(s)) continue;
            flag = true;
        }
        return flag;
    }

    public static String getHurtSound(InputPlayerEntity player) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        String result = null;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            String baseResult = accessor.getPlayerBases().get(i).getHurtSound(result);
            if (baseResult == null) continue;
            result = baseResult;
        }
        return result;
    }

    public static Boolean canHarvestBlock(InputPlayerEntity player, Block block) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        Boolean result = null;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            Boolean baseResult = accessor.getPlayerBases().get(i).canHarvestBlock(block, result);
            if (baseResult == null) continue;
            result = baseResult;
        }
        return result;
    }

    public static boolean fall(InputPlayerEntity player, float f) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean flag = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).fall(f)) continue;
            flag = true;
        }
        return flag;
    }

    public static boolean jump(InputPlayerEntity player) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean flag = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).jump()) continue;
            flag = true;
        }
        return flag;
    }

    public static boolean damageEntity(InputPlayerEntity player, int i1) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean flag = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).damageEntity(i1)) continue;
            flag = true;
        }
        return flag;
    }

    public static Double getDistanceSqToEntity(InputPlayerEntity player, Entity entity) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        Double result = null;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            Double baseResult = accessor.getPlayerBases().get(i).getDistanceSqToEntity(entity, result);
            if (baseResult == null) continue;
            result = baseResult;
        }
        return result;
    }

    public static boolean attackTargetEntityWithCurrentItem(InputPlayerEntity player, Entity entity) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean flag = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).attackTargetEntityWithCurrentItem(entity)) continue;
            flag = true;
        }
        return flag;
    }

    public static Boolean handleWaterMovement(InputPlayerEntity player) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        Boolean result = null;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            Boolean baseResult = accessor.getPlayerBases().get(i).handleWaterMovement(result);
            if (baseResult == null) continue;
            result = baseResult;
        }
        return result;
    }

    public static Boolean handleLavaMovement(InputPlayerEntity player) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        Boolean result = null;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            Boolean baseResult = accessor.getPlayerBases().get(i).handleLavaMovement(result);
            if (baseResult == null) continue;
            result = baseResult;
        }
        return result;
    }

    public static boolean dropPlayerItemWithRandomChoice(InputPlayerEntity player, ItemStack itemstack, boolean flag1) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        boolean flag = false;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            if (!accessor.getPlayerBases().get(i).dropPlayerItemWithRandomChoice(itemstack, flag1)) continue;
            flag = true;
        }
        return flag;
    }

    public static void beforeUpdate(InputPlayerEntity player) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            accessor.getPlayerBases().get(i).beforeUpdate();
        }
    }

    public static void beforeMoveEntity(InputPlayerEntity player, double d, double d1, double d2) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            accessor.getPlayerBases().get(i).beforeMoveEntity(d, d1, d2);
        }
    }

    public static void afterMoveEntity(InputPlayerEntity player, double d, double d1, double d2) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            accessor.getPlayerBases().get(i).afterMoveEntity(d, d1, d2);
        }
    }

    public static void beforeSleepInBedAt(InputPlayerEntity player, int i1, int j, int k) {
        PlayerBaseAccessor accessor = (PlayerBaseAccessor)player;
        for (int i = 0; i < accessor.getPlayerBases().size(); ++i) {
            accessor.getPlayerBases().get(i).beforeSleepInBedAt(i1, j, k);
        }
    }
}
