package wtf.opal.client.feature.module.impl.utility;

import wtf.opal.client.feature.module.Module;
import wtf.opal.client.feature.module.ModuleCategory;
import wtf.opal.client.feature.module.property.impl.GroupProperty;
import wtf.opal.client.feature.module.property.impl.bool.BooleanProperty;
import wtf.opal.event.impl.game.PreGameTickEvent;
import wtf.opal.event.subscriber.Subscribe;
import wtf.opal.mixin.MinecraftClientAccessor;

import static wtf.opal.client.Constants.mc;

public final class FastUseModule extends Module {

    private final BooleanProperty fastPlaceEnabled = new BooleanProperty("Enabled", true);

    public FastUseModule() {
        super("Fast Use", "Uses things faster.", ModuleCategory.UTILITY);
        this.addProperties(new GroupProperty("Placements", fastPlaceEnabled));
    }

    @Subscribe
    public void onPreGameTick(final PreGameTickEvent event) {
        if (!fastPlaceEnabled.getValue()) return;

        final MinecraftClientAccessor minecraftClientAccessor = (MinecraftClientAccessor) mc;

        minecraftClientAccessor.setItemUseCooldown(0);
    }

}
