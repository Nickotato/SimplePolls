package me.nickotato.simplePolls.guis

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class ConfigGui: Gui(Component.text("§8Configuring Settings"), 27) {
    private val air = ItemStack(Material.AIR)

    init {
        buildPage()
    }

    private fun buildPage() {
        for (i in 0 until 26) {
            setItem(i, air)
        }

//        val anonymousMaterial =
//            if (.anonymous) Material.LIME_DYE
//            else Material.GRAY_DYE
//
//        val anonymous = ItemStack(Material.LIME_STAINED_GLASS_PANE)
//        val meta = item.itemMeta as BookMeta
//        meta.displayName(Component.text("§6${poll.question}"))
//
//        item.itemMeta = meta
//        setItem(13,item)
    }

    override fun onClick(event: InventoryClickEvent) {
        //This does nothing
    }
}