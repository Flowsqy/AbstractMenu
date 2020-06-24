package fr.flo504.abstractmenu.parser.item;

import fr.flo504.abstractmenu.item.ItemStackGetter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.Objects;

public interface InventorySlotParser {

    ItemStackData parse(ConfigurationSection section, Map<String, InventorySlotParser> parserData);

    final class ItemStackData {

        private final String id;
        private final ItemStackGetter item;

        public ItemStackData(String id, ItemStackGetter item) {
            this.id = id;
            this.item = item;
        }

        public String getId() {
            return id;
        }

        public ItemStackGetter getItem() {
            return item;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemStackData that = (ItemStackData) o;
            return Objects.equals(id, that.id) &&
                    Objects.equals(item, that.item);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, item);
        }

        @Override
        public String toString() {
            return "ItemStackData{" +
                    "id='" + id + '\'' +
                    ", item=" + item +
                    '}';
        }
    }

}
