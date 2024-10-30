package io.github.lassebq.modloader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import org.lwjgl.input.Keyboard;
import org.mcphackers.launchwrapper.loader.LaunchClassLoader;
import org.mcphackers.launchwrapper.util.UnsafeUtils;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Session;
import net.minecraft.client.crash.CrashSummary;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.BlockRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.texture.TextureAtlas;
import net.minecraft.client.render.texture.TextureManager;
import net.minecraft.client.resource.pack.TexturePacks;
import net.minecraft.crafting.CraftingManager;
import net.minecraft.crafting.SmeltingManager;
import net.minecraft.crafting.recipe.Recipe;
import net.minecraft.entity.Entities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.MobCategory;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.locale.LanguageManager;
import net.minecraft.stat.ItemStat;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.stat.achievement.AchievementStat;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome__SpawnEntry;
import net.minecraft.world.biome.HellBiome;
import net.minecraft.world.biome.TheEndBiome;
import net.minecraft.world.chunk.ChunkSource;

public final class ModLoader {
	private static final List<TextureAtlas> animList = new LinkedList<>();
	private static final Map<Integer, BaseMod> blockModels = new HashMap<>();
	private static final Map<Integer, Boolean> blockSpecialInv = new HashMap<>();
	private static final File cfgdir = new File(Minecraft.getRunDirectory(), "/config/");
	private static final File cfgfile = new File(cfgdir, "ModLoader.cfg");
	public static Level cfgLoggingLevel = Level.FINER;
	private static Map<String, Class<? extends Entity>> classMap = null;
	private static long clock = 0L;
	public static final boolean DEBUG = false;
	// private static Field field_animList = null;
	// private static Field field_armorList = null;
	// private static Field field_blockList = null;
	// private static Field field_modifiers = null;
	// private static Field field_TileEntityRenderers = null;
	private static boolean hasInit = false;
	private static int highestEntityId = 3000;
	private static final Map<BaseMod, Boolean> inGameHooks = new HashMap<>();
	private static final Map<BaseMod, Boolean> inGUIHooks = new HashMap<>();
	private static Minecraft instance = null;
	private static int itemSpriteIndex = 0;
	private static int itemSpritesLeft = 0;
	private static final Map<BaseMod, Map<KeyBinding, boolean[]>> keyList = new HashMap<>();
	private static final File logfile = new File(Minecraft.getRunDirectory(), "ModLoader.txt");
	private static final Logger logger = Logger.getLogger("ModLoader");
	private static FileHandler logHandler = null;
	private static final File modDir = new File(Minecraft.getRunDirectory(), "/mods/");
	private static final LinkedList<BaseMod> modList = new LinkedList<>();
	private static int nextBlockModelID = 1000;
	private static final Map<Integer, Map<String, Integer>> overrides = new HashMap<>();
	public static final Properties props = new Properties();
	private static Biome[] standardBiomes;
	private static int terrainSpriteIndex = 0;
	private static int terrainSpritesLeft = 0;
	private static String texPack = null;
	private static boolean texturesAdded = false;
	private static final boolean[] usedItemSprites = new boolean[256];
	private static final boolean[] usedTerrainSprites = new boolean[256];
	public static final String VERSION = "ModLoader Beta 1.7.3";

	public static void AddAchievementDesc(AchievementStat AchievementStat, String name, String description) {
		try {
			if(AchievementStat.name.contains(".")) {
				String[] e = AchievementStat.name.split("\\.");
				if(e.length == 2) {
					String key = e[1];
					AddLocalization("AchievementStat." + key, name);
					AddLocalization("AchievementStat." + key + ".desc", description);
					setPrivateValue(Stat.class, AchievementStat, 1, LanguageManager.getInstance().translate("AchievementStat." + key));
					setPrivateValue(AchievementStat.class, AchievementStat, 3, LanguageManager.getInstance().translate("AchievementStat." + key + ".desc"));
				} else {
					setPrivateValue(Stat.class, AchievementStat, 1, name);
					setPrivateValue(AchievementStat.class, AchievementStat, 3, description);
				}
			} else {
				setPrivateValue(Stat.class, AchievementStat, 1, name);
				setPrivateValue(AchievementStat.class, AchievementStat, 3, description);
			}
		} catch (IllegalArgumentException var5) {
			logger.throwing("ModLoader", "AddAchievementStatDesc", var5);
			ThrowException(var5);
		} catch (SecurityException var6) {
			logger.throwing("ModLoader", "AddAchievementStatDesc", var6);
			ThrowException(var6);
		} catch (NoSuchFieldException var7) {
			logger.throwing("ModLoader", "AddAchievementStatDesc", var7);
			ThrowException(var7);
		}

	}

	public static int AddAllFuel(int id) {
		logger.finest("Finding fuel for " + id);
		int result = 0;

		for(Iterator<BaseMod> iter = modList.iterator(); iter.hasNext() && result == 0; result = iter.next().AddFuel(id)) {
		}

		if(result != 0) {
			logger.finest("Returned " + result);
		}

		return result;
	}

	public static void AddAllRenderers(Map<Class<? extends Entity>, EntityRenderer> o) {
		if(!hasInit) {
			init();
			logger.fine("Initialized");
		}

		for(BaseMod mod : modList) {
			mod.AddRenderer(o);
		}

	}

	public static void addAnimation(TextureAtlas anim) {
		logger.finest("Adding animation " + anim.toString());

		for(TextureAtlas oldAnim : animList) {
			if(oldAnim.type == anim.type && oldAnim.sprite == anim.sprite) {
				animList.remove(anim);
				break;
			}
		}

		animList.add(anim);
	}

	public static int AddArmor(String armor) {
		String[] e = PlayerEntityRenderer.ARMOR_VARIANTS;
		List<String> existingArmorList = Arrays.asList(e);
		ArrayList<String> combinedList = new ArrayList<>();
		combinedList.addAll(existingArmorList);
		if(!combinedList.contains(armor)) {
			combinedList.add(armor);
		}

		int index = combinedList.indexOf(armor);
		PlayerEntityRenderer.ARMOR_VARIANTS = combinedList.toArray(new String[0]);
		return index;
	}

