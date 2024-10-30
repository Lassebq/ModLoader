package io.github.lassebq.modloader;

import net.minecraft.entity.Entity;

public class NetClientHandlerEntity {
    public Class<Entity> entityClass = null;
    public boolean entityHasOwner = false;

    public NetClientHandlerEntity(Class<Entity> class1, boolean flag) {
        this.entityClass = class1;
        this.entityHasOwner = flag;
    }
}
