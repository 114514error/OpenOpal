package wtf.opal.client.feature.module.impl.world;

import wtf.opal.client.feature.helper.impl.player.timer.TimerHelper;
import wtf.opal.client.feature.module.Module;
import wtf.opal.client.feature.module.ModuleCategory;
import wtf.opal.client.feature.module.property.impl.number.NumberProperty;
import wtf.opal.event.impl.game.PreGameTickEvent;
import wtf.opal.event.subscriber.Subscribe;

public final class
TimerModule extends Module {

    private final NumberProperty gameSpeed = new NumberProperty("Game speed", "x", 2F, 0.05F, 10F, 0.05F);

    public TimerModule() {
        super("Timer", "Modifies your game speed.", ModuleCategory.WORLD);

        addProperties(gameSpeed);
    }

    @Subscribe
    public void onPreGameTick(final PreGameTickEvent event) {
        TimerHelper.getInstance().timer = gameSpeed.getValue().floatValue();
    }

    @Override
    protected void onDisable() {
        TimerHelper.getInstance().timer = 1F;
        super.onDisable();
    }
}
