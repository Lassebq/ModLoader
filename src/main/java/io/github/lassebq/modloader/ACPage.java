package io.github.lassebq.modloader;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.stat.achievement.AchievementStat;

public class ACPage {
    private static int nextID = 1;
    public final int id;
    public final String title;
    ArrayList<Integer> list = new ArrayList<>();

    public ACPage() {
        this.id = 0;
        this.title = "Minecraft";
        SAPI.acPageAdd(this);
    }

    public ACPage(String title) {
        this.id = nextID++;
        this.title = title;
        SAPI.acPageAdd(this);
    }

    public void addAchievements(AchievementStat... achievements) {
        AchievementStat[] var5 = achievements;
        int var4 = achievements.length;

        for(int var3 = 0; var3 < var4; ++var3) {
            AchievementStat achievement = var5[var3];
            this.list.add(achievement.id);
        }

    }

    public int bgGetSprite(Random random, int x, int y) {
        int sprite = Block.SAND.sprite;
        int rnd = random.nextInt(1 + y) + y / 2;
        if (rnd <= 37 && y != 35) {
            if (rnd == 22) {
                sprite = random.nextInt(2) == 0 ? Block.DIAMOND_ORE.sprite : Block.REDSTONE_ORE.sprite;
            } else if (rnd == 10) {
                sprite = Block.IRON_ORE.sprite;
            } else if (rnd == 8) {
                sprite = Block.COAL_ORE.sprite;
            } else if (rnd > 4) {
                sprite = Block.STONE.sprite;
            } else if (rnd > 0) {
                sprite = Block.DIRT.sprite;
            }
        } else {
            sprite = Block.BEDROCK.sprite;
        }

        return sprite;
    }
}
