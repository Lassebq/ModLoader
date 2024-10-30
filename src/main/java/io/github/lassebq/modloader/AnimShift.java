package io.github.lassebq.modloader;

public class AnimShift extends AnimBase {
    private final int h;
    private final int v;

    public AnimShift(int spriteID, String spritePath, int h, int v) {
        super(spriteID, spritePath);
        this.h = h;
        this.v = v;
        this.getCleanFrame();
    }

    public void tick() {
        this.animFrame();
        this.copyFrameToArray();
    }

    public void animFrame() {
        this.shiftFrame(this.h, this.v, true, true);
    }
}
