package me.nickotato.simplePolls.guis

import me.nickotato.simplePolls.managers.GuiManager
import me.nickotato.simplePolls.model.Poll
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.UUID

class PollResultsGui(poll: Poll)
    : Gui(Component.text("§8Poll Results"), 54)  {
    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true

        val player = event.whoClicked
        if (player !is Player) return

        if (event.slot == 49) {
            GuiManager.open(ViewExpiredPollsGui(), player)
        }
    }

    init {
        for (i in 0 until 54) {
            setItem(i, ItemStack(Material.GRAY_STAINED_GLASS_PANE))
        }

        val grouped = mutableMapOf<String, MutableList<String>>()

        for ((uuid, choice) in poll.votes) {
            val name = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).name ?: uuid
            grouped.computeIfAbsent(choice) { mutableListOf() }.add(name)
        }

        var slot = 0

        for ((option, voters) in grouped) {
            val item = ItemStack(Material.PAPER)
            val meta = item.itemMeta

            meta.displayName(Component.text("§6$option"))

            val lore = mutableListOf<Component>()
            lore.add(Component.text("§7Votes: §a${voters.size}"))
            lore.add(Component.text("§8"))

            for (name in voters.take(10)) {
                lore.add(Component.text("§7- §f$name"))
            }

            if (voters.size > 10) {
                lore.add(Component.text("§7... and ${voters.size - 10} more"))
            }

            meta.lore(lore)
            item.itemMeta = meta

            setItem(slot++, item)

            val back = ItemStack(Material.ARROW)
            val backMeta = back.itemMeta

            backMeta.displayName(Component.text("§cBack"))

            backMeta.lore(listOf(
                Component.text("§7Return to expired polls")
            ))

            back.itemMeta = backMeta

            setItem(49, back)
        }
    }
}