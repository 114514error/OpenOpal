package wtf.opal.utility.misc.time;

import wtf.opal.client.feature.helper.IHelper;
import wtf.opal.event.EventDispatcher;
import wtf.opal.event.impl.game.PreGameTickEvent;
import wtf.opal.event.subscriber.Subscribe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class Scheduler implements IHelper {

    private static final Map<Runnable, AtomicInteger> TASKS = new ConcurrentHashMap<>();

    @Subscribe
    public void onPreGameTick(final PreGameTickEvent event) {
        TASKS.forEach((function, remainingTicks) -> {
            if (remainingTicks.getAndDecrement() < 1) {
                TASKS.remove(function);
                function.run();
            }
        });
    }

    public static void addTask(final Runnable function, final int tickDelay) {
        TASKS.put(function, new AtomicInteger(tickDelay));
    }

    static {
        EventDispatcher.subscribe(new Scheduler());
    }

}
