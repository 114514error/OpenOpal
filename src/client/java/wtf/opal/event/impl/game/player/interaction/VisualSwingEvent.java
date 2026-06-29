package wtf.opal.event.impl.game.player.interaction;

import net.minecraft.util.Hand;
import wtf.opal.event.EventCancellable;

public final class VisualSwingEvent extends EventCancellable {

    public final Hand hand;

    public VisualSwingEvent(final Hand hand) {
        this.hand = hand;
    }

    public Hand getHand() {
        return this.hand;
    }

}
