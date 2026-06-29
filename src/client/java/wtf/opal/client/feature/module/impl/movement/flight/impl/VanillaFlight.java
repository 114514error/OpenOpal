package wtf.opal.client.feature.module.impl.movement.flight.impl;

import net.minecraft.util.math.Direction;
import wtf.opal.client.feature.module.impl.movement.flight.FlightModule;
import wtf.opal.client.feature.module.property.impl.mode.ModuleMode;
import wtf.opal.client.feature.module.property.impl.number.NumberProperty;
import wtf.opal.event.impl.game.input.MoveInputEvent;
import wtf.opal.event.impl.game.player.movement.PostMoveEvent;
import wtf.opal.event.subscriber.Subscribe;
import wtf.opal.utility.player.MoveUtility;

import static wtf.opal.client.Constants.mc;

public final class VanillaFlight extends ModuleMode<FlightModule> {

    private final NumberProperty speedProperty = new NumberProperty("Speed", this, 1.D, 0.1D, 10.D, 0.1D).hideIf(() -> this.module.getActiveMode() != this);

    public VanillaFlight(FlightModule module) {
        super(module);
    }

    @Override
    public Enum<?> getEnumValue() {
        return FlightModule.Mode.VANILLA;
    }

    @Subscribe
    public void onPostMove(PostMoveEvent event) {
        final double speed = this.speedProperty.getValue();
        double motionY = 0.D;
        if (mc.options.jumpKey.isPressed()) {
            motionY = speed;
        } else if (mc.options.sneakKey.isPressed()) {
            motionY = -speed;
        }
        if (MoveUtility.isMoving()) {
            MoveUtility.setSpeed(speed);
        } else {
            MoveUtility.setSpeed(0);
        }
        mc.player.setVelocity(mc.player.getVelocity().withAxis(Direction.Axis.Y, motionY));
    }

    @Subscribe
    public void onMoveInput(final MoveInputEvent event) {
        event.setSneak(false);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.player == null) return;
        final double maxSpeed = MoveUtility.getSwiftnessSpeed(0.221D);
        MoveUtility.setSpeed(Math.min(MoveUtility.getSpeed(), maxSpeed));
        mc.player.setVelocity(mc.player.getVelocity().withAxis(Direction.Axis.Y, 0));
    }
}