	public static void AddLocalization(String key, String value) {
		Properties props = null;

		try {
			props = (Properties)getPrivateValue(LanguageManager.class, LanguageManager.getInstance(), 1);
		} catch (SecurityException var4) {
			logger.throwing("ModLoader", "AddLocalization", var4);
			ThrowException(var4);
		} catch (NoSuchFieldException var5) {
			logger.throwing("ModLoader", "AddLocalization", var5);
			ThrowException(var5);
		}

		if(props != null) {
			props.put(key, value);
		}

	}

	private static void addMod(ClassLoader loader, String filename) {
		try {
			String e = filename.split("\\.")[0];
			if(e.contains("$")) {
				return;
			}

			if(props.containsKey(e) && (props.getProperty(e).equalsIgnoreCase("no") || props.getProperty(e).equalsIgnoreCase("off"))) {
				return;
			}

			Package pack = ModLoader.class.getPackage();
			if(pack != null && !pack.getName().isEmpty()) {
				e = pack.getName() + "." + e;
			}

			Class instclass = loader.loadClass(e);
			if(!BaseMod.class.isAssignableFrom(instclass)) {
				return;
			}

			setupProperties(instclass);
			BaseMod mod = (BaseMod)instclass.newInstance();
			if(mod != null) {
				modList.add(mod);
				logger.fine("Mod Loaded: \"" + mod.toString() + "\" from " + filename);
				System.out.println("Mod Loaded: " + mod.toString());
			}
		} catch (Throwable var6) {
			logger.fine("Failed to load mod from \"" + filename + "\"");
			System.out.println("Failed to load mod from \"" + filename + "\"");
			logger.throwing("ModLoader", "addMod", var6);
			ThrowException(var6);
		}

	}

	public static void AddName(Object instance, String name) {
		String tag = null;
		Exception e3;
		if(instance instanceof Item) {
			Item e = (Item)instance;
			if(e.getTranslationKey() != null) {
				tag = e.getTranslationKey() + ".name";
			}
		} else if(instance instanceof Block) {
			Block e1 = (Block)instance;
			if(e1.getTranslationKey() != null) {
				tag = e1.getTranslationKey() + ".name";
			}
		} else if(instance instanceof ItemStack) {
			ItemStack e2 = (ItemStack)instance;
			if(e2.getTranslationKey() != null) {
				tag = e2.getTranslationKey() + ".name";
			}
		} else {
			e3 = new Exception(instance.getClass().getName() + " cannot have name attached to it!");
			logger.throwing("ModLoader", "AddName", e3);
			ThrowException(e3);
		}

		if(tag != null) {
			AddLocalization(tag, name);
		} else {
			e3 = new Exception(instance + " is missing name tag!");
			logger.throwing("ModLoader", "AddName", e3);
			ThrowException(e3);
		}

	}

	public static int addOverride(String fileToOverride, String fileToAdd) {
		try {
			int e = getUniqueSpriteIndex(fileToOverride);
			addOverride(fileToOverride, fileToAdd, e);
			return e;
		} catch (Throwable var3) {
			logger.throwing("ModLoader", "addOverride", var3);
			ThrowException(var3);
			throw new RuntimeException(var3);
		}
	}

	public static void addOverride(String path, String overlayPath, int index) {
		boolean dst = true;
		boolean left = false;
		byte dst1;
		int left1;
		if(path.equals("/terrain.png")) {
			dst1 = 0;
			left1 = terrainSpritesLeft;
		} else {
			if(!path.equals("/gui/items.png")) {
				return;
			}

			dst1 = 1;
			left1 = itemSpritesLeft;
		}

		System.out.println("Overriding " + path + " with " + overlayPath + " @ " + index + ". " + left1 + " left.");
		logger.finer("addOverride(" + path + "," + overlayPath + "," + index + "). " + left1 + " left.");
		Map<String, Integer> overlays = overrides.get(Integer.valueOf(dst1));
		if(overlays == null) {
			overlays = new HashMap();
			overrides.put(Integer.valueOf(dst1), overlays);
		}

		overlays.put(overlayPath, Integer.valueOf(index));
	}

	public static void AddRecipe(ItemStack output, Object... params) {
		CraftingManager.getInstance().registerShaped(output, params);
	}

	public static void AddShapelessRecipe(ItemStack output, Object... params) {
		CraftingManager.getInstance().registerShapeless(output, params);
	}

	public static void AddSmelting(int input, ItemStack output) {
		SmeltingManager.getInstance().register(input, output);
	}

	public static void AddSpawn(Class<? extends LivingEntity> entityClass, int weightedProb, MobCategory spawnList) {
		AddSpawn(entityClass, weightedProb, spawnList, (Biome[])null);
	}

	public static void AddSpawn(Class<? extends LivingEntity> entityClass, int weightedProb, MobCategory spawnList, Biome... biomes) {
		if(entityClass == null) {
			throw new IllegalArgumentException("entityClass cannot be null");
		} else if(spawnList == null) {
			throw new IllegalArgumentException("spawnList cannot be null");
		} else {
			if(biomes == null) {
				biomes = standardBiomes;
			}

			for(int i = 0; i < biomes.length; ++i) {
				List list = biomes[i].getSpawnEntries(spawnList);
				if(list != null) {
					boolean exists = false;
					Iterator var8 = list.iterator();

					while(var8.hasNext()) {
						Biome__SpawnEntry entry = (Biome__SpawnEntry)var8.next();
						if(entry.type == entityClass) {
							entry.weight = weightedProb;
							exists = true;
							break;
						}
					}

					if(!exists) {
						list.add(new Biome__SpawnEntry(entityClass, weightedProb));
					}
				}
			}

		}
	}

	public static void AddSpawn(String entityName, int weightedProb, MobCategory spawnList) {
		AddSpawn(entityName, weightedProb, spawnList, (Biome[])null);
	}

