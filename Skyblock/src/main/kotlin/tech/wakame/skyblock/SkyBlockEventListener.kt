package tech.wakame.skyblock

import fr.minuskube.netherboard.Netherboard
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.*
import tech.wakame.skyblock.api.convertChestToMerchantRecipes
import tech.wakame.skyblock.util.Palette
import tech.wakame.skyblock.util.IUI
import tech.wakame.skyblock.util.PlayerMetaData
import tech.wakame.skyblock.util.colored
import tech.wakame.skyblock.util.uuid

class SkyBlockEventListener(private val plugin: SkyBlock) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player.sendTitle("yellow{SkyBlock(仮称)}".colored(), plugin.description.version)
        Netherboard.instance().createBoard(event.player, "ステータス")

        // event.player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = 30.0
    }

    @EventHandler
    fun useSkill(event: PlayerInteractEvent) {
        val use = event.player.inventory.itemInMainHand
        if (use.type != Material.SNOWBALL) return
        val meta = PlayerMetaData(event.player, plugin)
        if (meta.get()) {
            // in cool time
            return
        }
        if (use.type != Material.SNOWBALL || !use.hasItemMeta()) {
            return
        }
        if (use in Palette.presets) {
            event.player.sendMessage("${event.player.displayName} は ${use.itemMeta!!.displayName} を使った")
            meta.set()
        }
    }

    @EventHandler
    fun convertMerchant(event: PlayerInteractEvent) {
        val use = event.player.inventory.itemInMainHand
        if (use.type != Material.BLAZE_ROD) return
        val block = event.clickedBlock ?: return
        if (block.type != Material.CHEST) {
            return
        }
        val up = block.location.add(0.0, 1.0, 0.0)
        val chest = block.state as Chest
        val villager = event.player.world.spawnEntity(up, EntityType.VILLAGER) as Villager
        villager.apply {
            profession = Villager.Profession.FARMER
            // tradable level
            villagerLevel = 2
            recipes = convertChestToMerchantRecipes(chest)
        }
    }

    @EventHandler
    fun captureIsland(event: PlayerInteractEvent) {
        val use = event.player.inventory.itemInMainHand
        if (use.type != Material.ENDER_EYE) return
        val block = event.clickedBlock ?: return
        // unfilled: 0 - 3, filled: 4 - 7
        val eyeUnfilled = block.data < 4.toByte()
        if (block.type == Material.END_PORTAL_FRAME && eyeUnfilled) {
            event.player.sendTitle("yellow{島を攻略した!!}".colored(), "攻略率 1 / 1")
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

        IUI.catalog.onClick(event)
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