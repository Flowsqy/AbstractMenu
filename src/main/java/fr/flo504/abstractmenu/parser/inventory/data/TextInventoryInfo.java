package fr.flo504.abstractmenu.parser.inventory.data;

import java.util.Objects;

public class TextInventoryInfo extends BaseInventoryInfo {

    private final String placeholder;

    public TextInventoryInfo(String title, String placeholder) {
        super(title);
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TextInventoryInfo that = (TextInventoryInfo) o;
        return Objects.equals(placeholder, that.placeholder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), placeholder);
    }

    @Override
    public String toString() {
        return "TextInventoryInfo{" +
                "placeholder='" + placeholder + '\'' +
                "} " + super.toString();
    }
}
