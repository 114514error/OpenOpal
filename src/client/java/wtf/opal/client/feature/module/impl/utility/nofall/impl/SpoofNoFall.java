package wtf.opal.client.feature.module.impl.utility.nofall.impl;

import wtf.opal.client.feature.module.impl.utility.nofall.NoFallModule;
import wtf.opal.client.feature.module.property.impl.bool.BooleanProperty;
import wtf.opal.client.feature.module.property.impl.mode.ModuleMode;
import wtf.opal.event.impl.game.player.movement.PreMovementPacketEvent;
import wtf.opal.event.subscriber.Subscribe;
import wtf.opal.utility.player.PlayerUtility;

public final class SpoofNoFall extends ModuleMode<NoFallModule> {

    private final BooleanProperty noGround = new BooleanProperty("No ground", this, false).hideIf(() -> !module.mode.is(NoFallModule.Mode.SPOOF));

    public SpoofNoFall(NoFallModule module) {
        super(module);
    }

    @Override
    public Enum<?> getEnumValue() {
        return NoFallModule.Mode.SPOOF;
    }

    @Subscribe
    public void onPreMovementPacket(final PreMovementPacketEvent event) {
        if (noGround.getValue()) {
            event.setOnGround(false);
        } else if (module.getFallDifference() >= PlayerUtility.getMaxFallDistance()) {
            module.syncFallDifference();
            event.setOnGround(true);
        }
    }
}
