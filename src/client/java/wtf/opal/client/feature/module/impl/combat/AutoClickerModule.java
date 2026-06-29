package wtf.opal.client.feature.module.impl.combat;

import net.minecraft.util.hit.HitResult;
import wtf.opal.client.OpalClient;
import wtf.opal.client.feature.helper.impl.player.mouse.MouseHelper;
import wtf.opal.client.feature.helper.impl.player.swing.CPSProperty;
import wtf.opal.client.feature.helper.impl.player.swing.SwingDelay;
import wtf.opal.client.feature.module.Module;
import wtf.opal.client.feature.module.ModuleCategory;
import wtf.opal.client.feature.module.property.impl.bool.BooleanProperty;
import wtf.opal.client.feature.module.property.impl.bool.MultipleBooleanProperty;
import wtf.opal.event.impl.game.input.MouseHandleInputEvent;
import wtf.opal.event.impl.game.player.interaction.AttackDelayEvent;
import wtf.opal.event.subscriber.Subscribe;

import static wtf.opal.client.Constants.mc;

public final class AutoClickerModule extends Module {

    private final MultipleBooleanProperty mouseButtons = new MultipleBooleanProperty("Mouse buttons",
            new BooleanProperty("Left", true),
            new BooleanProperty("Right", false)
    );
    private final CPSProperty cpsProperty = new CPSProperty(this);
    private final BooleanProperty requirePressed = new BooleanProperty("Require pressed", true);

    public AutoClickerModule() {
        super("Auto Clicker", "Clicks for you automatically.", ModuleCategory.COMBAT);
        addProperties(mouseButtons, requirePressed);
    }

    @Subscribe
    public void onHandleInput(final MouseHandleInputEvent event) {
        final BlockModule blockModule = OpalClient.getInstance().getModuleRepository().getModule(BlockModule.class);
        final boolean allowSwingWhenUsing = blockModule.isEnabled() && blockModule.isSwingAllowed();
        if (mc.player.isUsingItem() && !allowSwingWhenUsing) {
            return;
        }

        if (SwingDelay.isSwingAvailable(cpsProperty)) {
            if (mouseButtons.getProperty("Left").getValue() && mc.crosshairTarget != null && mc.crosshairTarget.getType() != HitResult.Type.BLOCK) {
                if (!requirePressed.getValue() || mc.options.attackKey.isPressed()) {
                    MouseHelper.getLeftButton().setPressed();
                }
            }

            if (mouseButtons.getProperty("Right").getValue()) {
                if (!requirePressed.getValue() || mc.options.useKey.isPressed()) {
                    MouseHelper.getRightButton().setPressed();
                }
            }
        }
    }

    @Subscribe
    public void onAttackCooldown(AttackDelayEvent event) {
        event.setDelay(0);
    }

}
