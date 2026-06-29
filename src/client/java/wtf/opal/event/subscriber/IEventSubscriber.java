package wtf.opal.event.subscriber;

public interface IEventSubscriber {
    default boolean isHandlingEvents() {
        return true;
    }
}
