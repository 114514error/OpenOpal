package wtf.opal.client.feature.module.impl.movement.speed.impl;

import wtf.opal.client.feature.module.impl.movement.speed.SpeedModule;
import wtf.opal.client.feature.module.property.impl.bool.BooleanProperty;
import wtf.opal.client.feature.module.property.impl.mode.ModuleMode;
import wtf.opal.client.feature.module.property.impl.number.NumberProperty;
import wtf.opal.event.impl.game.input.MoveInputEvent;
import wtf.opal.event.impl.game.player.movement.PostMoveEvent;
import wtf.opal.event.subscriber.Subscribe;
import wtf.opal.utility.player.MoveUtility;

import static wtf.opal.client.Constants.mc;

public final class VanillaSpeed extends ModuleMode<SpeedModule> {

    private final NumberProperty speedProperty = new NumberProperty("Speed", this, 1.D, 0.1D, 10.D, 0.1D).hideIf(() -> this.module.getActiveMode() != this);
    private final BooleanProperty autoJump = new BooleanProperty("Auto jump", this, true).hideIf(() -> this.module.getActiveMode() != this);

    public VanillaSpeed(SpeedModule module) {
        super(module);
    }

    @Override
    public Enum<?> getEnumValue() {
        return SpeedModule.Mode.VANILLA;
    }

    @Subscribe
    public void onPostMove(PostMoveEvent event) {
        final double speed = this.speedProperty.getValue();
        if (MoveUtility.isMoving()) {
            MoveUtility.setSpeed(speed);
        } else {
            MoveUtility.setSpeed(0);
        }
    }

    @Subscribe
    public void onMoveInput(MoveInputEvent event) {
        if (this.autoJump.getValue() && MoveUtility.isMoving()) {
            event.setJump(true);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.player == null) return;
        final double maxSpeed = MoveUtility.getSwiftnessSpeed(0.221D);
        MoveUtility.setSpeed(Math.min(MoveUtility.getSpeed(), maxSpeed));
    }
}
