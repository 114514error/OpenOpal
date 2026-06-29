package wtf.opal.mixin;

import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import wtf.opal.duck.EntityRenderStateAccess;

@Mixin(EntityRenderState.class)
public final class EntityRenderStateMixin implements EntityRenderStateAccess {

    @Unique
    private Entity entity;

    private EntityRenderStateMixin() {
    }

    @Override
    public Entity opal$getEntity() {
        return entity;
    }

    @Override
    public void opal$setEntity(final Entity entity) {
        this.entity = entity;
    }
}
