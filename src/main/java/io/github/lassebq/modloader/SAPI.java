package io.github.lassebq.modloader;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.achievement.AchievementStat;
import net.minecraft.world.World;

public class SAPI {
    public static boolean usingText;
    private static ArrayList<IInterceptHarvest> harvestIntercepts = new ArrayList<>();
    private static ArrayList<IInterceptBlockSet> setIntercepts = new ArrayList<>();
    private static ArrayList<IReach> reaches = new ArrayList<>();
    private static ArrayList<String> dngMobs = new ArrayList<>();
    private static ArrayList<DungeonLoot> dngItems = new ArrayList<>();
    private static ArrayList<DungeonLoot> dngGuaranteed = new ArrayList<>();
    private static boolean dngAddedMobs;
    private static boolean dngAddedItems;
    public static int acCurrentPage;
    private static ArrayList<Integer> acHidden = new ArrayList<>();
    private static ArrayList<ACPage> acPages = new ArrayList<>();
    public static final ACPage acDefaultPage = new ACPage();

    static {
        showText();
    }

    public SAPI() {
    }

    public static void showText() {
        if (!usingText) {
            System.out.println("Using ShockAhPI r5.1");
            usingText = true;
        }

    }

    public static Minecraft getMinecraftInstance() {
        return ModLoader.getMinecraftInstance();
    }

    public static void interceptAdd(IInterceptHarvest iinterceptharvest) {
        harvestIntercepts.add(iinterceptharvest);
    }

    public static boolean interceptHarvest(World world, PlayerEntity entityplayer, Loc loc, int i, int j) {
        for(IInterceptHarvest iinterceptharvest : harvestIntercepts) {
            if(iinterceptharvest.canIntercept(world, entityplayer, loc, i, j)) {
                iinterceptharvest.intercept(world, entityplayer, loc, i, j);
                return true;
            }
        }
        return false;
    }

    public static void drop(World world, Loc loc, ItemStack itemstack) {
        if (!world.isMultiplayer) {
            for(int i = 0; i < itemstack.size; ++i) {
                float f = 0.7F;
                double d = (double)(world.random.nextFloat() * f) + (double)(1.0F - f) * 0.5;
                double d1 = (double)(world.random.nextFloat() * f) + (double)(1.0F - f) * 0.5;
                double d2 = (double)(world.random.nextFloat() * f) + (double)(1.0F - f) * 0.5;
                ItemEntity entityitem = new ItemEntity(world, (double)loc.x() + d, (double)loc.y() + d1, (double)loc.z() + d2, new ItemStack(itemstack.itemId, 1, itemstack.getMetadata()));
                entityitem.pickUpDelay = 10;
                world.addEntity(entityitem);
            }

        }
    }

    public static void interceptAdd(IInterceptBlockSet iinterceptblockset) {
        setIntercepts.add(iinterceptblockset);
    }

    // FIXME implement
    public static int interceptBlockSet(World world, Loc loc, int i) {
        for(IInterceptBlockSet iinterceptblockset : setIntercepts) {
            if(iinterceptblockset.canIntercept(world, loc, i)) {
                return iinterceptblockset.intercept(world, loc, i);
            }
        }
        return i;
    }

    public static void reachAdd(IReach ireach) {
        reaches.add(ireach);
    }

    public static float reachGet() {
        ItemStack itemstack = getMinecraftInstance().player.inventory.getMainHandStack();

        for(IReach ireach : reaches) {
            if(ireach.reachItemMatches(itemstack)) {
                return ireach.getReach(itemstack);
            }
        }
        return 4.0F;
    }

    public static void dungeonAddMob(String s) {
        dungeonAddMob(s, 10);
    }

    public static void dungeonAddMob(String s, int i) {
        for(int j = 0; j < i; ++j) {
            dngMobs.add(s);
        }

    }

    public static void dungeonRemoveMob(String s) {
        for(int i = 0; i < dngMobs.size(); ++i) {
            if (dngMobs.get(i).equals(s)) {
                dngMobs.remove(i);
                --i;
            }
        }

    }

    public static void dungeonRemoveAllMobs() {
        dngAddedMobs = true;
        dngMobs.clear();
    }

    static void dungeonAddDefaultMobs() {
        int k;
        for(k = 0; k < 10; ++k) {
            dngMobs.add("Skeleton");
        }

        for(k = 0; k < 20; ++k) {
            dngMobs.add("Zombie");
        }

        for(k = 0; k < 10; ++k) {
            dngMobs.add("Spider");
        }

    }

    public static String dungeonGetRandomMob() {
        if (!dngAddedMobs) {
            dungeonAddDefaultMobs();
            dngAddedMobs = true;
        }

        return dngMobs.isEmpty() ? "Pig" : (String)dngMobs.get((new Random()).nextInt(dngMobs.size()));
    }

