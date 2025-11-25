package me.nickotato.simplePolls.guis

import me.nickotato.simplePolls.managers.GuiManager
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class ManagePollsGui: Gui(Component.text("§6Managing Polls"),45) {
    init {
        val create = ItemStack(Material.WRITABLE_BOOK)
        val createMeta = create.itemMeta
        createMeta.displayName(Component.text("§2Create Poll"))
        create.itemMeta = createMeta
        setItem(41, create)
    }

    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true
        val player = event.whoClicked
        if (player !is Player) return
        val slot = event.slot

        when (slot) {
            41 -> {
                GuiManager.open(CreatePollGui(), player)
            }
        }
    }
}