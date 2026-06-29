package wtf.opal.client.feature.module.impl.utility.inventory;

import wtf.opal.client.OpalClient;
import wtf.opal.client.feature.module.Module;
import wtf.opal.client.feature.module.ModuleCategory;
import wtf.opal.client.feature.module.impl.utility.inventory.manager.InventoryManagerModule;
import wtf.opal.client.feature.module.property.impl.number.BoundedNumberProperty;
import wtf.opal.event.impl.game.PreGameTickEvent;
import wtf.opal.event.subscriber.Subscribe;

import static wtf.opal.client.Constants.mc;

public final class AutoArmorModule extends Module {

    private final BoundedNumberProperty delay = new BoundedNumberProperty("Delay", 150, 200, 0, 500, 10);

    public AutoArmorModule() {
        super("Auto Armor", "Delegates armor handling to Inventory Manager.", ModuleCategory.UTILITY);
        addProperties(delay);
    }

    @Subscribe
    public void onPreGameTickEvent(final PreGameTickEvent event) {
        if (mc.player == null) {
            return;
        }

        final InventoryManagerModule inventoryManagerModule = OpalClient.getInstance()
                .getModuleRepository()
                .getModule(InventoryManagerModule.class);

        if (inventoryManagerModule.isEnabled()) {
            return;
        }

        inventoryManagerModule.runLegacyAutoArmorOnly(delay.getRandomValue().longValue());
    }
}
