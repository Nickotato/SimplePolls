package me.nickotato.simplePolls

import me.nickotato.simplePolls.commands.PollCommand
import me.nickotato.simplePolls.managers.GuiManager
import org.bukkit.plugin.java.JavaPlugin

class SimplePolls : JavaPlugin() {

    companion object {
        lateinit var instance: SimplePolls
            private set
    }

    override fun onEnable() {
        instance = this

        server.pluginManager.registerEvents(GuiManager, this)

        getCommand("poll")?.setExecutor(PollCommand())
    }
}
