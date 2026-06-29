package wtf.opal.client.feature.module.impl.utility;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import wtf.opal.client.feature.module.Module;
import wtf.opal.client.feature.module.ModuleCategory;
import wtf.opal.client.feature.module.property.impl.StringProperty;
import wtf.opal.event.impl.game.PreGameTickEvent;
import wtf.opal.event.impl.game.packet.ReceivePacketEvent;
import wtf.opal.event.subscriber.Subscribe;
import wtf.opal.utility.misc.chat.ChatUtility;
import wtf.opal.utility.misc.time.Stopwatch;

public final class PartySpamModule extends Module {

    private final StringProperty username = new StringProperty("Username", "");

    private final Stopwatch stopwatch = new Stopwatch();
    private boolean state;

    public PartySpamModule() {
        super("Party Spam", "Spams specified player with party invites.", ModuleCategory.UTILITY);
        addProperties(username);
    }

    @Subscribe
    public void onPreGameTick(final PreGameTickEvent event) {
        if (username.getValue().isEmpty()) return;

        final String command = state ? "party " + username.getValue() : "party disband";

        if (stopwatch.hasTimeElapsed(250, true)) {
            ChatUtility.sendCommand(command);
            state = !state;
        }
    }

}
