package wtf.opal.client.feature.module.property.impl.bool;

import wtf.opal.client.feature.module.property.Property;
import wtf.opal.client.feature.module.property.impl.mode.ModuleMode;
import wtf.opal.client.screen.click.dropdown.panel.property.PropertyPanel;
import wtf.opal.client.screen.click.dropdown.panel.property.impl.BooleanPropertyComponent;

public final class BooleanProperty extends Property<Boolean> {

    public BooleanProperty(final String name, final boolean value) {
        super(name);
        setValue(value);
    }

    public BooleanProperty(final String name, final ModuleMode<?> parent, final boolean value) {
        super(name, parent);
        setValue(value);
    }

    public void toggle() {
        setValue(!getValue());
    }

    @Override
    public Boolean getValue() {
        return super.getValue() && !this.isHidden();
    }

    @Override
    public PropertyPanel<?> createClickGUIComponent() {
        return new BooleanPropertyComponent(this);
    }

    @Override
    public void applyValue(Object propertyValue) {
        setValue(Boolean.parseBoolean(String.valueOf(propertyValue)));
    }
}
