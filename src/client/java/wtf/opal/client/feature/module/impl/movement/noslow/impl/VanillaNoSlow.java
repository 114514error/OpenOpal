package wtf.opal.client.feature.module.impl.movement.noslow.impl;

import wtf.opal.client.feature.module.impl.movement.noslow.NoSlowModule;
import wtf.opal.client.feature.module.property.impl.mode.ModuleMode;
import wtf.opal.event.impl.game.player.movement.SlowdownEvent;
import wtf.opal.event.subscriber.Subscribe;

public final class VanillaNoSlow extends ModuleMode<NoSlowModule> {

    public VanillaNoSlow(final NoSlowModule module) {
        super(module);
    }

    @Subscribe
    public void onSlowdown(final SlowdownEvent event) {
        event.setCancelled();
    }

    @Override
    public Enum<?> getEnumValue() {
        return NoSlowModule.Mode.VANILLA;
    }

}
