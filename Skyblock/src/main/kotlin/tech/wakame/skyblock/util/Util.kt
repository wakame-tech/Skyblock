package tech.wakame.skyblock.util

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.scheduler.BukkitRunnable
import tech.wakame.skyblock.SkyBlock

/*
    文字列内の "{color}" を色コードに置き換える
 */
fun String.colored(): String {
    fun String.replace(map: Map<String, Any>) = map.toList().fold(this) { text, (key, value) ->
        text.replace(key.toRegex(), value.toString())
    }

    return this.replace(mapOf(
            "black\\{" to ChatColor.WHITE,
            "red\\{" to ChatColor.RED,
            "green\\{" to ChatColor.GREEN,
            "yellow\\{" to ChatColor.YELLOW,
            "blue\\{" to ChatColor.BLUE,
            "white\\{" to ChatColor.WHITE,
            "bold\\{" to ChatColor.BOLD,
            "\\}" to ChatColor.RESET
    ))
}

val Player.uuid: String
    get() = this.uniqueId.toString()

//
// ItemStack extensions
//
fun ItemStack.renamed(name: String): ItemStack {
    return this.apply {
        itemMeta = itemMeta?.apply { setDisplayName(name) }
    }
}

class PlayerMetaData(private val player: Player, private val plugin: SkyBlock) {
    private val KEY = "PlayerInteractEvent"

    fun set() {
        setMetaData(true)
        object: BukkitRunnable() {
            override fun run() {
                setMetaData(false)
            }
        }.runTaskLater(plugin, 1)
    }

    private fun setMetaData(flag: Boolean) {
        player.setMetadata(KEY, FixedMetadataValue(plugin, flag))
    }

    fun get() = player.getMetadata(KEY).firstOrNull()?.asBoolean() ?: false
}