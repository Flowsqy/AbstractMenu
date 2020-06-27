package fr.flo504.abstractmenu.parser.item;

import fr.flo504.abstractmenu.item.InventorySlot;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.Objects;

public interface InventorySlotParser {

    ItemData parse(ConfigurationSection section, Map<String, InventorySlotParser> parserData);

    final class ItemData {

        private final String id;
        private final InventorySlot item;

        public ItemData(String id, InventorySlot item) {
            this.id = id;
            this.item = item;
        }

        public String getId() {
            return id;
        }

        public InventorySlot getItem() {
            return item;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemData that = (ItemData) o;
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
