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
import org.bukkit.persistence.PersistentDataType
import java.time.Duration
import java.time.LocalDateTime

class ManagePollsGui: Gui(Component.text("§6Managing Polls"),54) {
    private val page = 1

    init {
        val create = ItemStack(Material.WRITABLE_BOOK)
        val createMeta = create.itemMeta
        createMeta.displayName(Component.text("§2Create Poll"))
        create.itemMeta = createMeta
        setItem(50, create)

        for ((index, poll) in getViewablePolls().withIndex()) {
            val pollItem = ItemStack(Material.WRITABLE_BOOK, 1)
            val pollMeta = pollItem.itemMeta
            pollMeta.displayName(Component.text("§6${poll.question}"))

            val lore = mutableListOf<Component>()
            val now = LocalDateTime.now()
            val ends = poll.endsAt
            val duration = Duration.between(now, ends)
            if (!duration.isNegative) {
                val hoursLeft = duration.toHours()
                lore.add(Component.text("§7Ends in §3$hoursLeft §7hours"))
            } else {
                lore.add(Component.text("§cExpired"))
            }

            pollMeta.lore(lore)

            val pdc = pollMeta.persistentDataContainer
            pdc.set(SimplePolls.POLL_KEY, PersistentDataType.INTEGER, poll.id)

            pollItem.itemMeta = pollMeta
            setItem(index, pollItem)
        }
    }

    private fun getViewablePolls(): List<Poll> {
        val polls = PollsManager.polls
        val viewablePolls = polls.drop(45 * (page - 1 )).take(45)
        return viewablePolls
    }

    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true
        val player = event.whoClicked
        if (player !is Player) return
        val slot = event.slot

        when (slot) {
            50 -> {
                GuiManager.open(CreatePollGui(), player)
            }
        }

        val item = event.currentItem ?: return
        val meta = item.itemMeta ?: return
        val pdc = meta.persistentDataContainer
        val pollId = pdc.get(SimplePolls.POLL_KEY, PersistentDataType.INTEGER) ?: return
        val poll = PollsManager.polls.find { it.id == pollId } ?: return
    }
}