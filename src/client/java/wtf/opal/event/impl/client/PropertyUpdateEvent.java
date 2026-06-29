package wtf.opal.event.impl.client;

import wtf.opal.client.feature.module.property.Property;

public record PropertyUpdateEvent(Property<?> property) {
}
