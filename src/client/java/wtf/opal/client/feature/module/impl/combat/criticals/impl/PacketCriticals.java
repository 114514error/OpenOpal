package wtf.opal.client.feature.module.impl.combat.criticals.impl;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import wtf.opal.client.feature.module.impl.combat.criticals.CriticalsModule;
import wtf.opal.client.feature.module.property.impl.bool.BooleanProperty;
import wtf.opal.client.feature.module.property.impl.mode.ModuleMode;
import wtf.opal.event.impl.game.player.interaction.AttackEvent;
import wtf.opal.event.subscriber.Subscribe;
import wtf.opal.mixin.ClientPlayerEntityAccessor;
import wtf.opal.utility.player.PlayerUtility;

import static wtf.opal.client.Constants.mc;

public final class

PacketCriticals extends ModuleMode<CriticalsModule> {
    public PacketCriticals(CriticalsModule module) {
        super(module);
    }

    private final BooleanProperty groundOnly = new BooleanProperty("Ground only", this, false).hideIf(() -> this.module.getActiveMode() != this);

    @Subscribe
    public void onAttack(AttackEvent event) {
        if (mc.player == null) {
            return;
        }

        if (event.getTarget() instanceof LivingEntity target) {
            if (!PlayerUtility.isCriticalHitAvailable()) {
                this.module.debugDamage("Packet", target, false, "critical state unavailable"
                        + ", onGround=" + mc.player.isOnGround()
                        + ", fall=" + format(mc.player.fallDistance));
                return;
            }
            if (this.groundOnly.getValue() && !mc.player.isOnGround()) {
                this.module.debugDamage("Packet", target, false, "ground-only blocked");
                return;
            }

            final Box box = mc.player.getBoundingBox().offset(0.0D, 0.0625D, 0.0D);
            if (!PlayerUtility.isBoxEmpty(box)) {
                this.module.debugDamage("Packet", target, false, "headroom blocked");
                return;
            }

            final Vec3d pos = mc.player.getEntityPos();
            final boolean ground = mc.player.isOnGround();
            final ClientPlayerEntityAccessor accessor = (ClientPlayerEntityAccessor) mc.player;

            mc.player.setPosition(pos.add(0.0D, 0.0625D, 0.0D));
            mc.player.setOnGround(false);
            accessor.callSendMovementPackets();

            mc.player.setPosition(pos.add(0.0D, 0.00125D, 0.0D));
            mc.player.setOnGround(false);
            accessor.callSendMovementPackets();

            mc.player.setPosition(pos);
            mc.player.setOnGround(ground);
            this.module.debugDamage("Packet", target, true, "sent offsets=0.0625,0.00125 ground=" + ground);
        }
    }

    private static String format(final double value) {
        return String.format(java.util.Locale.ROOT, "%.2f", value);
    }

    @Override
    public Enum<?> getEnumValue() {
        return CriticalsModule.Mode.PACKET;
    }
}
