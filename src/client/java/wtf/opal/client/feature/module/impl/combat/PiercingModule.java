package wtf.opal.client.feature.module.impl.combat;

import wtf.opal.client.feature.module.Module;
import wtf.opal.client.feature.module.ModuleCategory;

public final class PiercingModule extends Module {

    // hooked in GameRendererMixin#redirectPassedThroughBlockDistance

    public PiercingModule() {
        super("Piercing", "Allows you to take players through blocks.", ModuleCategory.COMBAT);
    }

}