    public static void dungeonAddItem(DungeonLoot dungeonloot) {
        dungeonAddItem(dungeonloot, 100);
    }

    public static void dungeonAddItem(DungeonLoot dungeonloot, int i) {
        for(int j = 0; j < i; ++j) {
            dngItems.add(dungeonloot);
        }

    }

    public static void dungeonAddGuaranteedItem(DungeonLoot dungeonloot) {
        dngGuaranteed.add(dungeonloot);
    }

    public static int dungeonGetAmountOfGuaranteed() {
        return dngGuaranteed.size();
    }

    public static DungeonLoot dungeonGetGuaranteed(int i) {
        return dngGuaranteed.get(i);
    }

    public static void dungeonRemoveItem(int i) {
        int k;
        for(k = 0; k < dngItems.size(); ++k) {
            if (dngItems.get(k).loot.itemId == i) {
                dngItems.remove(k);
                --k;
            }
        }

        for(k = 0; k < dngGuaranteed.size(); ++k) {
            if (dngGuaranteed.get(k).loot.itemId == i) {
                dngGuaranteed.remove(k);
                --k;
            }
        }

    }

    public static void dungeonRemoveAllItems() {
        dngAddedItems = true;
        dngItems.clear();
        dngGuaranteed.clear();
    }

    static void dungeonAddDefaultItems() {
        int j2;
        for(j2 = 0; j2 < 100; ++j2) {
            dngItems.add(new DungeonLoot(new ItemStack(Item.SADDLE)));
        }

        for(j2 = 0; j2 < 100; ++j2) {
            dngItems.add(new DungeonLoot(new ItemStack(Item.IRON_INGOT), 1, 4));
        }

        for(j2 = 0; j2 < 100; ++j2) {
            dngItems.add(new DungeonLoot(new ItemStack(Item.BREAD)));
        }

        for(j2 = 0; j2 < 100; ++j2) {
            dngItems.add(new DungeonLoot(new ItemStack(Item.WHEAT), 1, 4));
        }

        for(j2 = 0; j2 < 100; ++j2) {
            dngItems.add(new DungeonLoot(new ItemStack(Item.GUNPOWDER), 1, 4));
        }

        for(j2 = 0; j2 < 100; ++j2) {
            dngItems.add(new DungeonLoot(new ItemStack(Item.STRING), 1, 4));
        }

        for(j2 = 0; j2 < 100; ++j2) {
            dngItems.add(new DungeonLoot(new ItemStack(Item.BUCKET)));
        }

        dngItems.add(new DungeonLoot(new ItemStack(Item.GOLDEN_APPLE)));

        for(j2 = 0; j2 < 50; ++j2) {
            dngItems.add(new DungeonLoot(new ItemStack(Item.REDSTONE), 1, 4));
        }

        for(j2 = 0; j2 < 5; ++j2) {
            dngItems.add(new DungeonLoot(new ItemStack(Item.RECORD_13)));
        }

        for(j2 = 0; j2 < 5; ++j2) {
            dngItems.add(new DungeonLoot(new ItemStack(Item.RECORD_CAT)));
        }

    }

    public static ItemStack dungeonGetRandomItem() {
        if (!dngAddedItems) {
            dungeonAddDefaultItems();
            dngAddedItems = true;
        }

        return dngItems.isEmpty() ? null : dngItems.get(new Random().nextInt(dngItems.size())).getStack();
    }

    public static void acPageAdd(ACPage acpage) {
        acPages.add(acpage);
    }

    public static void acHide(AchievementStat[] aachievement) {
        AchievementStat[] aachievement1 = aachievement;
        int j = aachievement.length;

        for(int i = 0; i < j; ++i) {
            AchievementStat achievement = aachievement1[i];
            acHidden.add(achievement.id);
        }

    }

    public static boolean acIsHidden(AchievementStat achievement) {
        return acHidden.contains(achievement.id);
    }

    public static ACPage acGetPage(AchievementStat achievement) {
        if (achievement == null) {
            return null;
        } else {
            for(ACPage acpage : acPages) {
                if(acpage.list.contains(achievement.id)) {
                    return acpage;
                }
            }
            return acDefaultPage;
        }
    }

    public static ACPage acGetCurrentPage() {
        return acPages.get(acCurrentPage);
    }

    public static String acGetCurrentPageTitle() {
        return acGetCurrentPage().title;
    }

    public static void acPageNext() {
        ++acCurrentPage;
        if (acCurrentPage > acPages.size() - 1) {
            acCurrentPage = 0;
        }

    }

    public static void acPagePrev() {
        --acCurrentPage;
        if (acCurrentPage < 0) {
            acCurrentPage = acPages.size() - 1;
        }

    }
}
