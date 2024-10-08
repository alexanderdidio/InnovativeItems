package me.boboballoon.innovativeitems.listeners;

import me.boboballoon.innovativeitems.ui.base.InnovativeElement;
import me.boboballoon.innovativeitems.ui.base.InnovativeView;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/**
 * A class that contains all listeners for custom UIs
 */
public final class UIViewListeners implements Listener {
    /**
     * Listener used to check a player interacts with a custom ui
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof InnovativeView) {
            event.setCancelled(true);
        }
    }

    /**
     * Listener used to check a player interacts with a custom ui
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || !(event.getInventory().getHolder() instanceof InnovativeView)) {
            return;
        }

        InnovativeView view = (InnovativeView) event.getInventory().getHolder();

        if (!event.getClickedInventory().getHolder().equals(view)) {
            return;
        }

        event.setCancelled(true);

        InnovativeElement element = view.getElement(event.getSlot());

        element.getClickAction().accept((Player) event.getWhoClicked(), event.getClick()); //can assume HumanEntity is Player
    }
}