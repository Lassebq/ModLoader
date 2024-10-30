package io.github.lassebq.modloader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import net.minecraft.client.Minecraft;
import net.minecraft.world.PortalForcer;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;

public class DimensionBase {
    public static ArrayList<DimensionBase> list = new ArrayList<>();
    public static LinkedList<Integer> order = new LinkedList<>();
    public final int number;
    public final Class<? extends Dimension> worldProvider;
    public final Class<? extends PortalForcer> teleporter;
    public String name = "Dimension";
    public String soundTrigger = "portal.trigger";
    public String soundTravel = "portal.travel";

    static {
        new DimensionOverworld();
        new DimensionNether();
    }

    public static DimensionBase getDimByNumber(int number) {
        for(int i = 0; i < list.size(); ++i) {
            DimensionBase dim = (DimensionBase)list.get(i);
            if (dim.number == number) {
                return dim;
            }
        }

        return null;
    }

    public static DimensionBase getDimByProvider(Class<? extends Dimension> worldProvider) {
        for(int i = 0; i < list.size(); ++i) {
            DimensionBase dim = (DimensionBase)list.get(i);
            if (dim.worldProvider.getName().equals(worldProvider.getName())) {
                return dim;
            }
        }

        return null;
    }

    public Dimension getWorldProvider() {
        try {
            return this.worldProvider.newInstance();
        } catch (InstantiationException var2) {
            var2.printStackTrace();
        } catch (IllegalAccessException var3) {
            var3.printStackTrace();
        }

        return null;
    }

    public PortalForcer getTeleporter() {
        try {
            if (this.teleporter != null) {
                return this.teleporter.newInstance();
            }
        } catch (InstantiationException var2) {
            var2.printStackTrace();
        } catch (IllegalAccessException var3) {
            var3.printStackTrace();
        }

        return null;
    }

    public static void respawn(boolean bl, int i) {
        Minecraft minecraft = ModLoader.getMinecraftInstance();
        minecraft.m_9990242(bl, i);
    }

    public static void usePortal(int dimNumber) {
        usePortal(dimNumber, false);
    }

    public static void usePortal(int dimNumber, boolean resetOrder) {
        Minecraft game = ModLoader.getMinecraftInstance();
        int oldDimension = game.player.dimensionId;
        int newDimension = dimNumber;
        if (oldDimension == newDimension) {
            newDimension = 0;
        }

        game.world.removeEntity(game.player);
        game.player.removed = false;
        Loc loc = new Loc(game.player.x, game.player.z);
        if (newDimension != 0) {
            order.push(newDimension);
        }

        if (newDimension == 0 && !order.isEmpty()) {
            newDimension = order.pop();
        }

        if (oldDimension == newDimension) {
            newDimension = 0;
        }

        String str = "";

        Integer dim;
        for(Iterator<Integer> var8 = order.iterator(); var8.hasNext(); str = str + dim) {
            dim = var8.next();
            if (!str.isEmpty()) {
                str = str + ",";
            }
        }

        dim = null;
        DimensionBase dimOld = getDimByNumber(oldDimension);
        DimensionBase dimNew = getDimByNumber(newDimension);
        loc = dimOld.getDistanceScale(loc, true);
        loc = dimNew.getDistanceScale(loc, false);
        game.player.dimensionId = newDimension;
        game.player.refreshPositionAndAngles(loc.x, game.player.y, loc.z, game.player.yaw, game.player.pitch);
        game.world.updateEntity(game.player, false);
        World world = new World(game.world, dimNew.getWorldProvider());
        game.setWorld(world, (newDimension == 0 ? "Leaving" : "Entering") + " the " + (newDimension == 0 ? dimOld.name : dimNew.name), game.player);
        game.player.world = game.world;
        game.player.refreshPositionAndAngles(loc.x, game.player.y, loc.z, game.player.yaw, game.player.pitch);
        game.world.updateEntity(game.player, false);
        PortalForcer teleporter = dimNew.getTeleporter();
        if (teleporter == null) {
            teleporter = dimOld.getTeleporter();
        }

        teleporter.onDimensionChanged(game.world, game.player);
    }

    public DimensionBase(int number, Class<? extends Dimension> worldProvider, Class<PortalForcer> teleporter) {
        this.number = number;
        this.worldProvider = worldProvider;
        this.teleporter = teleporter;
        list.add(this);
    }

    public Loc getDistanceScale(Loc loc, boolean goingIn) {
        return loc;
    }
}
