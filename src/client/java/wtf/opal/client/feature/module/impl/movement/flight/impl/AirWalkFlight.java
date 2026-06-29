package wtf.opal.client.feature.module.impl.movement.flight.impl;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShapes;
import wtf.opal.client.feature.module.impl.movement.flight.FlightModule;
import wtf.opal.client.feature.module.property.impl.mode.ModuleMode;
import wtf.opal.event.impl.game.world.BlockShapeEvent;
import wtf.opal.event.subscriber.Subscribe;

import static wtf.opal.client.Constants.mc;

public final class
AirWalkFlight extends ModuleMode<FlightModule> {
    public AirWalkFlight(FlightModule module) {
        super(module);
    }

    @Subscribe
    public void onBlockShape(BlockShapeEvent event) {
        BlockPos blockPos = event.getBlockPos();
        if (blockPos.getY() < mc.player.getY() && event.getBlockState().isAir()) {
            event.setVoxelShape(VoxelShapes.cuboid(-2.0D, 0.0D, -2.0D, 2.0D, 1.0D, 2.0D));
        }
    }

    @Override
    public Enum<?> getEnumValue() {
        return FlightModule.Mode.AIR_WALK;
    }
}
