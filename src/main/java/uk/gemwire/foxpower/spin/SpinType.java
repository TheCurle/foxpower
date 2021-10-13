package uk.gemwire.foxpower.spin;

import net.minecraft.util.StringRepresentable;

/**
 * A String-Serializable enum for the {@link uk.gemwire.foxpower.generator.SpinBlock}'s properties.
 *
 * @author Curle
 */
public enum SpinType implements StringRepresentable {
    Tumble("tumble"),
    Rotate("rotate"),
    Drill("drill"),
    Flail("flail");

    private final String name;

    SpinType(String type) {
        name = type;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
