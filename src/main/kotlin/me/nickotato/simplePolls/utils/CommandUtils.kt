package me.nickotato.simplePolls.utils

import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object CommandUtils {
    fun requirePlayer(sender: CommandSender): Player? {
        if (sender is Player) return sender

        sender.sendMessage(Component.text("§cOnly a player can run this command"))
        return null
    }
}