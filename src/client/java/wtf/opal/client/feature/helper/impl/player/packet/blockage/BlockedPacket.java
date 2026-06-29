package wtf.opal.client.feature.helper.impl.player.packet.blockage;

import net.minecraft.network.packet.Packet;

public final class BlockedPacket {
    private final Packet<?> packet;
    private final long id;

    public BlockedPacket(Packet<?> packet, long id) {
        this.packet = packet;
        this.id = id;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public long getId() {
        return id;
    }
}
