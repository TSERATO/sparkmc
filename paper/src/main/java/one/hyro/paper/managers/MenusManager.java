package one.hyro.paper.managers;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import one.hyro.paper.HyrosPaper;
import one.hyro.paper.enums.PersistentDataKeys;
import one.hyro.paper.utilities.ConfigParser;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MenusManager {
    private static final Map<String, FileConfiguration> menus = new HashMap<>();
    private static final NamespacedKey key = PersistentDataKeys.CUSTOM_MENU.getKey();

    public static void loadMenus() {
        File dataFolder = new File(HyrosPaper.getInstance().getDataFolder(), "menus");
        File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            menus.put(file.getName(), config);
            Bukkit.getLogger().info("Loaded menu: " + file.getName());
        }
    }

    public static void openMenu(Player player, String menuName) {
        FileConfiguration config = menus.get(menuName + ".yml");

        if (config == null) {
            Bukkit.getLogger().warning("Invalid menu name.");
            return;
        }

        Inventory menu = createMenuFromConfig(config, player);
        player.getPersistentDataContainer().set(key, PersistentDataType.STRING, menuName);
        player.openInventory(menu);
    }

    private static Inventory createMenuFromConfig(FileConfiguration config, Player player) {
        Inventory menu = Bukkit.createInventory(null,
                config.getInt("slots", 27),
                LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("title", "Menu"))
        );

        Map<ItemStack, Integer> items = ConfigParser.parseItems(config, "items", player);
        for (Map.Entry<ItemStack, Integer> entry : items.entrySet()) {
            ItemStack item = entry.getKey();
            int slot = entry.getValue();
            menu.setItem(slot, item);
        }

        return menu;
    }
}