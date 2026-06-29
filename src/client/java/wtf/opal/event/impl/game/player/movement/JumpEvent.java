package wtf.opal.event.impl.game.player.movement;

import wtf.opal.event.EventCancellable;

public final class JumpEvent extends EventCancellable {

    private boolean sprinting;

    public JumpEvent(final boolean sprinting) {
        this.sprinting = sprinting;
    }

    public boolean isSprinting() {
        return sprinting;
    }

    public void setSprinting(boolean sprinting) {
        this.sprinting = sprinting;
    }
}
