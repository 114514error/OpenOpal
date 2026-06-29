package wtf.opal.client.feature.module.impl.movement;

import wtf.opal.client.feature.module.Module;
import wtf.opal.client.feature.module.ModuleCategory;
import wtf.opal.client.feature.module.property.impl.bool.BooleanProperty;
import wtf.opal.client.feature.module.property.impl.bool.MultipleBooleanProperty;
import wtf.opal.client.feature.module.property.impl.number.NumberProperty;
import wtf.opal.event.impl.game.player.movement.PostMoveEvent;
import wtf.opal.event.subscriber.Subscribe;
import wtf.opal.utility.player.MoveUtility;

import static wtf.opal.client.Constants.mc;

public final class FastStopModule extends Module {

    private final MultipleBooleanProperty conditions = new MultipleBooleanProperty("Conditions",
            new BooleanProperty("On ground", true),
            new BooleanProperty("In air", true));

    private final NumberProperty multiplier = new NumberProperty("Multiplier", "x", 0.5, 0, 1, 0.05);

    public FastStopModule() {
        super("Fast Stop", "Makes you stop moving faster.", ModuleCategory.MOVEMENT);
        this.addProperties(this.conditions, this.multiplier);
    }

    @Subscribe
    public void onPostMove(final PostMoveEvent event) {
        if ((mc.player.getVelocity().getX() != 0 || mc.player.getVelocity().getZ() != 0) && !MoveUtility.isMoving()) {
            final boolean allowOnGround = conditions.getProperty("On ground").getValue();
            final boolean allowInAir = conditions.getProperty("In air").getValue();

            if (!allowOnGround && !allowInAir) {
                return;
            }
            if (!allowOnGround && mc.player.isOnGround()) {
                return;
            }
            if (!allowInAir && !mc.player.isOnGround()) {
                return;
            }

            if (mc.player.hurtTime != 0) return;

            mc.player.setVelocity(mc.player.getVelocity().multiply(this.multiplier.getValue(), 1, this.multiplier.getValue()));
        }
    }
}
