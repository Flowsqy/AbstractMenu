package fr.flo504.abstractmenu.parser.inventory.data;

import java.util.Objects;

public class BaseInventoryInfo {

    private final String title;

    public BaseInventoryInfo(String title) {
        Objects.requireNonNull(title);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseInventoryInfo that = (BaseInventoryInfo) o;
        return title.equals(that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }

    @Override
    public String toString() {
        return "BaseInventoryInfo{" +
                "title='" + title + '\'' +
                '}';
    }
}
