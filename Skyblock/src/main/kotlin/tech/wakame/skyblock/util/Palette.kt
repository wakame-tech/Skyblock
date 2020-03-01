package tech.wakame.skyblock.util

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

class Palette : InventoryHolder {
    var visible: Boolean = false
    private set
    private val buffer: Array<ItemStack?> = Array(9) { null }
    private val palette: Inventory = Bukkit.createInventory(this, 9, "palette")

    init {
        for (i in 0 until 9) {
            palette.setItem(i, presets[i] ?: ItemStack(Material.BARRIER, 1))
        }
    }

    override fun getInventory() = palette

    fun open(player: Player) {
        player.openInventory(palette)
    }

    fun update(player: Player) {
        visible = !visible
        if (visible) {
            for (i in 0 until 9) {
                buffer[i] = player.inventory.getItem(i)
                player.inventory.setItem(i, palette.getItem(i))
            }
        } else {
            for (i in 0 until 9) {
                player.inventory.setItem(i, buffer[i])
            }
        }
    }

    companion object {
        val store: MutableMap<String, Palette> = mutableMapOf()
        val fireHeart = ItemStack(Material.SNOWBALL, 1).apply {
            itemMeta = itemMeta?.apply {
                setDisplayName("bold{red{火の心}}".colored())
                lore = listOf("火属性の魔法を使うことができる")
                addEnchant(Enchantment.LUCK, 1, false)
            }
        }
        val windHeart = ItemStack(Material.SNOWBALL, 1).apply {
            itemMeta = itemMeta?.apply {
                setDisplayName("bold{green{風の心}}".colored())
                lore = listOf("風属性の魔法を使うことができる")
                addEnchant(Enchantment.LUCK, 1, false)
            }
        }
        val iceHeart = ItemStack(Material.SNOWBALL, 1).apply {
            itemMeta = itemMeta?.apply {
                setDisplayName("bold{blue{氷の心}}".colored())
                lore = listOf("氷属性の魔法を使うことができる")
                addEnchant(Enchantment.LUCK, 1, false)
            }
        }
        val hpUnit = ItemStack(Material.BOOK, 1).apply {
            itemMeta = itemMeta?.apply {
                setDisplayName("bold{yellow{HPユニット}}".colored())
                lore = listOf("最大HPがアップする")
                addEnchant(Enchantment.LUCK, 1, false)
            }
        }

        var presets: Array<ItemStack?> = arrayOf(fireHeart, windHeart, iceHeart, hpUnit, null, null, null, null, null)
    }
}