package me.nickotato.simplePolls.commands

import me.nickotato.simplePolls.guis.MainPollGui
import me.nickotato.simplePolls.managers.GuiManager
import me.nickotato.simplePolls.utils.CommandUtils
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class PollCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, p2: String, args: Array<out String>): Boolean {
        val player = CommandUtils.requirePlayer(sender) ?: return true

        GuiManager.open(MainPollGui(player), player)

        return true
    }
}