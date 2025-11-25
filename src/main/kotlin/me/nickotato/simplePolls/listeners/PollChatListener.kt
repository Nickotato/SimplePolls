package me.nickotato.simplePolls.listeners

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object PollChatListener: Listener {
    private val awaitingInput = mutableMapOf<Player, (String) -> Unit>()

    fun requestInput(player: Player, callback: (String) -> Unit) {
        awaitingInput[player] = callback
        player.sendMessage("§aPlease type your input in chat:")
    }

    @EventHandler
    fun onPlayerChat(event: AsyncChatEvent) {
        val player = event.player
        val callback = awaitingInput[player] ?: return
        event.isCancelled = true

        val inputText: String = PlainTextComponentSerializer.plainText().serialize(event.message())
        callback(inputText)
        awaitingInput.remove(player)
    }
}