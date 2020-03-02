package tech.wakame.skyblock

import de.slikey.effectlib.effect.AtomEffect
import de.slikey.effectlib.effect.FountainEffect
import fr.minuskube.netherboard.Netherboard
import fr.mrmicky.fastparticle.FastParticle
import fr.mrmicky.fastparticle.ParticleType
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Chest
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.*
import tech.wakame.skyblock.api.Island
import tech.wakame.skyblock.api.convertChestToMerchantRecipes
import tech.wakame.skyblock.util.*

class SkyBlockEventListener(private val plugin: SkyBlock) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player.sendTitle("yellow{SkyBlock(仮称)}".colored(), plugin.description.version)
        Netherboard.instance().createBoard(event.player, "ステータス")

        val board = Netherboard.instance().getBoard(event.player)
        board.set("mp", 50)
        // event.player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = 30.0
    }

    @EventHandler
    fun useSkill(event: PlayerInteractEvent) {
        val use = event.player.inventory.itemInMainHand
        val key = NamespacedKey(plugin, "palette")
        val customMeta = use.getTag(key) ?: return
        val meta = PlayerMetaData(plugin)
        if (meta.inCoolTime(event.player)) {
            // in cool time
            event.player.sendMessage("in cool time")
            return
        }
        if (!use.hasItemMeta()) {
            return
        }

        when(customMeta) {
            "fire" -> {
                // FastParticle.spawnParticle(event.player, ParticleType.FLAME, event.player.location.overHead(), 100)
                AtomEffect(plugin.effectManager).apply {
                    entity = event.player
                    callback = Runnable {
                        event.player.sendMessage("end")
                    }
                    iterations = 20 * 2
                }.start()
                event.player.sendMessage("yellow{${event.player.displayName}} は ${use.itemMeta!!.displayName} を使った".colored())
            }
            "wind" -> {
//                FastParticle.spawnParticle(event.player, ParticleType.EXPLOSION_HUGE, event.player.location, 100)
//                event.player.sendMessage("${event.player.displayName} は ${use.itemMeta!!.displayName} を使った")

                FountainEffect(plugin.effectManager).apply {
                    entity = event.player
                    callback = Runnable {
                        event.player.sendMessage("end")
                    }
                    iterations = 20 * 2
                }.start()
                event.player.sendMessage("yellow{${event.player.displayName}} は ${use.itemMeta!!.displayName} を使った".colored())
            }
            "ice" -> {
                FastParticle.spawnParticle(event.player, ParticleType.SNOWBALL, event.player.location.overHead(), 100)
                event.player.sendMessage("yellow{${event.player.displayName}} は ${use.itemMeta!!.displayName} を使った".colored())
            }
            "heal" -> {
                FastParticle.spawnParticle(event.player, ParticleType.SPELL, event.player.location.overHead(), 100)
                event.player.sendMessage("yellow{${event.player.displayName}} は ${use.itemMeta!!.displayName} を使った".colored())
            }
            else -> return
        }

        val originalName = use.itemMeta!!.displayName
        use.renamed("$originalName gray{[使用不可]}")

        meta.coolTime(event.player) {
            use.renamed(originalName)
            event.player.sendMessage("end cool time")
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
        if (block.filledEnderEye() == false) {
            Island.capture(block.location, event.player)
        }
    }

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
            if (it.visible) {
                event.player.sendMessage("Palette: bold{yellow{ON}}".colored())
            } else {
                event.player.sendMessage("Palette: bold{OFF}".colored())
            }
        }
        event.isCancelled = true
    }
}
