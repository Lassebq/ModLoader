package io.github.lassebq.modloader;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.render.texture.TextureAtlas;

public class ModTextureAnimation extends TextureAtlas {
	private final int tickRate;
	private final byte[][] images;
	private int index;
	private int ticks;

	public ModTextureAnimation(int slot, int dst, BufferedImage source, int rate) {
		this(slot, 1, dst, source, rate);
	}

	public ModTextureAnimation(int slot, int size, int dst, BufferedImage source, int rate) {
		super(slot);
		this.index = 0;
		this.ticks = 0;
		this.resolution = size;
		this.type = dst;
		this.tickRate = rate;
		this.ticks = rate;
		this.bind(ModLoader.getMinecraftInstance().textureManager);
		int targetWidth = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH) / 16;
		int targetHeight = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT) / 16;
		int width = source.getWidth();
		int height = source.getHeight();
		int images = (int)Math.floor((double)(height / width));
		if(images <= 0) {
			throw new IllegalArgumentException("source has no complete images");
		} else {
			this.images = new byte[images][];
			if(width != targetWidth) {
				BufferedImage i = new BufferedImage(targetWidth, targetHeight * images, 6);
				Graphics2D temp = i.createGraphics();
				temp.drawImage(source, 0, 0, targetWidth, targetHeight * images, 0, 0, width, height, (ImageObserver)null);
				temp.dispose();
				source = i;
			}

			for(int var18 = 0; var18 < images; ++var18) {
				int[] var19 = new int[targetWidth * targetHeight];
				source.getRGB(0, targetHeight * var18, targetWidth, targetHeight, var19, 0, targetWidth);
				this.images[var18] = new byte[targetWidth * targetHeight * 4];

				for(int j = 0; j < var19.length; ++j) {
					int a = var19[j] >> 24 & 255;
					int r = var19[j] >> 16 & 255;
					int g = var19[j] >> 8 & 255;
					int b = var19[j] >> 0 & 255;
					this.images[var18][j * 4 + 0] = (byte)r;
					this.images[var18][j * 4 + 1] = (byte)g;
					this.images[var18][j * 4 + 2] = (byte)b;
					this.images[var18][j * 4 + 3] = (byte)a;
				}
			}

		}
	}

	public void onTick() {
		if(this.ticks >= this.tickRate) {
			++this.index;
			if(this.index >= this.images.length) {
				this.index = 0;
			}

			this.buffer = this.images[this.index];
			this.ticks = 0;
		}

		++this.ticks;
	}
}
