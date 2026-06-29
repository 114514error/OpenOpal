package wtf.opal.client.feature.module.impl.visual.overlay.impl.targetinfo;

import wtf.opal.client.feature.helper.impl.render.ScaleProperty;
import wtf.opal.client.feature.module.impl.visual.overlay.OverlayModule;
import wtf.opal.client.feature.module.property.impl.GroupProperty;
import wtf.opal.client.feature.module.property.impl.ScreenPositionProperty;
import wtf.opal.client.feature.module.property.impl.bool.BooleanProperty;

public final class TargetInfoSettings {

    private final BooleanProperty enabled;
    private final ScreenPositionProperty screenPosition;
    private final ScaleProperty scale;

    TargetInfoSettings(final OverlayModule module) {
        this.enabled = new BooleanProperty("Enabled", true);
        this.screenPosition = new ScreenPositionProperty("Screen Position", 0.43F, 0.65F);
        this.scale = ScaleProperty.newNVGElement();
        module.addProperties(new GroupProperty("Target information", this.enabled, this.screenPosition, this.scale.get()));
    }

    public boolean isEnabled() {
        return this.enabled.getValue();
    }

    public ScreenPositionProperty getScreenPosition() {
        return this.screenPosition;
    }

    public float getScale() {
        return scale.getScale();
    }

}
