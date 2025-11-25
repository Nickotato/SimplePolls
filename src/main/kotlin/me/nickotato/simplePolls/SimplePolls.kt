package me.nickotato.simplePolls

import me.nickotato.simplePolls.commands.PollCommand
import me.nickotato.simplePolls.listeners.PlayerJoinListener
import me.nickotato.simplePolls.listeners.PollChatListener
import me.nickotato.simplePolls.managers.GuiManager
import me.nickotato.simplePolls.managers.PollsManager
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

class SimplePolls : JavaPlugin() {

    companion object {
        lateinit var instance: SimplePolls
        lateinit var POLL_KEY: NamespacedKey
        lateinit var OPTION_KEY: NamespacedKey
        lateinit var ACTION_KEY: NamespacedKey
            private set
    }

    override fun onEnable() {
        instance = this
        POLL_KEY = NamespacedKey(this, "poll_id")
        OPTION_KEY = NamespacedKey(this, "option_name")
        ACTION_KEY = NamespacedKey(this, "poll_action")

        server.pluginManager.registerEvents(GuiManager, this)
        server.pluginManager.registerEvents(PollChatListener, this)
        server.pluginManager.registerEvents(PlayerJoinListener(), this)

        getCommand("poll")?.setExecutor(PollCommand())

        PollsManager.beginRepeatingTasks()
    }

    override fun onDisable() {
        PollsManager.save()
    }
}
