package me.nickotato.simplePolls.guis

import me.nickotato.simplePolls.SimplePolls
import me.nickotato.simplePolls.listeners.PollChatListener
import me.nickotato.simplePolls.managers.GuiManager
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class CreatePollGui:Gui(Component.text("§2Creating Poll"),3 * 9) {
    private val gui = this
    private var name = "Undefined"
    private val options = mutableListOf<String>()
    private var duration: Long = 0

    init {
        updateNameItem()
        updateDurationItem()
        updateOptionsItem()
    }

    private fun updateNameItem() {
        val nameItem = ItemStack(Material.NAME_TAG, 1)
        val meta = nameItem.itemMeta
        meta.displayName(Component.text("§6Set Item Name"))
        meta.lore(listOf(Component.text("§7Current Name: "), Component.text("§5$name")))
        nameItem.itemMeta = meta
        setItem(11, nameItem)
    }

    private fun updateDurationItem() {
        val durationItem = ItemStack(Material.CLOCK, 1)
        val meta = durationItem.itemMeta
        meta.displayName(Component.text("§6Set Duration (in hours)"))
        meta.lore(listOf(Component.text("§7Current Duration: "), Component.text("§5$duration")))
        durationItem.itemMeta = meta
        setItem(15, durationItem)
    }

    private fun updateOptionsItem() {
        val optionsItem = ItemStack(Material.PAPER, 1)
        val meta = optionsItem.itemMeta
        meta.displayName(Component.text("§6Add Option"))
        val lore = mutableListOf<Component>()
        for (option in options) {
            lore.add(Component.text("§5$option"))
        }
        meta.lore(lore)
        optionsItem.itemMeta = meta
        setItem(13, optionsItem)
    }

    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true
        val player = event.whoClicked as? Player ?: return
        val slot = event.slot

        when (slot) {
            11 -> {
                player.closeInventory()
                PollChatListener.requestInput(player) { input ->
                    Bukkit.getScheduler().runTask(SimplePolls.instance, Runnable {
                        name = input
                        updateNameItem()
                        GuiManager.open(gui, player)
                    })
                }
            }
            13 -> {
                player.closeInventory()
                PollChatListener.requestInput(player) { input ->
                    Bukkit.getScheduler().runTask(SimplePolls.instance, Runnable {
                        options.add(input)
                        updateOptionsItem()
                        GuiManager.open(gui, player)
                    })
                }
            }
            15 -> {
                player.closeInventory()
                PollChatListener.requestInput(player) { input ->
                    Bukkit.getScheduler().runTask(SimplePolls.instance, Runnable {
                        val parsed: Long? = input.toLongOrNull()
                        if (parsed == null || parsed <= 0) {
                            player.sendMessage("§cInvalid number! Please enter a positive number.")
                            return@Runnable
                        }

                        duration = parsed
                        updateDurationItem()
                        GuiManager.open(gui, player)
                    })
                }

            }
        }
    }

}