package wtf.opal.client.feature.helper.impl.render;

import net.minecraft.client.render.Frustum;
import org.jetbrains.annotations.Nullable;
import wtf.opal.event.EventDispatcher;
import wtf.opal.event.impl.game.JoinWorldEvent;
import wtf.opal.event.subscriber.IEventSubscriber;
import wtf.opal.event.subscriber.Subscribe;

/**
 * @author Trol
 * @since 2.0-beta.11
 **/
public class FrustumHelper implements IEventSubscriber {

    private static @Nullable Frustum frustum;

    private static FrustumHelper instance;


    public static void setFrustum(@Nullable final Frustum frustum) {
        FrustumHelper.frustum = frustum;
    }

    public static Frustum get() {
        return frustum;
    }


    @Subscribe
    public void onDisconnectWorld(final JoinWorldEvent event) {
        FrustumHelper.setFrustum(null);
    }

    static {
        instance = new FrustumHelper();
        EventDispatcher.subscribe(instance);
    }
}
