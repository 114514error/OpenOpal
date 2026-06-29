package wtf.opal.duck;

import net.minecraft.network.packet.Packet;

public interface ClientConnectionAccess {
    void opal$channelReadSilent(Packet<?> packet);
    void opal$sendPacketSilent(Packet<?> packet);
}
