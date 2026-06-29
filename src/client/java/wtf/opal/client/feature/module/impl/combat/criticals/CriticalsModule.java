package wtf.opal.client.feature.module.impl.combat.criticals;

import net.minecraft.entity.LivingEntity;
import wtf.opal.client.feature.module.Module;
import wtf.opal.client.feature.module.ModuleCategory;
import wtf.opal.client.feature.module.impl.combat.criticals.impl.GrimCriticals;
import wtf.opal.client.feature.module.impl.combat.criticals.impl.HeypixelCriticals;
import wtf.opal.client.feature.module.impl.combat.criticals.impl.PacketCriticals;
import wtf.opal.client.feature.module.property.impl.bool.BooleanProperty;
import wtf.opal.client.feature.module.property.impl.mode.ModeProperty;
import wtf.opal.utility.misc.chat.ChatUtility;
import wtf.opal.utility.player.PlayerUtility;

import java.util.Locale;

import static wtf.opal.client.Constants.mc;

public final class CriticalsModule extends Module {

    private final ModeProperty<Mode> mode = new ModeProperty<>("Mode", this, Mode.PACKET);
    private final BooleanProperty debug = new BooleanProperty("Debug", false);

    public CriticalsModule() {
        super("Criticals", "Forces every attack to be a critical hit.", ModuleCategory.COMBAT);
        addProperties(mode, debug);
        addModuleModes(mode, new PacketCriticals(this), new GrimCriticals(this), new HeypixelCriticals(this));
    }

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }

    public boolean isDebug() {
        return this.debug.getValue();
    }

    public void debug(final String message) {
        if (this.isDebug()) {
            ChatUtility.print("Criticals Debug | " + message);
        }
    }

    public void debugDamage(final String mode, final LivingEntity target, final boolean forced, final String detail) {
        if (!this.isDebug() || mc.player == null) {
            return;
        }

        final double baseDamage = Math.max(PlayerUtility.getStackAttackDamage(mc.player.getMainHandStack()), 0.5D);
        final double extraDamage = forced ? baseDamage * 0.5D : 0.0D;
        final double critDamage = baseDamage + extraDamage;
        this.debug(mode + " " + (forced ? "forced" : "skipped")
                + " target=" + target.getName().getString()
                + " base=" + format(baseDamage)
                + " crit=" + format(critDamage)
                + " extra=" + format(extraDamage)
                + (detail == null || detail.isEmpty() ? "" : " | " + detail));
    }

    private static String format(final double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    public enum Mode {
        PACKET("Packet"),
        GRIM("Grim"),
        HEYPIXEL("Heypixel");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
