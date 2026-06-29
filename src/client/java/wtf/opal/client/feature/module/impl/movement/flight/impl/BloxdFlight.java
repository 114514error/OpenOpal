package wtf.opal.client.feature.module.impl.movement.flight.impl;

import net.minecraft.util.math.Direction;
import wtf.opal.client.OpalClient;
import wtf.opal.client.feature.helper.impl.LocalDataWatch;
import wtf.opal.client.feature.module.impl.movement.flight.FlightModule;
import wtf.opal.client.feature.module.impl.movement.physics.PhysicsModule;
import wtf.opal.client.feature.module.property.impl.mode.ModuleMode;
import wtf.opal.client.feature.module.property.impl.number.NumberProperty;
import wtf.opal.event.impl.game.player.movement.PostMoveEvent;
import wtf.opal.event.impl.game.player.movement.step.StepEvent;
import wtf.opal.event.subscriber.Subscribe;
import wtf.opal.utility.player.MoveUtility;

import static wtf.opal.client.Constants.mc;

public final class BloxdFlight extends ModuleMode<FlightModule> {

    private final NumberProperty speedProperty = new NumberProperty("Speed", this, 1.D, 0.1D, 10.D, 0.1D).id("speedBloxd").hideIf(() -> this.module.getActiveMode() != this);
    private final NumberProperty heightProperty = new NumberProperty("Height", this, 1.D, 0.1D, 5.D, 0.1D).id("heightBloxd").hideIf(() -> this.module.getActiveMode() != this);

    public BloxdFlight(FlightModule module) {
        super(module);
    }

    @Override
    public Enum<?> getEnumValue() {
        return FlightModule.Mode.BLOXD;
    }

    private boolean isVelocityExempted() {
        return !LocalDataWatch.get().velocityStopwatch.hasTimeElapsed(1300L);
    }

    @Subscribe(priority = 4)
    public void onPostMove(PostMoveEvent event) {
        if (!this.isVelocityExempted()) {
            final PhysicsModule physicsModule = OpalClient.getInstance().getModuleRepository().getModule(PhysicsModule.class);
            if (physicsModule.isEnabled()) {
                if (mc.player.horizontalCollision) {
                    physicsModule.getPhysics().velocity = LocalDataWatch.get().airTicks <= 1 ? 8.0D : (30.0D * this.heightProperty.getValue());
                }
            }
        }
    }

    @Subscribe(priority = -1)
    public void onStep(final StepEvent event) {
        event.setStepHeight(0.0F);
    }

    @Subscribe
    public void onPostMoveLow(final PostMoveEvent event) {
        if (this.isVelocityExempted()) {
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
    }
}
