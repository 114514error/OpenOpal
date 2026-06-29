package wtf.opal.client.feature.module.impl.world.scaffold;

import wtf.opal.client.feature.helper.impl.LocalDataWatch;
import wtf.opal.client.feature.helper.impl.player.rotation.RotationProperty;
import wtf.opal.client.feature.helper.impl.player.rotation.model.IRotationModel;
import wtf.opal.client.feature.helper.impl.player.rotation.model.impl.InstantRotationModel;
import wtf.opal.client.feature.helper.impl.player.swing.CPSProperty;
import wtf.opal.client.feature.helper.impl.server.impl.HypixelServer;
import wtf.opal.client.feature.module.impl.world.scaffold.mode.AntiGamingChairScaffold;
import wtf.opal.client.feature.module.impl.world.scaffold.mode.BloxdScaffold;
import wtf.opal.client.feature.module.impl.world.scaffold.mode.TellyScaffold;
import wtf.opal.client.feature.module.impl.world.scaffold.mode.VanillaScaffold;
import wtf.opal.client.feature.module.impl.world.scaffold.mode.watchdog.WatchdogScaffold;
import wtf.opal.client.feature.module.property.impl.GroupProperty;
import wtf.opal.client.feature.module.property.impl.bool.BooleanProperty;
import wtf.opal.client.feature.module.property.impl.bool.MultipleBooleanProperty;
import wtf.opal.client.feature.module.property.impl.mode.ModeProperty;
import wtf.opal.client.feature.module.property.impl.number.NumberProperty;

public final class ScaffoldSettings {

    private final RotationProperty rotationProperty;
    private final BooleanProperty movementIntelligence;
    private final BooleanProperty movementSnapping, diagonalMovement;
    private final NumberProperty movementSteps;

    private final CPSProperty simulationCps;

    private final BooleanProperty tower;
    private final ModeProperty<Mode> mode;

    private final ModeProperty<SwitchMode> switchMode;
    private final ModeProperty<SwingMode> swingMode;

    private final BooleanProperty snapRotations;
    private final BooleanProperty sameY, autoJump;
    private final BooleanProperty tellyHeypixel;

    private final BooleanProperty blockOverlay;

    private final BooleanProperty overrideRaycast;
    private final BooleanProperty interactBeforePlace;

    private final MultipleBooleanProperty hypixelAddons;

    public ScaffoldSettings(final ScaffoldModule module) {
        this.movementIntelligence = new BooleanProperty("Enabled", false);
        this.diagonalMovement = new BooleanProperty("Diagonal movement", false);
        this.movementSnapping = new BooleanProperty("Snap movement", true);
        this.movementSteps = new NumberProperty("Steps", 3, 1, 3, 1).hideIf(() -> !this.isMovementSnapping());
        this.rotationProperty = new RotationProperty(InstantRotationModel.INSTANCE,
                new GroupProperty("Movement intelligence", this.movementIntelligence, this.diagonalMovement, this.movementSnapping, this.movementSteps));

        this.simulationCps = new CPSProperty(module, "Interact CPS", false);

        this.mode = new ModeProperty<>("Mode", module, Mode.WATCHDOG);
        this.tower = new BooleanProperty("Tower", false);

        this.switchMode = new ModeProperty<>("Switch mode", module, SwitchMode.HOTBAR);
        this.swingMode = new ModeProperty<>("Swing mode", SwingMode.CLIENT);

        this.snapRotations = new BooleanProperty("Snap rotations", false);
        this.sameY = new BooleanProperty("Same Y", true);
        this.autoJump = new BooleanProperty("Auto jump", true).hideIf(() -> !this.isSameYEnabled());
        this.tellyHeypixel = new BooleanProperty("Heypixel", false).hideIf(() -> !this.isTellyMode());

        this.blockOverlay = new BooleanProperty("Block overlay", true);

        this.overrideRaycast = new BooleanProperty("Override raycast", true);
        this.interactBeforePlace = new BooleanProperty("Interact before place", false);

        this.hypixelAddons = new MultipleBooleanProperty("Hypixel addons",
                new BooleanProperty("Boost", true)).hideIf(() -> !(LocalDataWatch.get().getKnownServerManager().getCurrentServer() instanceof HypixelServer));

        module.addModuleModes(mode, new VanillaScaffold(module), new WatchdogScaffold(module), new AntiGamingChairScaffold(module), new BloxdScaffold(module), new TellyScaffold(module));
        module.addProperties(rotationProperty.get(), mode, switchMode, swingMode, tower, snapRotations, overrideRaycast,
                interactBeforePlace, sameY, autoJump, tellyHeypixel, blockOverlay, hypixelAddons);
    }

    private boolean isTellyMode() {
        return this.mode.getValue() == Mode.TELLY;
    }

    public boolean isTellyHeypixel() {
        return this.tellyHeypixel.getValue();
    }

    public CPSProperty getSimulationCps() {
        return simulationCps;
    }

    public boolean isDiagonalMovement() {
        return this.diagonalMovement.getValue();
    }

    public boolean isMovementSnapping() {
        return this.movementSnapping.getValue();
    }

    public boolean isMovementIntelligence() {
        return this.movementIntelligence.getValue();
    }

    public int getMovementIntelligenceSteps() {
        return this.movementSteps.getValue().intValue();
    }

    public boolean isAutoJump() {
        return this.autoJump.getValue();
    }

    public ModeProperty<Mode> getMode() {
        return mode;
    }

    public boolean isTowerEnabled() {
        return tower.getValue();
    }

    public boolean isBlockOverlayEnabled() {
        return blockOverlay.getValue();
    }

    public ModeProperty<SwitchMode> getSwitchMode() {
        return switchMode;
    }

    public ModeProperty<SwingMode> getSwingMode() {
        return swingMode;
    }

    public boolean isSnapRotationsEnabled() {
        return snapRotations.getValue();
    }

    public boolean isSameYEnabled() {
        return sameY.getValue();
    }

    public boolean isOverrideRaycast() {
        return overrideRaycast.getValue();
    }

    public boolean isInteractBeforePlace() {
        return interactBeforePlace.getValue();
    }

    public MultipleBooleanProperty getHypixelAddons() {
        return hypixelAddons;
    }

    public IRotationModel createRotationModel() {
        return rotationProperty.createModel();
    }

    public enum SwitchMode {
        NORMAL("Normal"),
        HOTBAR("Hotbar"),
        FULL("Full");

        private final String name;

        SwitchMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum SwingMode {
        CLIENT("Client"),
        SERVER("Server");

        private final String name;

        SwingMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum Mode {
        VANILLA("Vanilla"),
        ANTI_GAMING_CHAIR("Anti Gaming Chair"),
        WATCHDOG("Watchdog"),
        BLOXD("Bloxd"),
        TELLY("Telly");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
