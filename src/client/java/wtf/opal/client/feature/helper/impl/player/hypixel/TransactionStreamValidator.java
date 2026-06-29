package wtf.opal.client.feature.helper.impl.player.hypixel;

import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import wtf.opal.client.feature.helper.IHelper;
import wtf.opal.client.feature.helper.impl.LocalDataWatch;
import wtf.opal.client.feature.helper.impl.server.impl.HypixelServer;
import wtf.opal.event.EventDispatcher;
import wtf.opal.event.impl.game.JoinWorldEvent;
import wtf.opal.event.impl.game.packet.SendPacketEvent;
import wtf.opal.event.subscriber.Subscribe;

public final class
TransactionStreamValidator implements IHelper { // should be removed in releases

    private Integer lastTransactionId;

    @Subscribe
    public void onSendPacket(final SendPacketEvent event) {
        if (event.getPacket() instanceof CommonPongC2SPacket packet) {
            if (packet.getParameter() == 0) return;
            if (this.lastTransactionId != null && packet.getParameter() != this.lastTransactionId - 1) {
//                ChatUtility.error("Invalid transaction id: " + packet.getParameter() + " prev: " + this.lastTransactionId);
                System.out.println("Invalid transaction id: " + packet.getParameter() + " prev: " + this.lastTransactionId);
            }
            this.lastTransactionId = packet.getParameter();
        }
    }

    @Subscribe
    public void onJoinWorld(final JoinWorldEvent event) {
        this.lastTransactionId = null;
    }

    @Override
    public boolean isHandlingEvents() {
        return LocalDataWatch.get().getKnownServerManager().getCurrentServer() instanceof HypixelServer;
    }

    private static TransactionStreamValidator instance;

    public static void setInstance() {
        instance = new TransactionStreamValidator();
        EventDispatcher.subscribe(instance);
    }
}
