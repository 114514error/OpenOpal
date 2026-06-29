package wtf.opal.utility.data;

import wtf.opal.client.binding.IBindable;

import java.util.Date;

public final class Config implements IBindable {

    private final String name;

    private String description;
    private boolean pinned;
    private Date updatedAt;

    public Config(final String name) {
        this.name = name;
    }

    public Config(final String name, final String description, final boolean pinned, final Date updatedAt) {
        this.name = name;
        this.description = description;
        this.pinned = pinned;
        this.updatedAt = updatedAt;
    }

    @Override
    public void onBindingInteraction() {
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public boolean isPinned() {
        return pinned;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
