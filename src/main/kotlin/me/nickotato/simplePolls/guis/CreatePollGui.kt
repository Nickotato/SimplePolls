package me.nickotato.simplePolls.guis

import me.nickotato.simplePolls.SimplePolls
import me.nickotato.simplePolls.listeners.PollChatListener
import me.nickotato.simplePolls.managers.GuiManager
import me.nickotato.simplePolls.managers.PollsManager
import me.nickotato.simplePolls.utils.DurationParser
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class CreatePollGui:Gui(Component.text("§8Creating Poll"),5 * 9) {
    private val gui = this
    private var name = "Undefined"
    private val options = mutableListOf<String>()
    private var duration: Long = 0
    private var anonymous = true
    private var minimumContinuousUnlockTime: Long = 0

    companion object {
        private const val SLOT_NAME = 10
        private const val SLOT_OPTIONS = 12
        private const val SLOT_DURATION = 14
        private const val SLOT_ANONYMOUS = 16
        private const val SLOT_UNLOCK_TIME = 19

        private const val SLOT_CANCEL = 30
        private const val SLOT_CREATE = 32
    }

    init {
        updateNameItem()
        updateDurationItem()
        updateOptionsItem()
        updateAnonymousItem()
        updateUnlockTimeItem()

        val create = ItemStack(Material.LIME_DYE)
        val createMeta = create.itemMeta
        createMeta.displayName(Component.text("§2Create Poll"))
        create.itemMeta = createMeta
        setItem(SLOT_CREATE, create)

        val cancel = ItemStack(Material.RED_DYE)
        val cancelMeta = cancel.itemMeta
        cancelMeta.displayName(Component.text("§cCancel"))
        cancel.itemMeta = cancelMeta
        setItem(SLOT_CANCEL, cancel)


    }

    private fun updateNameItem() {
        val nameItem = ItemStack(Material.NAME_TAG, 1)
        val meta = nameItem.itemMeta
        meta.displayName(Component.text("§6Poll Question"))
        meta.lore(listOf(Component.text("§7Current Question: "), Component.text("§5$name")))
        nameItem.itemMeta = meta
        setItem(SLOT_NAME, nameItem)
    }

    private fun updateDurationItem() {
        val durationItem = ItemStack(Material.CLOCK, 1)
        val meta = durationItem.itemMeta
        meta.displayName(Component.text("§6Set Duration (e.g., 1d 2h 5m)"))
        val durationText = if (duration <= 0) "Not set" else DurationParser.formatDuration(duration)
        meta.lore(listOf(Component.text("§7Current Duration: "), Component.text("§5$durationText")))
        durationItem.itemMeta = meta
        setItem(SLOT_DURATION, durationItem)
    }

    private fun updateOptionsItem() {
        val optionsItem = ItemStack(Material.PAPER, 1)
        val meta = optionsItem.itemMeta
        meta.displayName(Component.text("§6Add Option"))
        val lore = mutableListOf<Component>()
        for (option in options) {
            lore.add(Component.text("§5● $option"))
        }
        meta.lore(lore)
        optionsItem.itemMeta = meta
        setItem(SLOT_OPTIONS, optionsItem)
    }

    private fun updateAnonymousItem() {
        val material =
            if (anonymous) Material.LIME_DYE
            else Material.GRAY_DYE

        val item = ItemStack(material)
        val meta = item.itemMeta

        meta.displayName(
            Component.text(
                if (anonymous)
                    "§aAnonymous Poll"
                else
                    "§cPublic Poll"
            )
        )

        meta.lore(
            listOf(
                Component.text(
                    if (anonymous)
                        "§7Votes are hidden"
                    else
                        "§7Votes are public"
                )
            )
        )

        item.itemMeta = meta

        setItem(SLOT_ANONYMOUS, item)
    }

    private fun updateUnlockTimeItem() {
        val unlockTimeItem = ItemStack(Material.CLOCK, 1)
        val meta = unlockTimeItem.itemMeta

        meta.displayName(
            Component.text("§6Minimum Continuous Playtime")
        )

        val unlockTimeText =
            if (minimumContinuousUnlockTime <= 0)
                "Not set"
            else
                DurationParser.formatDuration(minimumContinuousUnlockTime)

        meta.lore(
            listOf(
                Component.text("§7Required continuous playtime:"),
                Component.text("§5$unlockTimeText")
            )
        )

        unlockTimeItem.itemMeta = meta

        setItem(SLOT_UNLOCK_TIME, unlockTimeItem)
    }

    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true
        val player = event.whoClicked as? Player ?: return
        val slot = event.slot

        when (slot) {
            SLOT_NAME -> {
                player.closeInventory()
                PollChatListener.requestInput(player, PollChatListener.InputType.NAME) { input ->
                    Bukkit.getScheduler().runTask(SimplePolls.instance, Runnable {
                        name = input
                        updateNameItem()
                        GuiManager.open(gui, player)
                    })
                }
            }
            SLOT_OPTIONS -> {
                player.closeInventory()
                PollChatListener.requestInput(player, PollChatListener.InputType.OPTION) { input ->
                    Bukkit.getScheduler().runTask(SimplePolls.instance, Runnable {
                        options.add(input)
                        updateOptionsItem()
                        GuiManager.open(gui, player)
                    })
                }
            }
            SLOT_DURATION -> {
                player.closeInventory()
                PollChatListener.requestInput(player, PollChatListener.InputType.DURATION) { input ->
                    Bukkit.getScheduler().runTask(SimplePolls.instance, Runnable {
                        val parsed = try {
                            DurationParser.parseDuration(input)
                        } catch (e: IllegalArgumentException) {
                            player.sendMessage("§a${e.message}")
                            return@Runnable
                        }

                        duration = parsed
                        updateDurationItem()
                        GuiManager.open(gui, player)
                    })
                }

            }
            SLOT_ANONYMOUS -> {
                anonymous = !anonymous
                updateAnonymousItem()
            }

            SLOT_UNLOCK_TIME -> {
                player.closeInventory()

                PollChatListener.requestInput(
                    player,
                    PollChatListener.InputType.DURATION
                ) { input ->

                    Bukkit.getScheduler().runTask(SimplePolls.instance, Runnable {

                        val parsed = try {
                            DurationParser.parseDuration(input)
                        } catch (e: IllegalArgumentException) {
                            player.sendMessage("§c${e.message}")
                            return@Runnable
                        }

                        minimumContinuousUnlockTime = parsed

                        updateUnlockTimeItem()
                        GuiManager.open(gui, player)
                    })
                }
            }

            SLOT_CANCEL -> {
                player.closeInventory()
                player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 1f, 1f)
            }
            SLOT_CREATE -> {
                player.closeInventory()
//                PollsManager.createPoll(name, options, duration)
                PollsManager.createPoll(
                    name,
                    options,
                    duration,
                    anonymous,
                    minimumContinuousUnlockTime
                )
                Bukkit.broadcast(Component.text("§3------------------------------"))
                val pollLine = Component.text("§3${name} ")
                    .append(
                        Component.text("§b/poll")
                            .clickEvent(ClickEvent.runCommand("/poll"))
                            .hoverEvent(HoverEvent.showText(Component.text("§7Click to vote")))
                    )
                    .append(Component.text("§3 to vote!"))
                Bukkit.broadcast(pollLine)
                Bukkit.broadcast(Component.text("§3------------------------------"))
                player.playSound(player.location, Sound.ENTITY_VILLAGER_YES, 1f, 1f)
            }
        }
    }

}