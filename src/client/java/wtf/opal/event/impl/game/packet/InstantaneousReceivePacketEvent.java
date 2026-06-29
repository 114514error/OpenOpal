package wtf.opal.event.impl.game.packet;

import net.minecraft.network.packet.Packet;
import wtf.opal.event.EventCancellable;

public final class InstantaneousReceivePacketEvent extends EventCancellable {

    private final Packet<?> packet;

    public InstantaneousReceivePacketEvent(final Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

}
