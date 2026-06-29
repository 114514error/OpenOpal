package wtf.opal.client.feature.module.impl.utility;

import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import wtf.opal.client.OpalClient;
import wtf.opal.client.feature.module.Module;
import wtf.opal.client.feature.module.ModuleCategory;
import wtf.opal.client.feature.module.property.impl.number.NumberProperty;
import wtf.opal.event.impl.game.JoinWorldEvent;
import wtf.opal.event.impl.game.PreGameTickEvent;
import wtf.opal.event.impl.game.packet.ReceivePacketEvent;
import wtf.opal.event.subscriber.Subscribe;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static wtf.opal.client.Constants.mc;

public final class AntiBotsModule extends Module {

    private static final Map<UUID, String> UUID_DISPLAY_NAMES = new ConcurrentHashMap<>();
    private static final Map<Integer, String> ENTITY_ID_DISPLAY_NAMES = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> PENDING_UUIDS = new ConcurrentHashMap<>();
    private static final Set<Integer> BOT_IDS = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final Map<UUID, Long> RESPAWN_TIMES = new ConcurrentHashMap<>();

    private final NumberProperty respawnTime = new NumberProperty("Respawn Time", 2500.0D, 0.0D, 10000.0D, 100.0D);
    private final NumberProperty minTicks = new NumberProperty("Min Ticks", 20.0D, 0.0D, 100.0D, 1.0D);

    public AntiBotsModule() {
        super("AntiBot", "Prevents bots from being targeted.", ModuleCategory.COMBAT);
        this.addProperties(this.respawnTime, this.minTicks);
    }

    public static boolean shouldFilter(final LivingEntity entity) {
        return isBot(entity) || isBedWarsBot(entity);
    }

    public static boolean isBedWarsBot(final Entity entity) {
        final AntiBotsModule module = getModule();
        if (module == null || !module.isEnabled()) {
            return false;
        }

        if (module.respawnTime.getValue().floatValue() >= 1.0F && RESPAWN_TIMES.containsKey(entity.getUuid())) {
            return (float) (System.currentTimeMillis() - RESPAWN_TIMES.get(entity.getUuid())) < module.respawnTime.getValue().floatValue();
        }

        return false;
    }

    public static boolean isBot(final Entity entity) {
        final AntiBotsModule module = getModule();
        if (module == null || !module.isEnabled() || entity == null) {
            return false;
        }

        boolean inTab = false;
        if (mc.getNetworkHandler() != null) {
            final PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(entity.getUuid());
            inTab = entry != null;
        }

        if (!inTab) {
            return true;
        }

        if (BOT_IDS.contains(entity.getId())) {
            return true;
        }

        if (entity.age < module.minTicks.getValue().intValue()) {
            if (mc.player != null && !entity.isOnGround() && Math.abs(entity.getVelocity().y) < 0.1D && entity.getY() > mc.player.getY() + 1.0D) {
                return true;
            }

            if (entity.isInvisible()) {
                return true;
            }
        }

        if (entity.getType() == EntityType.PLAYER) {
            final String name = entity.getName().getString();
            if (name.isEmpty() || name.contains("CIT-") || name.length() > 16 || name.contains(" ")) {
                return true;
            }

            final int version = entity.getUuid().version();
            return version != 4 && version != 1;
        }

        return false;
    }

    public static String getDisplayNameForUuid(final UUID uuid) {
        return UUID_DISPLAY_NAMES.get(uuid);
    }

    @Subscribe
    public void onReceivePacket(final ReceivePacketEvent event) {
        if (mc.world == null) {
            return;
        }

        final Packet<?> packet = event.getPacket();

        if (packet instanceof PlayerListS2CPacket infoPacket) {
            if (infoPacket.getActions().contains(PlayerListS2CPacket.Action.ADD_PLAYER)) {
                for (final PlayerListS2CPacket.Entry entry : infoPacket.getEntries()) {
                    RESPAWN_TIMES.put(entry.profileId(), System.currentTimeMillis());
                }
            }

            if (infoPacket.getActions().contains(PlayerListS2CPacket.Action.ADD_PLAYER)
                    || infoPacket.getActions().contains(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME)) {
                for (final PlayerListS2CPacket.Entry entry : infoPacket.getEntries()) {
                    if (entry.displayName() != null) {
                        final UUID uuid = entry.profileId();
                        PENDING_UUIDS.put(uuid, System.currentTimeMillis());
                        UUID_DISPLAY_NAMES.put(uuid, entry.displayName().getString());
                    }
                }
            }
            return;
        }

        if (packet instanceof EntityAnimationS2CPacket animationPacket) {
            final Entity entity = mc.world.getEntityById(animationPacket.getEntityId());
            if (entity != null && animationPacket.getAnimationId() == 0) {
                RESPAWN_TIMES.remove(entity.getUuid());
            }
            return;
        }

        if (packet instanceof EntitySpawnS2CPacket spawnPacket && spawnPacket.getEntityType() == EntityType.PLAYER) {
            if (PENDING_UUIDS.containsKey(spawnPacket.getUuid())) {
                final String displayName = UUID_DISPLAY_NAMES.get(spawnPacket.getUuid());
                ENTITY_ID_DISPLAY_NAMES.put(spawnPacket.getEntityId(), displayName);
                PENDING_UUIDS.remove(spawnPacket.getUuid());
                BOT_IDS.add(spawnPacket.getEntityId());
            }
            return;
        }

        if (packet instanceof EntitiesDestroyS2CPacket destroyPacket) {
            for (final int entityId : destroyPacket.getEntityIds()) {
                BOT_IDS.remove(entityId);
                ENTITY_ID_DISPLAY_NAMES.remove(entityId);
            }
        }
    }

    @Subscribe
    public void onWorldLoad(final JoinWorldEvent event) {
        clear();
    }

    @Subscribe
    public void onPreTick(final PreGameTickEvent event) {
        for (final Map.Entry<UUID, Long> entry : PENDING_UUIDS.entrySet()) {
            if (System.currentTimeMillis() - entry.getValue() > 500L) {
                PENDING_UUIDS.remove(entry.getKey());
                UUID_DISPLAY_NAMES.remove(entry.getKey());
            }
        }
    }

    @Override
    protected void onDisable() {
        clear();
        super.onDisable();
    }

    private static void clear() {
        UUID_DISPLAY_NAMES.clear();
        ENTITY_ID_DISPLAY_NAMES.clear();
        BOT_IDS.clear();
        PENDING_UUIDS.clear();
        RESPAWN_TIMES.clear();
    }

    private static AntiBotsModule getModule() {
        final OpalClient client = OpalClient.getInstance();
        if (client.getModuleRepository() == null) {
            return null;
        }
        return client.getModuleRepository().getModule(AntiBotsModule.class);
    }
}
