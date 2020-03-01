package tech.wakame.skyblock.util

import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Server
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.scheduler.BukkitRunnable
import tech.wakame.skyblock.SkyBlock

fun Server.broadcast(vararg message: String) {
    this.spigot().broadcast(*message.map { TextComponent(it.colored()) }.toTypedArray())
}

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
        itemMeta = itemMeta?.apply { setDisplayName(name.colored()) }
    }
}

fun ItemStack.described(describe: String): ItemStack {
    return this.apply {
        itemMeta = itemMeta?.apply { lore = describe.colored().split("\n") }
    }
}

fun ItemStack.effected(effect: PotionEffect): ItemStack {
    return this.apply {
        itemMeta = (itemMeta as? PotionMeta)?.apply {
            addCustomEffect(effect, true)
        }
    }
}

fun ItemStack.enchanted(enchant: Enchantment, level: Int): ItemStack {
    return this.apply {
        itemMeta = itemMeta?.apply {
            addEnchantment(enchant, level)
        }
    }
}

fun ItemStack.addTag(key: NamespacedKey, value: String): ItemStack {
    return this.apply {
        itemMeta = itemMeta?.apply {
            persistentDataContainer.set(key, PersistentDataType.STRING, value)
        }
    }
}

fun ItemStack.getTag(key: NamespacedKey): String? {
    return this.itemMeta?.persistentDataContainer?.get<String, String>(key, PersistentDataType.STRING)
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

// iterated to column
inline fun <reified T, R> Array<T>.mapColumned(width: Int, transform: (List<T>) -> R): List<R> {
    val columns = this.size.div(width)
    return (0 until width).map{ i ->
        val items = List(columns) { this[it * width + i] }
        transform(items)
    }
}

fun <T> List<T>.forEachChunked(n: Int, block: (List<T>) -> Unit) {
    require(n != 0)
    for (i in 0..(size / n)) {
        block(subList(i * n, ((i + 1) * n).coerceAtMost(size)))
    }
}

val PICKAXES = arrayOf(
        Material.DIAMOND_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.STONE_PICKAXE, Material.WOODEN_PICKAXE
)

val SHOVELS = arrayOf(
        Material.DIAMOND_SHOVEL, Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL, Material.STONE_SHOVEL, Material.WOODEN_SHOVEL
)

val SWORDS = arrayOf(
        Material.DIAMOND_SWORD, Material.GOLDEN_SWORD, Material.IRON_SWORD, Material.STONE_SWORD, Material.WOODEN_SWORD
)

val OTHER_WEAPONS = arrayOf(
        Material.BOW, Material.TRIDENT, Material.CROSSBOW, Material.SHIELD
)

val HOES = arrayOf(
        Material.DIAMOND_HOE, Material.GOLDEN_HOE, Material.IRON_HOE, Material.STONE_HOE, Material.WOODEN_HOE
)

val AXES = arrayOf(
        Material.DIAMOND_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.STONE_AXE, Material.WOODEN_AXE
)

val HELMETS = arrayOf(
        Material.DIAMOND_HELMET, Material.IRON_HELMET, Material.CHAINMAIL_HELMET, Material.GOLDEN_HELMET, Material.LEATHER_HELMET
)

val CHESTPLATES = arrayOf(
        Material.DIAMOND_CHESTPLATE, Material.IRON_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.GOLDEN_CHESTPLATE, Material.LEATHER_CHESTPLATE
)

val LEGGINGS = arrayOf(
        Material.CHAINMAIL_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.IRON_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.LEATHER_LEGGINGS
)

val BOOTS = arrayOf(
        Material.DIAMOND_BOOTS, Material.GOLDEN_BOOTS, Material.IRON_BOOTS, Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS
)

val OTHER_TOOLS = arrayOf(
        Material.BOW, Material.FLINT_AND_STEEL, Material.FISHING_ROD, Material.SHEARS
)

val DUARUBLE_TOOLS = arrayOf(
        *PICKAXES, *SHOVELS, *SWORDS, *AXES, *HOES, *HELMETS, *CHESTPLATES, *LEGGINGS, *BOOTS, *OTHER_TOOLS
)

val WEAPONS = arrayOf(
    *SWORDS, *OTHER_WEAPONS
)