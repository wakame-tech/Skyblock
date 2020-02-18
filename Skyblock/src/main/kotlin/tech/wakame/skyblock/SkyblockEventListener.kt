package tech.wakame.skyblock

import fr.minuskube.netherboard.Netherboard
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import tech.wakame.skyblock.skills.Palette

class SkyblockEventListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player.spigot().sendMessage(TextComponent("Hello, SkyBlock!").apply {
            color = ChatColor.BLUE
            isBold = true
        })
        Netherboard.instance().createBoard(event.player, "ステータス")
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val board = Netherboard.instance().getBoard(event.player)
        board.set("x", event.player.location.blockX)
        board.set("y", event.player.location.blockY)
        board.set("z", event.player.location.blockZ)
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.clickedInventory == null) return
        if (event.clickedInventory!!.holder is Palette) {
            event.whoClicked.sendMessage("edit palette")
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerSwapHandItemsEvent(event: PlayerSwapHandItemsEvent) {
        Skyblock.commandPalettes.putIfAbsent(event.player.uniqueId.toString(), Palette())
        Skyblock.commandPalettes[event.player.uniqueId.toString()]?.update(event.player)
        event.player.sendMessage("toggle palette")
        event.isCancelled = true
    }
}