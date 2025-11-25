package me.nickotato.simplePolls.guis

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class ManagePollsGui: Gui(Component.text("§6Managing Polls"),45) {
    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true
        val player = event.whoClicked
        if (player !is Player) return
    }
}