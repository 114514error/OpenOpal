package wtf.opal.client.feature.module.impl.utility;

import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import wtf.opal.client.feature.helper.impl.player.packet.blockage.block.holder.BlockHolder;
import wtf.opal.client.feature.helper.impl.player.packet.blockage.impl.InboundNetworkBlockage;
import wtf.opal.client.feature.helper.impl.player.packet.blockage.impl.OutboundNetworkBlockage;
import wtf.opal.client.feature.module.Module;
import wtf.opal.client.feature.module.ModuleCategory;
import wtf.opal.client.feature.module.property.impl.bool.BooleanProperty;
import wtf.opal.client.feature.module.property.impl.bool.MultipleBooleanProperty;
import wtf.opal.client.feature.module.property.impl.number.BoundedNumberProperty;
import wtf.opal.event.impl.game.PreGameTickEvent;
import wtf.opal.event.subscriber.Subscribe;
import wtf.opal.utility.misc.math.RandomUtility;
import wtf.opal.utility.misc.time.Stopwatch;

public final class BlinkModule extends Module {

    private final MultipleBooleanProperty blinkDirections = new MultipleBooleanProperty("Direction",
            new BooleanProperty("Inbound", true),
            new BooleanProperty("Outbound", true));

    private final BooleanProperty pulse = new BooleanProperty("Pulse", false);
    private final BoundedNumberProperty pulseDelay = new BoundedNumberProperty("Pulse delay", "ms", 1000, 2000, 50, 10000, 1)
            .hideIf(() -> !pulse.getValue());

    private final Stopwatch oPulseTimer = new Stopwatch();
    private final Stopwatch iPulseTimer = new Stopwatch();

    public BlinkModule() {
        super("Blink", "Blocks your network connection.", ModuleCategory.UTILITY);
        addProperties(blinkDirections, pulse, pulseDelay);
    }

    private final BlockHolder iBlockHolder = new BlockHolder(InboundNetworkBlockage.get());
    private final BlockHolder oBlockHolder = new BlockHolder(OutboundNetworkBlockage.get());

    @Subscribe
    public void onPreGameTick(final PreGameTickEvent event) {
        if (blinkDirections.getProperty("Inbound").getValue()) {
            this.iBlockHolder.block(p -> p, p -> p instanceof CommonPingS2CPacket);

            if (pulse.getValue() && iPulseTimer.hasTimeElapsed(RandomUtility.getRandomInt((int) pulseDelay.getMinValue(), (int) pulseDelay.getMaxValue()), true)) {
                this.iBlockHolder.release();
            }
        }
        if (blinkDirections.getProperty("Outbound").getValue()) {
            this.oBlockHolder.block();

            if (pulse.getValue() && oPulseTimer.hasTimeElapsed(RandomUtility.getRandomInt((int) pulseDelay.getMinValue(), (int) pulseDelay.getMaxValue()), true)) {
                this.oBlockHolder.release();
            }
        }
    }

    @Override
    protected void onDisable() {
        this.iBlockHolder.release();
        this.oBlockHolder.release();
    }
}
