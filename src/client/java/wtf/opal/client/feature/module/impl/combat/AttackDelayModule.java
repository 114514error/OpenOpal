package wtf.opal.client.feature.module.impl.combat;

import wtf.opal.client.feature.module.Module;
import wtf.opal.client.feature.module.ModuleCategory;
import wtf.opal.client.feature.module.property.impl.number.NumberProperty;
import wtf.opal.event.impl.game.player.interaction.AttackDelayEvent;
import wtf.opal.event.subscriber.Subscribe;

public final class AttackDelayModule extends Module {
    private final NumberProperty maxCooldown = new NumberProperty("Max cooldown", 0, 0, 9, 1);

    public AttackDelayModule() {
        super("Attack Delay", "Removes the delay after missing an attack.", ModuleCategory.COMBAT);
        this.addProperties(this.maxCooldown);
    }

    @Subscribe
    public void onAttackCooldown(AttackDelayEvent event) {
        event.setDelay(this.maxCooldown.getValue().intValue());
    }
}
