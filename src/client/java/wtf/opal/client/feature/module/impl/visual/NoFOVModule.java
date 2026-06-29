package wtf.opal.client.feature.module.impl.visual;

import wtf.opal.client.feature.module.Module;
import wtf.opal.client.feature.module.ModuleCategory;

public final class NoFOVModule extends Module {
    public NoFOVModule() {
        super("No FOV", "Locks your FOV and disables FOV changes.", ModuleCategory.VISUAL);
    }
}
