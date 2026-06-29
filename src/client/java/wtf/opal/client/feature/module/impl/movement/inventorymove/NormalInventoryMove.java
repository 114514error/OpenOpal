package wtf.opal.client.feature.module.impl.movement.inventorymove;

import wtf.opal.client.feature.module.impl.movement.InventoryMoveModule;
import wtf.opal.client.feature.module.property.impl.mode.ModuleMode;
import wtf.opal.event.impl.game.input.MoveInputEvent;
import wtf.opal.event.subscriber.Subscribe;

public final class NormalInventoryMove extends ModuleMode<InventoryMoveModule> {

    public NormalInventoryMove(final InventoryMoveModule module) {
        super(module);
    }

    @Override
    public Enum<?> getEnumValue() {
        return InventoryMoveModule.Mode.NORMAL;
    }

    @Subscribe
    public void onMoveInput(final MoveInputEvent event) {
        if (!module.canProcessScreenInput() || module.isNormalScreenBlocked()) {
            return;
        }

        module.applyMovementInput(event);
    }
}
