package me.nickotato.simplePolls.guis

import me.nickotato.simplePolls.SimplePolls
import me.nickotato.simplePolls.managers.GuiManager
import me.nickotato.simplePolls.managers.PollsManager
import me.nickotato.simplePolls.model.Poll
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.persistence.PersistentDataType
import java.time.format.DateTimeFormatter
import java.util.Locale

class ViewExpiredPollsGui: Gui(Component.text("§8Viewing Expired Polls"),54) {
    private var page = 1
    private val air = ItemStack(Material.AIR)

    init {
        buildPage()
    }

    private fun buildPage() {
        for (i in 0 until 53) {
            setItem(i, air)
        }
        for ((index, poll) in getViewablePolls().withIndex()) {
            val item = ItemStack(Material.WRITTEN_BOOK)
            val meta = item.itemMeta as BookMeta
            meta.displayName(Component.text("§6${poll.question}"))

            val lore = mutableListOf<Component>()

            lore.add(Component.text("§7Winner was: §d${PollsManager.getOptionWithMostVotes(poll)}"))

            lore.add(Component.text("§8"))
            for ((option, votes) in poll.options) {
                lore.add(Component.text("§f$option: §a$votes votes"))
            }
            lore.add(Component.text("§8"))

            val dateFormatter =
                DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a", Locale.US)

//            lore.add( Component.text("§7Ended on ${poll.endsAt}"))
            lore.add(
                Component.text(
                    "§7Ended on ${poll.endsAt.format(dateFormatter)}"
                )
            )

            val pdc = meta.persistentDataContainer
            pdc.set(SimplePolls.POLL_KEY, PersistentDataType.INTEGER, poll.id)

            meta.lore(lore)
            item.itemMeta = meta
            setItem(index,item)
        }

        buildNavigationItems()
    }

    private fun getViewablePolls(): List<Poll> {
        val polls = PollsManager.expiredPolls
        val viewablePolls = polls.drop(45 * (page - 1 )).take(45)
        return viewablePolls
    }

    private fun hasNextPage(): Boolean {
        return PollsManager.expiredPolls.size > page * 45
    }

    private fun hasPreviousPage(): Boolean {
        return page > 1
    }

    private fun buildNavigationItems() {
        val default = ItemStack(Material.ARROW)

        val next = default.clone()
        val nextMeta = next.itemMeta
        nextMeta.displayName(Component.text("Next Page"))
        next.itemMeta = nextMeta

        val previous = default.clone()
        val previousMeta = previous.itemMeta
        previousMeta.displayName(Component.text("Previous Page"))
        previous.itemMeta = previousMeta

        if (hasPreviousPage()) {
            setItem(45, previous)
        } else {
            setItem(45, ItemStack(Material.AIR))
        }

        if (hasNextPage()) {
            setItem(53, next)
        } else {
            setItem(53, ItemStack(Material.AIR))
        }
    }

    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true
        val player = event.whoClicked
        if (player !is Player) return
        val slot = event.slot

        when (slot) {
            45 -> {
                if (hasPreviousPage()) {
                    page--
                    buildPage()
                }
            }
            53 -> {
                if (hasNextPage()) {
                    page++
                    buildPage()
                }
            }
        }

        val item = event.currentItem ?: return
        val meta = item.itemMeta ?: return

        val pdc = meta.persistentDataContainer
        val pollId = pdc.get(SimplePolls.POLL_KEY, PersistentDataType.INTEGER) ?: return

        val poll = PollsManager.expiredPolls.find { it.id == pollId } ?: return

        if (poll.anonymous) {
            player.sendMessage("§4Poll is anonymous. Cannot view.")
            return
        }

        GuiManager.open(PollResultsGui(poll), player)
    }
}