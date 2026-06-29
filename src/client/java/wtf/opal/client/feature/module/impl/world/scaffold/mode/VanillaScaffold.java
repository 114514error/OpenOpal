package wtf.opal.client.feature.module.impl.world.scaffold.mode;

import net.minecraft.entity.effect.StatusEffects;
import wtf.opal.client.feature.module.impl.world.scaffold.ScaffoldModule;
import wtf.opal.client.feature.module.impl.world.scaffold.ScaffoldSettings;
import wtf.opal.client.feature.module.property.impl.mode.ModuleMode;
import wtf.opal.event.impl.game.player.interaction.block.BlockPlacedEvent;
import wtf.opal.event.subscriber.Subscribe;

import static wtf.opal.client.Constants.mc;

public final class VanillaScaffold extends ModuleMode<ScaffoldModule> {

    public VanillaScaffold(final ScaffoldModule module) {
        super(module);
    }

    @Subscribe
    public void onBlockPlaced(final BlockPlacedEvent event) {
        if (mc.player.hasStatusEffect(StatusEffects.JUMP_BOOST) || !module.getSettings().isTowerEnabled() || mc.options.useKey.isPressed()) {
            return;
        }

        if (mc.options.jumpKey.isPressed()) {
            mc.player.jump();
        }
    }

    @Override
    public Enum<?> getEnumValue() {
        return ScaffoldSettings.Mode.VANILLA;
    }
}
