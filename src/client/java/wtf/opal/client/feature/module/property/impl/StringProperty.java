package wtf.opal.client.feature.module.property.impl;

import wtf.opal.client.feature.module.property.Property;
import wtf.opal.client.feature.module.property.impl.mode.ModuleMode;
import wtf.opal.client.screen.click.dropdown.panel.property.PropertyPanel;
import wtf.opal.client.screen.click.dropdown.panel.property.impl.StringPropertyComponent;

public final class StringProperty extends Property<String> {

    public StringProperty(final String name, final String value) {
        super(name);
        setValue(value);
    }

    public StringProperty(final String name, final ModuleMode<?> parent, final String value) {
        super(name, parent);
        setValue(value);
    }

    @Override
    public void applyValue(Object propertyValue) {
        setValue(String.valueOf(propertyValue));
    }

    @Override
    public PropertyPanel<?> createClickGUIComponent() {
        return new StringPropertyComponent(this);
    }
}