	public static void AddSpawn(String entityName, int weightedProb, MobCategory spawnList, Biome... biomes) {
		Class entityClass = classMap.get(entityName);
		if(entityClass != null && LivingEntity.class.isAssignableFrom(entityClass)) {
			AddSpawn(entityClass, weightedProb, spawnList, biomes);
		}

	}

	public static boolean DispenseEntity(World world, double x, double y, double z, int xVel, int zVel, ItemStack item) {
		boolean result = false;

		for(Iterator iter = modList.iterator(); iter.hasNext() && !result; result = ((BaseMod)iter.next()).DispenseEntity(world, x, y, z, xVel, zVel, item)) {
		}

		return result;
	}

	public static List<BaseMod> getLoadedMods() {
		return Collections.unmodifiableList(modList);
	}

	public static Logger getLogger() {
		return logger;
	}

	public static Minecraft getMinecraftInstance() {
		instance = Minecraft.INSTANCE;
		return instance;
	}

	public static <T, E> T getPrivateValue(Class<? super E> instanceclass, E instance, int fieldindex) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
		return getPrivateValue(instanceclass, instance, instanceclass.getDeclaredFields()[fieldindex]);
	}

	public static <T, E> T getPrivateValue(Class<? super E> instanceclass, E instance, String field) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
		return getPrivateValue(instanceclass, instance, instanceclass.getDeclaredField(field));
	}

	private static <T, E> T getPrivateValue(Class<? super E> instanceclass, E instance, Field field) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
		UnsafeUtils.ensureClassInitialized(instanceclass);
		if(Modifier.isStatic(field.getModifiers())) {
			if(field.getType() == boolean.class) {
				return (T)Boolean.valueOf(UnsafeUtils.getStaticBoolean(field));
			}
			if(field.getType() == int.class) {
				return (T)Integer.valueOf(UnsafeUtils.getStaticInt(field));
			}
			if(field.getType() == long.class) {
				return (T)Long.valueOf(UnsafeUtils.getStaticLong(field));
			}
			if(field.getType() == char.class) {
				return (T)Character.valueOf(UnsafeUtils.getStaticChar(field));
			}
			if(field.getType() == short.class) {
				return (T)Short.valueOf(UnsafeUtils.getStaticShort(field));
			}
			if(field.getType() == byte.class) {
				return (T)Byte.valueOf(UnsafeUtils.getStaticByte(field));
			}
			return (T)UnsafeUtils.getStaticObject(field);
		}
		if(field.getType() == boolean.class) {
			return (T)Boolean.valueOf(UnsafeUtils.getBoolean(instance, field));
		}
		if(field.getType() == int.class) {
			return (T)Integer.valueOf(UnsafeUtils.getInt(instance, field));
		}
		if(field.getType() == long.class) {
			return (T)Long.valueOf(UnsafeUtils.getLong(instance, field));
		}
		if(field.getType() == char.class) {
			return (T)Character.valueOf(UnsafeUtils.getChar(instance, field));
		}
		if(field.getType() == short.class) {
			return (T)Short.valueOf(UnsafeUtils.getShort(instance, field));
		}
		if(field.getType() == byte.class) {
			return (T)Byte.valueOf(UnsafeUtils.getByte(instance, field));
		}
		return (T)UnsafeUtils.getObject(instance, field);
	}

	public static int getUniqueBlockModelID(BaseMod mod, boolean full3DItem) {
		int id = nextBlockModelID++;
		blockModels.put(Integer.valueOf(id), mod);
		blockSpecialInv.put(Integer.valueOf(id), Boolean.valueOf(full3DItem));
		return id;
	}

	public static int getUniqueEntityId() {
		return highestEntityId++;
	}

	private static int getUniqueItemSpriteIndex() {
		while(itemSpriteIndex < usedItemSprites.length) {
			if(!usedItemSprites[itemSpriteIndex]) {
				usedItemSprites[itemSpriteIndex] = true;
				--itemSpritesLeft;
				return itemSpriteIndex++;
			}

			++itemSpriteIndex;
		}

		Exception e = new Exception("No more empty item sprite indices left!");
		logger.throwing("ModLoader", "getUniqueItemSpriteIndex", e);
		ThrowException(e);
		return 0;
	}

	public static int getUniqueSpriteIndex(String path) {
		if(path.equals("/gui/items.png")) {
			return getUniqueItemSpriteIndex();
		} else if(path.equals("/terrain.png")) {
			return getUniqueTerrainSpriteIndex();
		} else {
			Exception e = new Exception("No registry for this texture: " + path);
			logger.throwing("ModLoader", "getUniqueItemSpriteIndex", e);
			ThrowException(e);
			return 0;
		}
	}

	private static int getUniqueTerrainSpriteIndex() {
		while(terrainSpriteIndex < usedTerrainSprites.length) {
			if(!usedTerrainSprites[terrainSpriteIndex]) {
				usedTerrainSprites[terrainSpriteIndex] = true;
				--terrainSpritesLeft;
				return terrainSpriteIndex++;
			}

			++terrainSpriteIndex;
		}

		Exception e = new Exception("No more empty terrain sprite indices left!");
		logger.throwing("ModLoader", "getUniqueItemSpriteIndex", e);
		ThrowException(e);
		return 0;
	}

	private static void init() {
		hasInit = true;
		String usedItemSpritesString = "1111111111111111111111111111111111111101111111011111111111111001111111111111111111111111111011111111100110000011111110000000001111111001100000110000000100000011000000010000001100000000000000110000000000000000000000000000000000000000000000001100000000000000";
		String usedTerrainSpritesString = "1111111111111111111111111111110111111111111111111111110111111111111111111111000111111011111111111111001111111110111111111111100011111111000010001111011110000000111111000000000011111100000000001111000000000111111000000000001101000000000001111111111111000011";

		for(int e = 0; e < 256; ++e) {
			usedItemSprites[e] = usedItemSpritesString.charAt(e) == 49;
			if(!usedItemSprites[e]) {
				++itemSpritesLeft;
			}

			usedTerrainSprites[e] = usedTerrainSpritesString.charAt(e) == 49;
			if(!usedTerrainSprites[e]) {
				++terrainSpritesLeft;
			}
		}

		try {
			instance = Minecraft.INSTANCE;
			classMap = Entities.KEY_TO_TYPE;
			Field[] var15 = Biome.class.getDeclaredFields();
			LinkedList mod = new LinkedList();

			for(int e1 = 0; e1 < var15.length; ++e1) {
				Class fieldType = var15[e1].getType();
				if((var15[e1].getModifiers() & 8) != 0 && fieldType.isAssignableFrom(Biome.class)) {
					Biome biome = (Biome)var15[e1].get((Object)null);
					if(!(biome instanceof HellBiome) && !(biome instanceof TheEndBiome)) {
						mod.add(biome);
					}
				}
			}

			standardBiomes = (Biome[])mod.toArray(new Biome[0]);
		} catch (SecurityException var10) {
			logger.throwing("ModLoader", "init", var10);
			ThrowException(var10);
			throw new RuntimeException(var10);
		} catch (IllegalArgumentException var13) {
			logger.throwing("ModLoader", "init", var13);
			ThrowException(var13);
			throw new RuntimeException(var13);
		} catch (IllegalAccessException var14) {
			logger.throwing("ModLoader", "init", var14);
			ThrowException(var14);
			throw new RuntimeException(var14);
		}

		try {
			loadConfig();
			if(props.containsKey("loggingLevel")) {
				cfgLoggingLevel = Level.parse(props.getProperty("loggingLevel"));
			}

			if(props.containsKey("grassFix")) {
				// BlockRenderer.cfgGrassFix = Boolean.parseBoolean(props.getProperty("grassFix"));
			}

			logger.setLevel(cfgLoggingLevel);
			if((logfile.exists() || logfile.createNewFile()) && logfile.canWrite() && logHandler == null) {
				logHandler = new FileHandler(logfile.getPath());
				logHandler.setFormatter(new SimpleFormatter());
				logger.addHandler(logHandler);
			}

			logger.fine("ModLoader Beta 1.7.3 Initializing...");
			System.out.println("ModLoader Beta 1.7.3 Initializing...");
			File var16 = new File(ModLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			modDir.mkdirs();
			readFromModFolder(modDir);
			readFromClassPath(var16);
			System.out.println("Done.");
			props.setProperty("loggingLevel", cfgLoggingLevel.getName());
			// props.setProperty("grassFix", Boolean.toString(BlockRenderer.cfgGrassFix));
			Iterator var18 = modList.iterator();

			while(var18.hasNext()) {
				BaseMod var17 = (BaseMod)var18.next();
				var17.ModsLoaded();
				if(!props.containsKey(var17.getClass().getName())) {
					props.setProperty(var17.getClass().getName(), "on");
				}
			}

			instance.options.keyBindings = RegisterAllKeys(instance.options.keyBindings);
			instance.options.load();
			initStats();
			saveConfig();
		} catch (Throwable var9) {
			logger.throwing("ModLoader", "init", var9);
			ThrowException("ModLoader has failed to initialize.", var9);
			if(logHandler != null) {
				logHandler.close();
			}

			throw new RuntimeException(var9);
		}
	}

	private static void initStats() {
		int idHashSet;
		String id;
		for(idHashSet = 0; idHashSet < Block.BY_ID.length; ++idHashSet) {
			if(!Stats.BY_KEY.containsKey(Integer.valueOf(16777216 + idHashSet)) && Block.BY_ID[idHashSet] != null && Block.BY_ID[idHashSet].hasStats()) {
				id = LanguageManager.getInstance().translate("stat.mineBlock", new Object[]{Block.BY_ID[idHashSet].getName()});
				Stats.BLOCKS_MINED[idHashSet] = (new ItemStat(16777216 + idHashSet, id, idHashSet)).register();
				Stats.MINED.add(Stats.BLOCKS_MINED[idHashSet]);
			}
		}

		for(idHashSet = 0; idHashSet < Item.BY_ID.length; ++idHashSet) {
			if(!Stats.BY_KEY.containsKey(Integer.valueOf(16908288 + idHashSet)) && Item.BY_ID[idHashSet] != null) {
				id = LanguageManager.getInstance().translate("stat.useItem", new Object[]{Item.BY_ID[idHashSet].getDisplayName()});
				Stats.ITEMS_USED[idHashSet] = (new ItemStat(16908288 + idHashSet, id, idHashSet)).register();
				if(idHashSet >= Block.BY_ID.length) {
					Stats.USED.add(Stats.ITEMS_USED[idHashSet]);
				}
			}

			if(!Stats.BY_KEY.containsKey(Integer.valueOf(16973824 + idHashSet)) && Item.BY_ID[idHashSet] != null && Item.BY_ID[idHashSet].isDamageable()) {
				id = LanguageManager.getInstance().translate("stat.breakItem", new Object[]{Item.BY_ID[idHashSet].getDisplayName()});
				Stats.ITEMS_BROKEN[idHashSet] = (new ItemStat(16973824 + idHashSet, id, idHashSet)).register();
			}
		}

		HashSet<Integer> var4 = new HashSet<Integer>();

		for(Recipe recipe : (List<Recipe>)CraftingManager.getInstance().getRecipes()) {
			var4.add(Integer.valueOf(recipe.getResult().itemId));
		}

		Iterator<ItemStack> var2 = SmeltingManager.getInstance().getRecipes().values().iterator();

		while(var2.hasNext()) {
			ItemStack var5 = (ItemStack)var2.next();
			var4.add(Integer.valueOf(var5.itemId));
		}

		for(int var6 : var4) {
			if(!Stats.BY_KEY.containsKey(Integer.valueOf(16842752 + var6)) && Item.BY_ID[var6] != null) {
				String str = LanguageManager.getInstance().translate("stat.craftItem", new Object[]{Item.BY_ID[var6].getDisplayName()});
				Stats.ITEMS_CRAFTED[var6] = (new ItemStat(16842752 + var6, str, var6)).register();
			}
		}

	}

	public static boolean isGUIOpen(Class<? extends Screen> gui) {
		Minecraft game = getMinecraftInstance();
		return gui == null ? game.screen == null : (game.screen == null && gui != null ? false : gui.isInstance(game.screen));
	}

	public static boolean isModLoaded(String modname) {
		Class chk = null;

		try {
			chk = Class.forName(modname);
		} catch (ClassNotFoundException var4) {
			return false;
		}

		if(chk != null) {
			Iterator var3 = modList.iterator();

			while(var3.hasNext()) {
				BaseMod mod = (BaseMod)var3.next();
				if(chk.isInstance(mod)) {
					return true;
				}
			}
		}

		return false;
	}

	public static void loadConfig() throws IOException {
		cfgdir.mkdir();
		if(cfgfile.exists() || cfgfile.createNewFile()) {
			if(cfgfile.canRead()) {
				FileInputStream in = new FileInputStream(cfgfile);
				props.load(in);
				in.close();
			}

		}
	}

	public static BufferedImage loadImage(TextureManager texCache, String path) throws Exception {
		TexturePacks pack = (TexturePacks)getPrivateValue(TextureManager.class, texCache, 11);
		InputStream input = pack.selected.getResource(path);
		if(input == null) {
			throw new Exception("Image not found: " + path);
		} else {
			BufferedImage image = ImageIO.read(input);
			if(image == null) {
				throw new Exception("Image corrupted: " + path);
			} else {
				return image;
			}
		}
	}

	public static void OnItemPickup(PlayerEntity player, ItemStack item) {
		for(BaseMod mod : modList) {
			mod.OnItemPickup(player, item);
		}

	}

	public static void OnTick(Minecraft game) {
		if(!hasInit) {
			init();
			logger.fine("Initialized");
		}

		if(texPack == null || game.options.skin != texPack) {
			texturesAdded = false;
			texPack = game.options.skin;
		}

		if(!texturesAdded && game.textureManager != null) {
			RegisterAllTextureOverrides(game.textureManager);
			texturesAdded = true;
		}

		long newclock = 0L;
		Iterator modSet;
		Entry modSet1;
		if(game.world != null) {
			newclock = game.world.getTime();
			modSet = inGameHooks.entrySet().iterator();

			label93:
			while(true) {
				do {
					if(!modSet.hasNext()) {
						break label93;
					}

					modSet1 = (Entry)modSet.next();
				} while(clock == newclock && ((Boolean)modSet1.getValue()).booleanValue());

				if(!((BaseMod)modSet1.getKey()).OnTickInGame(game)) {
					modSet.remove();
				}
			}
		}

		if(game.screen != null) {
			modSet = inGUIHooks.entrySet().iterator();

			label80:
			while(true) {
				do {
					if(!modSet.hasNext()) {
						break label80;
					}

					modSet1 = (Entry)modSet.next();
				} while(clock == newclock && ((Boolean)modSet1.getValue()).booleanValue() & game.world != null);

				if(!((BaseMod)modSet1.getKey()).OnTickInGUI(game, game.screen)) {
					modSet.remove();
				}
			}
		}

		if(clock != newclock) {
			for(Entry<BaseMod, Map<KeyBinding, boolean[]>> modSet2 : keyList.entrySet()) {
				
				for(Entry<KeyBinding, boolean[]> keySet : modSet2.getValue().entrySet()) {
					
					boolean state = Keyboard.isKeyDown(keySet.getKey().keyCode);
					boolean[] keyInfo = keySet.getValue();
					boolean oldState = keyInfo[1];
					keyInfo[1] = state;
					if(!state) {
						continue;
					}
					if(oldState && !keyInfo[0]) {
						continue;
					}
					modSet2.getKey().KeyboardEvent(keySet.getKey());
				}
				// Iterator var6 = modSet2.getValue().entrySet().iterator();

				// while(true) {
				// 	Entry keySet;
				// 	boolean state;
				// 	boolean[] keyInfo;
				// 	boolean oldState;
				// 	do {
				// 		do {
				// 			if(!var6.hasNext()) {
				// 				continue label66;
				// 			}

				// 			keySet = (Entry)var6.next();
				// 			state = Keyboard.isKeyDown(((KeyBinding)keySet.getKey()).keyCode);
				// 			keyInfo = (boolean[])keySet.getValue();
				// 			oldState = keyInfo[1];
				// 			keyInfo[1] = state;
				// 		} while(!state);
				// 	} while(oldState && !keyInfo[0]);

				// 	modSet2.getKey().KeyboardEvent((KeyBinding)keySet.getKey());
				// }
			}
		}

		clock = newclock;
	}

	public static void OpenGUI(PlayerEntity player, Screen gui) {
		if(!hasInit) {
			init();
			logger.fine("Initialized");
		}

		Minecraft game = getMinecraftInstance();
		if(game.player == player) {
			if(gui != null) {
				game.openScreen(gui);
			}

		}
	}

	public static void PopulateChunk(ChunkSource generator, int chunkX, int chunkZ, World world) {
		if(!hasInit) {
			init();
			logger.fine("Initialized");
		}

		Random rnd = new Random(world.getSeed());
		long xSeed = rnd.nextLong() / 2L * 2L + 1L;
		long zSeed = rnd.nextLong() / 2L * 2L + 1L;
		rnd.setSeed((long)chunkX * xSeed + (long)chunkZ * zSeed ^ world.getSeed());

		for(BaseMod mod : modList) {
			if(generator.getDebugInfo().equals("RandomLevelSource")) {
				mod.GenerateSurface(world, rnd, chunkX << 4, chunkZ << 4);
			} else if(generator.getDebugInfo().equals("HellRandomLevelSource")) {
				mod.GenerateNether(world, rnd, chunkX << 4, chunkZ << 4);
			}
		}

	}

	private static void readFromClassPath(File source) throws FileNotFoundException, IOException {
		logger.finer("Adding mods from " + source.getCanonicalPath());
		ClassLoader loader = ModLoader.class.getClassLoader();
		String name;
		if(source.isFile() && (source.getName().endsWith(".jar") || source.getName().endsWith(".zip"))) {
			logger.finer("Zip found.");
			FileInputStream var6 = new FileInputStream(source);
			ZipInputStream var8 = new ZipInputStream(var6);
			ZipEntry var9 = null;

			while(true) {
				var9 = var8.getNextEntry();
				if(var9 == null) {
					var6.close();
					break;
				}

				name = var9.getName();
				if(!var9.isDirectory() && name.startsWith("mod_") && name.endsWith(".class")) {
					addMod(loader, name);
				}
			}
		} else if(source.isDirectory()) {
			Package pkg = ModLoader.class.getPackage();
			if(pkg != null) {
				String files = pkg.getName().replace('.', File.separatorChar);
				source = new File(source, files);
			}

			logger.finer("Directory found.");
			File[] var7 = source.listFiles();
			if(var7 != null) {
				for(int i = 0; i < var7.length; ++i) {
					name = var7[i].getName();
					if(var7[i].isFile() && name.startsWith("mod_") && name.endsWith(".class")) {
						addMod(loader, name);
					}
				}
			}
		}

	}

	private static void readFromModFolder(File folder) throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
		ClassLoader loader = Minecraft.class.getClassLoader();
		// java.lang.reflect.InaccessibleObjectException on Java 21
		// Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
		// addURL.setAccessible(true);
		if(!folder.isDirectory()) {
			throw new IllegalArgumentException("folder must be a Directory.");
		} else {
			List<File> sourcefiles = Arrays.asList(folder.listFiles());
			sourcefiles.sort(Comparator.naturalOrder());
			if(loader instanceof LaunchClassLoader) {
				for(File source : sourcefiles) {
					if(source.isDirectory() || source.isFile() && (source.getName().endsWith(".jar") || source.getName().endsWith(".zip"))) {
						// addURL.invoke(loader, new Object[]{source.toURI().toURL()});
						((LaunchClassLoader)loader).addURL(source.toURI().toURL());
					}
				}
			}

			for(File source : sourcefiles) {
				if(source.isDirectory() || source.isFile() && (source.getName().endsWith(".jar") || source.getName().endsWith(".zip"))) {
					logger.finer("Adding mods from " + source.getCanonicalPath());
					String name;
					if(!source.isFile()) {
						if(source.isDirectory()) {
							Package var10 = ModLoader.class.getPackage();
							if(var10 != null) {
								String var11 = var10.getName().replace('.', File.separatorChar);
								source = new File(source, var11);
							}

							logger.finer("Directory found.");
							File[] var12 = source.listFiles();
							if(var12 != null) {
								List<File> sourcefiles2 = Arrays.asList(var12);
								sourcefiles2.sort(Comparator.naturalOrder());
								for(File file : sourcefiles2) {
									name = file.getName();
									if(file.isFile() && name.startsWith("mod_") && name.endsWith(".class")) {
										addMod(loader, name);
									}
								}
							}
						}
					} else {
						logger.finer("Zip found.");
						FileInputStream pkg = new FileInputStream(source);
						ZipInputStream dirfiles = new ZipInputStream(pkg);
						ZipEntry j = null;

						while(true) {
							j = dirfiles.getNextEntry();
							if(j == null) {
								dirfiles.close();
								pkg.close();
								break;
							}

							name = j.getName();
							if(!j.isDirectory() && name.startsWith("mod_") && name.endsWith(".class")) {
								addMod(loader, name);
							}
						}
					}
				}
			}

		}
	}

	public static KeyBinding[] RegisterAllKeys(KeyBinding[] w) {
		LinkedList<KeyBinding> combinedList = new LinkedList<>();
		combinedList.addAll(Arrays.asList(w));

		for(Map<KeyBinding, boolean[]> keyMap : keyList.values()) {
			combinedList.addAll(keyMap.keySet());
		}

		return combinedList.toArray(new KeyBinding[0]);
	}

    public static void RegisterAllTextureOverrides(TextureManager texCache) {
        animList.clear();
        Minecraft game = getMinecraftInstance();

		for(BaseMod mod : modList) {
            mod.RegisterAnimation(game);
        }

		for(TextureAtlas anim : animList) {
            texCache.addSprite(anim);
        }

        for(Map.Entry<Integer, Map<String, Integer>> overlay : overrides.entrySet()) {
			for(Map.Entry<String, Integer> overlayEntry : overlay.getValue().entrySet()) {
                String overlayPath = overlayEntry.getKey();
                int index = overlayEntry.getValue();
                int dst = overlay.getKey();

                try {
                    BufferedImage im = loadImage(texCache, overlayPath);
                    TextureAtlas anim = new ModTextureStatic(index, dst, im);
                    texCache.addSprite(anim);
                } catch (Exception var11) {
                    Exception e = var11;
                    logger.throwing("ModLoader", "RegisterAllTextureOverrides", e);
                    ThrowException(e);
                    throw new RuntimeException(e);
                }
            }
        }

    }

	public static void RegisterBlock(Block block) {
		RegisterBlock(block, null);
	}

	@SuppressWarnings("unchecked")
	public static void RegisterBlock(Block block, Class<? extends BlockItem> itemclass) {
		try {
			if(block == null) {
				throw new IllegalArgumentException("block parameter cannot be null.");
			}

			Session.f_1364632.add(block);
			int id = block.id;
			BlockItem item = null;
			if(itemclass != null) {
				item = (BlockItem)itemclass.getConstructor(new Class[]{Integer.TYPE}).newInstance(new Object[]{Integer.valueOf(id - 256)});
			} else {
				item = new BlockItem(id - 256);
			}

			if(Block.BY_ID[id] != null && Item.BY_ID[id] == null) {
				Item.BY_ID[id] = item;
			}
		} catch (IllegalArgumentException var5) {
			logger.throwing("ModLoader", "RegisterBlock", var5);
			ThrowException(var5);
		} catch (IllegalAccessException var6) {
			logger.throwing("ModLoader", "RegisterBlock", var6);
			ThrowException(var6);
		} catch (SecurityException var7) {
			logger.throwing("ModLoader", "RegisterBlock", var7);
			ThrowException(var7);
		} catch (InstantiationException var8) {
			logger.throwing("ModLoader", "RegisterBlock", var8);
			ThrowException(var8);
		} catch (InvocationTargetException var9) {
			logger.throwing("ModLoader", "RegisterBlock", var9);
			ThrowException(var9);
		} catch (NoSuchMethodException var10) {
			logger.throwing("ModLoader", "RegisterBlock", var10);
			ThrowException(var10);
		}

	}

	public static void RegisterEntityID(Class<? extends Entity> entityClass, String entityName, int id) {
		Entities.register(entityClass, entityName, id);
	}

	public static void RegisterKey(BaseMod mod, KeyBinding keyHandler, boolean allowRepeat) {
		Map<KeyBinding, boolean[]> keyMap = keyList.get(mod);
		if(keyMap == null) {
			keyMap = new HashMap<>();
		}

		keyMap.put(keyHandler, new boolean[]{allowRepeat, false});
		keyList.put(mod, keyMap);
	}

	public static void RegisterTileEntity(Class<? extends BlockEntity> tileEntityClass, String id) {
		RegisterTileEntity(tileEntityClass, id, null);
	}

	@SuppressWarnings("unchecked")
	public static void RegisterTileEntity(Class<? extends BlockEntity> tileEntityClass, String id, BlockEntityRenderer renderer) {

		BlockEntity.register(tileEntityClass, id);
		if(renderer != null) {
			BlockEntityRenderDispatcher.INSTANCE.renderers.put(tileEntityClass, renderer);
			renderer.init(BlockEntityRenderDispatcher.INSTANCE);
		}

	}

	public static void RemoveSpawn(Class<? extends LivingEntity> entityClass, MobCategory spawnList) {
		RemoveSpawn(entityClass, spawnList, (Biome[])null);
	}

	@SuppressWarnings("rawtypes")
	public static void RemoveSpawn(Class<? extends LivingEntity> entityClass, MobCategory spawnList, Biome... biomes) {
		if(entityClass == null) {
			throw new IllegalArgumentException("entityClass cannot be null");
		} else if(spawnList == null) {
			throw new IllegalArgumentException("spawnList cannot be null");
		} else {
			if(biomes == null) {
				biomes = standardBiomes;
			}

			for(int i = 0; i < biomes.length; ++i) {
				List list = biomes[i].getSpawnEntries(spawnList);
				if(list != null) {
					Iterator iter = list.iterator();

					while(iter.hasNext()) {
						Biome__SpawnEntry entry = (Biome__SpawnEntry)iter.next();
						if(entry.type == entityClass) {
							iter.remove();
						}
					}
				}
			}

		}
	}

	public static void RemoveSpawn(String entityName, MobCategory spawnList) {
		RemoveSpawn(entityName, spawnList, (Biome[])null);
	}

	public static void RemoveSpawn(String entityName, MobCategory spawnList, Biome... biomes) {
		Class<? extends Entity> entityClass = classMap.get(entityName);
		if(entityClass != null && LivingEntity.class.isAssignableFrom(entityClass)) {
			RemoveSpawn(entityClass.asSubclass(LivingEntity.class), spawnList, biomes);
		}

	}

	public static boolean RenderBlockIsItemFull3D(int modelID) {
		return !blockSpecialInv.containsKey(Integer.valueOf(modelID)) ? modelID == 16 : ((Boolean)blockSpecialInv.get(Integer.valueOf(modelID))).booleanValue();
	}

	public static void RenderInvBlock(BlockRenderer renderer, Block block, int metadata, int modelID) {
		BaseMod mod = (BaseMod)blockModels.get(Integer.valueOf(modelID));
		if(mod != null) {
			mod.RenderInvBlock(renderer, block, metadata, modelID);
		}
	}

	public static boolean RenderWorldBlock(BlockRenderer renderer, WorldView world, int x, int y, int z, Block block, int modelID) {
		BaseMod mod = (BaseMod)blockModels.get(Integer.valueOf(modelID));
		return mod == null ? false : mod.RenderWorldBlock(renderer, world, x, y, z, block, modelID);
	}

	public static void saveConfig() throws IOException {
		cfgdir.mkdir();
		if(cfgfile.exists() || cfgfile.createNewFile()) {
			if(cfgfile.canWrite()) {
				FileOutputStream out = new FileOutputStream(cfgfile);
				props.store(out, "ModLoader Config");
				out.close();
			}

		}
	}

	public static void SetInGameHook(BaseMod mod, boolean enable, boolean useClock) {
		if(enable) {
			inGameHooks.put(mod, Boolean.valueOf(useClock));
		} else {
			inGameHooks.remove(mod);
		}

	}

	public static void SetInGUIHook(BaseMod mod, boolean enable, boolean useClock) {
		if(enable) {
			inGUIHooks.put(mod, Boolean.valueOf(useClock));
		} else {
			inGUIHooks.remove(mod);
		}

	}

	public static <T, E> void setPrivateValue(Class<? super T> instanceclass, T instance, int fieldindex, E value) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
		Field e = instanceclass.getDeclaredFields()[fieldindex];
		setPrivateValue(instanceclass, instance, e, value);
	}

	public static <T, E> void setPrivateValue(Class<? super T> instanceclass, T instance, String field, E value) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
		Field e = instanceclass.getDeclaredField(field);
		setPrivateValue(instanceclass, instance, e, value);
	}


	private static <T, E> void setPrivateValue(Class<? super T> instanceclass, T instance, Field field, E value) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
		UnsafeUtils.ensureClassInitialized(instanceclass);
		if(Modifier.isStatic(field.getModifiers())) {
			if(field.getType() == boolean.class) {
				UnsafeUtils.setStaticBoolean(field, (Boolean)value);
				return;
			}
			if(field.getType() == int.class) {
				UnsafeUtils.setStaticInt(field, (Integer)value);
				return;
			}
			if(field.getType() == long.class) {
				UnsafeUtils.setStaticLong(field, (Long)value);
				return;
			}
			if(field.getType() == char.class) {
				UnsafeUtils.setStaticChar(field, (Character)value);
				return;
			}
			if(field.getType() == short.class) {
				UnsafeUtils.setStaticShort(field, (Short)value);
				return;
			}
			if(field.getType() == byte.class) {
				UnsafeUtils.setStaticByte(field, (Byte)value);
				return;
			}
			UnsafeUtils.setStaticObject(field, value);
			return;
		}
		if(field.getType() == boolean.class) {
			UnsafeUtils.setBoolean(instance, field, (Boolean)value);
			return;
		}
		if(field.getType() == int.class) {
			UnsafeUtils.setInt(instance, field, (Integer)value);
			return;
		}
		if(field.getType() == long.class) {
			UnsafeUtils.setLong(instance, field, (Long)value);
			return;
		}
		if(field.getType() == char.class) {
			UnsafeUtils.setChar(instance, field, (Character)value);
			return;
		}
		if(field.getType() == short.class) {
			UnsafeUtils.setShort(instance, field, (Short)value);
			return;
		}
		if(field.getType() == byte.class) {
			UnsafeUtils.setByte(instance, field, (Byte)value);
			return;
		}
		UnsafeUtils.setObject(instance, field, value);
	}

	private static void setupProperties(Class<? extends BaseMod> mod) throws IllegalArgumentException, IllegalAccessException, IOException, SecurityException, NoSuchFieldException {
		Properties modprops = new Properties();
		File modcfgfile = new File(cfgdir, mod.getName() + ".cfg");
		if(modcfgfile.exists() && modcfgfile.canRead()) {
			modprops.load(new FileInputStream(modcfgfile));
		}

		StringBuilder helptext = new StringBuilder();
		Field[] var7 = mod.getFields();
		int var6 = var7.length;

		for(int var5 = 0; var5 < var6; ++var5) {
			Field field = var7[var5];
			if((field.getModifiers() & 8) != 0 && field.isAnnotationPresent(MLProp.class)) {
				Class<?> type = field.getType();
				MLProp annotation = (MLProp)field.getAnnotation(MLProp.class);
				String key = annotation.name().length() == 0 ? field.getName() : annotation.name();
				Object currentvalue = field.get((Object)null);
				StringBuilder range = new StringBuilder();
				if(annotation.min() != -Double.POSITIVE_INFINITY) {
					range.append(String.format(",>=%.1f", new Object[]{Double.valueOf(annotation.min())}));
				}

				if(annotation.max() != Double.POSITIVE_INFINITY) {
					range.append(String.format(",<=%.1f", new Object[]{Double.valueOf(annotation.max())}));
				}

				StringBuilder info = new StringBuilder();
				if(annotation.info().length() > 0) {
					info.append(" -- ");
					info.append(annotation.info());
				}

				helptext.append(String.format("%s (%s:%s%s)%s\n", new Object[]{key, type.getName(), currentvalue, range, info}));
				if(modprops.containsKey(key)) {
					String strvalue = modprops.getProperty(key);
					Object value = null;
					if(type.isAssignableFrom(String.class)) {
						value = strvalue;
					} else if(type.isAssignableFrom(Integer.TYPE)) {
						value = Integer.valueOf(Integer.parseInt(strvalue));
					} else if(type.isAssignableFrom(Short.TYPE)) {
						value = Short.valueOf(Short.parseShort(strvalue));
					} else if(type.isAssignableFrom(Byte.TYPE)) {
						value = Byte.valueOf(Byte.parseByte(strvalue));
					} else if(type.isAssignableFrom(Boolean.TYPE)) {
						value = Boolean.valueOf(Boolean.parseBoolean(strvalue));
					} else if(type.isAssignableFrom(Float.TYPE)) {
						value = Float.valueOf(Float.parseFloat(strvalue));
					} else if(type.isAssignableFrom(Double.TYPE)) {
						value = Double.valueOf(Double.parseDouble(strvalue));
					}

					if(value != null) {
						if(value instanceof Number) {
							double num = ((Number)value).doubleValue();
							if(annotation.min() != -Double.POSITIVE_INFINITY && num < annotation.min() || annotation.max() != Double.POSITIVE_INFINITY && num > annotation.max()) {
								continue;
							}
						}

						logger.finer(key + " set to " + value);
						if(!value.equals(currentvalue)) {
							field.set((Object)null, value);
						}
					}
				} else {
					logger.finer(key + " not in config, using default: " + currentvalue);
					modprops.setProperty(key, currentvalue.toString());
				}
			}
		}

		if(!modprops.isEmpty() && (modcfgfile.exists() || modcfgfile.createNewFile()) && modcfgfile.canWrite()) {
			modprops.store(new FileOutputStream(modcfgfile), helptext.toString());
		}

	}

	public static void TakenFromCrafting(PlayerEntity player, ItemStack item) {
		for(BaseMod mod : modList) {
			mod.TakenFromCrafting(player, item);
		}

	}

	public static void TakenFromFurnace(PlayerEntity player, ItemStack item) {
		for(BaseMod mod : modList) {
			mod.TakenFromFurnace(player, item);
		}

	}

	public static void ThrowException(String message, Throwable e) {
		Minecraft game = getMinecraftInstance();
		if(game != null) {
			game.printCrashReport(new CrashSummary(message, e));
		} else {
			throw new RuntimeException(e);
		}
	}

	private static void ThrowException(Throwable e) {
		ThrowException("Exception occured in ModLoader", e);
	}
}
