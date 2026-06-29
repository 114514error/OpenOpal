package wtf.opal.client.feature.module.impl.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.scoreboard.AbstractTeam;
import wtf.opal.client.OpalClient;
import wtf.opal.client.feature.module.Module;
import wtf.opal.client.feature.module.ModuleCategory;
import wtf.opal.client.feature.module.property.impl.bool.BooleanProperty;
import wtf.opal.utility.player.PlayerUtility;

import static wtf.opal.client.Constants.mc;

public final class TeamsModule extends Module {

    private final BooleanProperty scoreboard = new BooleanProperty("Scoreboard", true);
    private final BooleanProperty color = new BooleanProperty("Color", true);

    public TeamsModule() {
        super("Teams", "Prevents you from attacking teammates.", ModuleCategory.COMBAT);
        addProperties(scoreboard, color);
    }

    public static boolean isTeammate(final Entity entity) {
        final TeamsModule module = OpalClient.getInstance().getModuleRepository().getModule(TeamsModule.class);
        if (module == null || !module.isEnabled() || mc.player == null || !(entity instanceof LivingEntity livingEntity)) {
            return false;
        }

        if (module.scoreboard.getValue()) {
            final AbstractTeam playerTeam = mc.player.getScoreboardTeam();
            final AbstractTeam entityTeam = livingEntity.getScoreboardTeam();
            if (playerTeam != null && entityTeam != null && playerTeam.isEqual(entityTeam)) {
                return true;
            }
        }

        return module.color.getValue() && PlayerUtility.areOnSameTeam(mc.player, livingEntity);
    }
}
