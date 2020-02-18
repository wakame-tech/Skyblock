package tech.wakame.skyblock

import fr.minuskube.netherboard.Netherboard
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.*
import tech.wakame.skyblock.skills.Palette
import tech.wakame.skyblock.util.PlayerMetaData
import tech.wakame.skyblock.util.uuid

class SkyBlockEventListener(private val plugin: SkyBlock) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player.spigot().sendMessage(TextComponent("Hello, SkyBlock!").apply {
            color = ChatColor.BLUE
            isBold = true
        })
        Netherboard.instance().createBoard(event.player, "ステータス")

        // event.player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = 30.0
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val meta = PlayerMetaData(event.player, plugin)
        if (meta.get()) {
            // in cool time
            return
        }
        val use = event.player.inventory.itemInMainHand
        if (use.type != Material.SNOWBALL || !use.hasItemMeta()) {
            return
        }
        if (use in Palette.presets) {
            event.player.sendMessage("${event.player.displayName} は ${use.itemMeta!!.displayName} を使った")
            meta.set()
        }
    }

//    @EventHandler
//    fun onPlayerMove(event: PlayerMoveEvent) {
//        val board = Netherboard.instance().getBoard(event.player)
//        board.set("x", event.player.location.blockX)
//        board.set("y", event.player.location.blockY)
//        board.set("z", event.player.location.blockZ)
//    }

    @EventHandler
    fun onPlayerItemDrop(event: PlayerDropItemEvent) {
        if (Palette.store[event.player.uuid]?.visible == true) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        if (event.inventory.holder is Palette) {
            event.player.sendMessage("update palette")
            event.player.sendMessage(event.inventory.filterNotNull().joinToString(",") { it.itemMeta?.displayName ?: it.type.toString() })
            return
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        if (Palette.store[player.uuid]?.visible == true) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerSwapHandItemsEvent(event: PlayerSwapHandItemsEvent) {
        Palette.store.getOrPut(event.player.uuid) { Palette() }.also {
            it.update(event.player)
            event.player.sendMessage("toggle palette")
        }
        event.isCancelled = true
    }
}