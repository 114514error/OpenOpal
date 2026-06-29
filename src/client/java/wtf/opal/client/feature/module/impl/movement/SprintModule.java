package wtf.opal.client.feature.module.impl.movement;

import wtf.opal.client.OpalClient;
import wtf.opal.client.feature.module.Module;
import wtf.opal.client.feature.module.ModuleCategory;
import wtf.opal.client.feature.module.impl.combat.killaura.KillAuraModule;
import wtf.opal.client.feature.module.property.impl.bool.BooleanProperty;
import wtf.opal.event.impl.game.PreGameTickEvent;
import wtf.opal.event.impl.game.player.movement.KeepSprintEvent;
import wtf.opal.event.subscriber.Subscribe;

import static wtf.opal.client.Constants.mc;

public final class SprintModule extends Module {

    private final BooleanProperty omniSprint = new BooleanProperty("Omnidirectional", false);
    private final BooleanProperty keepSprint = new BooleanProperty("Keep sprint", true);

    public SprintModule() {
        super("Sprint", "Modifies the logic behind sprinting.", ModuleCategory.MOVEMENT);
        addProperties(omniSprint, keepSprint);
        setEnabled(true);
    }

    @Subscribe
    public void onPreGameTick(final PreGameTickEvent event) {
        mc.options.sprintKey.setPressed(true);
    }

    @Subscribe
    public void onKeepSprint(final KeepSprintEvent event) {
        if (!keepSprint.getValue()) return;

        final KillAuraModule killAuraModule = OpalClient.getInstance().getModuleRepository().getModule(KillAuraModule.class);
        if (killAuraModule.isEnabled() && killAuraModule.isTargeting()) {
            return;
        }

        event.setCancelled();
    }

    public static boolean isOmniSprint() {
        final SprintModule sprintModule = OpalClient.getInstance().getModuleRepository().getModule(SprintModule.class);
        return sprintModule.isEnabled() && sprintModule.omniSprint.getValue();
    }
}
