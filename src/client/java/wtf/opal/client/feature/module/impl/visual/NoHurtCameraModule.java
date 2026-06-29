package wtf.opal.client.feature.module.impl.visual;

import wtf.opal.client.feature.module.Module;
import wtf.opal.client.feature.module.ModuleCategory;
import wtf.opal.client.feature.module.property.impl.bool.BooleanProperty;

public final class
NoHurtCameraModule extends Module {
    public NoHurtCameraModule() {
        super("No Hurt Camera", "Disables the camera tilt when damaged.", ModuleCategory.VISUAL);
        addProperties(this.hideModelDamage);
    }

    private final BooleanProperty hideModelDamage = new BooleanProperty("No player model hurt", false);

    public boolean isHideModelDamage() {
        return this.hideModelDamage.getValue();
    }
}
