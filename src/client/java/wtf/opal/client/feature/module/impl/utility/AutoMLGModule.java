package wtf.opal.client.feature.module.impl.utility;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import wtf.opal.client.feature.helper.impl.player.rotation.RotationHelper;
import wtf.opal.client.feature.helper.impl.player.rotation.model.impl.InstantRotationModel;
import wtf.opal.client.feature.helper.impl.player.slot.SlotHelper;
import wtf.opal.client.feature.module.Module;
import wtf.opal.client.feature.module.ModuleCategory;
import wtf.opal.client.feature.module.property.impl.bool.BooleanProperty;
import wtf.opal.client.feature.module.property.impl.number.NumberProperty;
import wtf.opal.event.impl.game.PreGameTickEvent;
import wtf.opal.event.impl.game.input.MoveInputEvent;
import wtf.opal.event.subscriber.Subscribe;
import wtf.opal.utility.player.RotationUtility;

import static wtf.opal.client.Constants.mc;

public final class AutoMLGModule extends Module {

    private final NumberProperty fallDistance = new NumberProperty("Fall Distance", 3.0D, 1.0D, 10.0D, 0.1D);
    private final NumberProperty predictTicks = new NumberProperty("Predict Ticks", "ticks", 2.0D, 1.0D, 5.0D, 1.0D);
    private final BooleanProperty solidCheck = new BooleanProperty("Solid Check", true);
    private final BooleanProperty recovery = new BooleanProperty("Recovery", true);

    private float accumulatedFall;
    private double lastY;
    private int restoreSlot = -1;
    private boolean waterPlaced;
    private boolean readyToPlace;
    private boolean recoveryActive;
    private int recoveryDelay;
    private int recoveryTriesLeft;
    private int recoverySlot = -1;
    private BlockPos placedWaterPos;
    private int postPlaceCooldown;
    private int postActionCooldown;
    private int retryCooldown;

    public AutoMLGModule() {
        super("AutoMLG", "Places and recovers water to break falls.", ModuleCategory.UTILITY);
        this.addProperties(this.fallDistance, this.predictTicks, this.solidCheck, this.recovery);
    }

    @Subscribe
    public void onPreGameTick(final PreGameTickEvent event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) {
            this.resetState();
            return;
        }

        if (mc.player.isSpectator() || mc.player.getAbilities().allowFlying || mc.player.getAbilities().flying) {
            this.resetState();
            return;
        }

        this.updateFallState();
        this.tickCooldowns();
        this.restoreSlotIfNeeded();

        if (mc.player.isOnGround() || this.accumulatedFall <= 0.0F) {
            this.waterPlaced = false;
            this.readyToPlace = false;
        }

        if (this.recoveryActive) {
            this.handleRecovery();
            return;
        }

        if (this.tryFillWaterBucket()) {
            return;
        }

        if (this.waterPlaced && !this.readyToPlace && mc.player.getVelocity().y < 0.0D) {
            final double distance = this.distanceToGround(2.5D);
            if (distance > 0.0D && distance <= 1.05D) {
                this.readyToPlace = true;
            }
        }

        if (this.waterPlaced) {
            if (this.placedWaterPos == null && this.retryCooldown == 0) {
                final double distance = this.distanceToGround(2.5D);
                if (distance > 0.0D && distance <= 1.35D) {
                    final int waterSlot = this.findWaterBucketSlot();
                    if (waterSlot != -1) {
                        this.placeWaterBucket(waterSlot, this.lookDownRotation(), false);
                        this.retryCooldown = 2;
                    }
                }
            }
            return;
        }

        if (this.postPlaceCooldown > 0 || this.postActionCooldown > 0) {
            return;
        }

        if (this.accumulatedFall < this.fallDistance.getValue().floatValue()) {
            return;
        }

        final int waterSlot = this.findWaterBucketSlot();
        if (waterSlot == -1 || this.ticksUntilGround() > this.predictTicks.getValue().intValue()) {
            return;
        }

        if (this.solidCheck.getValue() && !this.hasSolidBelow(mc.player.getBlockPos())) {
            return;
        }

        final Vec2f rotation = this.lookDownRotation();
        final BlockHitResult hit = this.raycastSolid(rotation, 5.0D);
        if (hit.getType() == HitResult.Type.MISS) {
            return;
        }

        this.placeWaterBucket(waterSlot, rotation, true);
    }

    @Subscribe
    public void onMoveInput(final MoveInputEvent event) {
        if (this.postActionCooldown > 0 || this.recoveryActive) {
            event.setSneak(false);
        }
    }

    private void updateFallState() {
        if (mc.player.isOnGround()
                || mc.player.isTouchingWater()
                || mc.player.isInLava()
                || mc.player.isClimbing()) {
            this.accumulatedFall = 0.0F;
        } else {
            final double deltaY = mc.player.getY() - this.lastY;
            if (deltaY < 0.0D) {
                this.accumulatedFall -= (float) deltaY;
            }
        }
        this.lastY = mc.player.getY();
    }

    private void tickCooldowns() {
        if (this.postPlaceCooldown > 0) {
            this.postPlaceCooldown--;
        }
        if (this.postActionCooldown > 0) {
            this.postActionCooldown--;
        }
        if (this.retryCooldown > 0) {
            this.retryCooldown--;
        }
    }

    private void restoreSlotIfNeeded() {
        if (this.restoreSlot == -1) {
            return;
        }
        SlotHelper.setCurrentItem(this.restoreSlot);
        this.restoreSlot = -1;
    }

    private boolean tryFillWaterBucket() {
        if (this.waterPlaced
                || this.recoveryActive
                || this.placedWaterPos != null
                || this.postPlaceCooldown > 0
                || this.postActionCooldown > 0
                || this.accumulatedFall > 0.5F
                || this.findWaterBucketSlot() != -1) {
            return false;
        }

        final int emptySlot = this.findEmptyBucketSlot();
        if (emptySlot == -1) {
            return false;
        }

        final BlockPos waterPos = this.findNearestWaterSource();
        if (waterPos == null) {
            return false;
        }

        final Vec2f rotation = this.getLookRotationTo(waterPos);
        final BlockHitResult hit = this.raycastFluid(rotation, 4.5D);
        if (hit.getType() == HitResult.Type.MISS || !hit.getBlockPos().equals(waterPos)) {
            return false;
        }

        this.saveAndSwitch(emptySlot);
        this.useBlock(hit);
        this.postActionCooldown = 8;
        this.postPlaceCooldown = Math.max(this.postPlaceCooldown, 1);
        return true;
    }

    private void handleRecovery() {
        if (this.recoveryDelay > 0) {
            this.recoveryDelay--;
            return;
        }

        if (this.recoveryTriesLeft-- <= 0) {
            this.clearRecovery();
            return;
        }

        if (this.recoverySlot == -1) {
            this.recoverySlot = this.findEmptyBucketSlot();
            if (this.recoverySlot == -1) {
                this.clearRecovery();
                return;
            }
        }

        final ItemStack stack = mc.player.getInventory().getStack(this.recoverySlot);
        if (stack.isOf(Items.WATER_BUCKET)) {
            this.clearRecovery();
            this.postPlaceCooldown = Math.max(this.postPlaceCooldown, 1);
            return;
        }

        if (this.placedWaterPos == null || !this.isWaterSource(this.placedWaterPos)) {
            this.clearRecovery();
            return;
        }

        final Vec2f rotation = this.getLookRotationTo(this.placedWaterPos);
        final BlockHitResult hit = this.raycastFluid(rotation, 4.5D);
        if (hit.getType() == HitResult.Type.MISS || !hit.getBlockPos().equals(this.placedWaterPos)) {
            this.clearRecovery();
            return;
        }

        this.saveAndSwitch(this.recoverySlot);
        this.useBlock(hit);
    }

    private void placeWaterBucket(final int slot, final Vec2f rotation, final boolean markPlaced) {
        final BlockHitResult hit = this.raycastSolid(rotation, 4.5D);
        if (hit.getType() == HitResult.Type.MISS) {
            return;
        }

        this.placedWaterPos = hit.getBlockPos().offset(hit.getSide());
        this.saveAndSwitch(slot);
        this.useBlock(hit);

        if (markPlaced) {
            this.waterPlaced = true;
        }

        this.recoveryActive = this.recovery.getValue() && this.placedWaterPos != null;
        this.recoveryDelay = 3;
        this.recoveryTriesLeft = this.recoveryActive ? 2 : 0;
        this.recoverySlot = -1;
        this.retryCooldown = 2;
    }

    private int ticksUntilGround() {
        if (mc.player.getVelocity().y >= 0.0D) {
            return 999;
        }

        final double distance = this.distanceToGround(30.0D);
        if (distance == Double.POSITIVE_INFINITY) {
            return 999;
        }

        double simulatedDrop = 0.0D;
        double simulatedVelocity = mc.player.getVelocity().y;
        for (int i = 1; i <= 20; i++) {
            simulatedDrop += simulatedVelocity;
            simulatedVelocity = (simulatedVelocity - 0.08D) * 0.98D;
            if (Math.abs(simulatedDrop) >= distance) {
                return i;
            }
        }
        return 999;
    }

    private double distanceToGround(final double maxDistance) {
        final Vec3d start = new Vec3d(mc.player.getX(), mc.player.getBoundingBox().minY, mc.player.getZ());
        final Vec3d end = start.add(0.0D, -maxDistance, 0.0D);
        final BlockHitResult hit = mc.world.raycast(new RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                mc.player
        ));

        if (hit.getType() == HitResult.Type.MISS) {
            return Double.POSITIVE_INFINITY;
        }
        return start.y - hit.getPos().y;
    }

    private BlockPos findNearestWaterSource() {
        final BlockPos playerPos = mc.player.getBlockPos();
        BlockPos closest = null;
        double closestDistance = Double.POSITIVE_INFINITY;

        for (int y = -1; y <= 1; y++) {
            for (int x = -4; x <= 4; x++) {
                for (int z = -4; z <= 4; z++) {
                    final BlockPos candidate = playerPos.add(x, y, z);
                    if (!this.isWaterSource(candidate)) {
                        continue;
                    }

                    final double distance = mc.player.squaredDistanceTo(candidate.getX() + 0.5D, candidate.getY() + 0.5D, candidate.getZ() + 0.5D);
                    if (distance >= closestDistance) {
                        continue;
                    }

                    final Vec2f rotation = this.getLookRotationTo(candidate);
                    final BlockHitResult hit = this.raycastFluid(rotation, 4.5D);
                    if (hit.getType() == HitResult.Type.MISS || !hit.getBlockPos().equals(candidate)) {
                        continue;
                    }

                    closest = candidate.toImmutable();
                    closestDistance = distance;
                }
            }
        }

        return closest;
    }

    private boolean hasSolidBelow(final BlockPos pos) {
        return this.isSolidNonInteractive(pos.down()) || this.isSolidNonInteractive(pos.down(2));
    }

    private boolean isSolidNonInteractive(final BlockPos pos) {
        final BlockState state = mc.world.getBlockState(pos);
        return !state.getCollisionShape(mc.world, pos).isEmpty()
                && state.createScreenHandlerFactory(mc.world, pos) == null;
    }

    private BlockHitResult raycastSolid(final Vec2f rotation, final double range) {
        final Vec3d start = mc.player.getEyePos();
        final Vec3d direction = RotationUtility.getRotationVector(rotation.y, rotation.x);
        final Vec3d end = start.add(direction.multiply(range));
        return mc.world.raycast(new RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                mc.player
        ));
    }

    private BlockHitResult raycastFluid(final Vec2f rotation, final double range) {
        final Vec3d start = mc.player.getEyePos();
        final Vec3d direction = RotationUtility.getRotationVector(rotation.y, rotation.x);
        final Vec3d end = start.add(direction.multiply(range));
        return mc.world.raycast(new RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.ANY,
                mc.player
        ));
    }

    private boolean isWaterSource(final BlockPos pos) {
        return !mc.world.getFluidState(pos).isEmpty() && mc.world.getFluidState(pos).isStill();
    }

    private Vec2f lookDownRotation() {
        return this.normalizeRotation(new Vec2f(mc.player.getYaw(), 89.64F));
    }

    private Vec2f getLookRotationTo(final BlockPos pos) {
        return this.normalizeRotation(RotationUtility.getRotationFromPosition(new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D)));
    }

    private Vec2f normalizeRotation(final Vec2f rotation) {
        final float currentYaw = mc.player.getYaw();
        final float yaw = currentYaw + MathHelper.wrapDegrees(rotation.x - currentYaw);
        final float pitch = MathHelper.clamp(rotation.y, -89.0F, 89.0F);
        return RotationUtility.patchConstantRotation(new Vec2f(yaw, pitch), new Vec2f(currentYaw, mc.player.getPitch()));
    }

    private void requestRotation(final Vec2f rotation) {
        RotationHelper.getHandler().rotate(rotation, InstantRotationModel.INSTANCE);
    }

    private void saveAndSwitch(final int targetSlot) {
        if (this.restoreSlot == -1) {
            this.restoreSlot = mc.player.getInventory().getSelectedSlot();
        }
        SlotHelper.setCurrentItem(targetSlot);
    }

    private void useBlock(final BlockHitResult hitResult) {
        final ActionResult result = mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
        if (!result.isAccepted()) {
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        }
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private int findWaterBucketSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).isOf(Items.WATER_BUCKET)) {
                return i;
            }
        }
        return -1;
    }

    private int findEmptyBucketSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).isOf(Items.BUCKET)) {
                return i;
            }
        }
        return -1;
    }

    private void clearRecovery() {
        this.recoveryActive = false;
        this.recoveryDelay = 0;
        this.recoveryTriesLeft = 0;
        this.recoverySlot = -1;
        this.placedWaterPos = null;
    }

    private void resetState() {
        this.accumulatedFall = 0.0F;
        this.lastY = mc.player == null ? 0.0D : mc.player.getY();
        this.restoreSlot = -1;
        this.waterPlaced = false;
        this.readyToPlace = false;
        this.clearRecovery();
        this.postPlaceCooldown = 0;
        this.postActionCooldown = 0;
        this.retryCooldown = 0;
    }

    @Override
    protected void onEnable() {
        this.resetState();
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        this.resetState();
        super.onDisable();
    }
}
