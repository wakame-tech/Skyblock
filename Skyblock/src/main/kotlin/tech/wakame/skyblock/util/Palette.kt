package tech.wakame.skyblock.util

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import tech.wakame.skyblock.SkyBlock

class Palette : InventoryHolder {
    var visible: Boolean = false
    private set
    private val buffer: Array<ItemStack?> = Array(9) { null }
    private val palette: Inventory = Bukkit.createInventory(this, 9, "palette")

    init {
        require(presets.size == 9)
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
        // player UUID to Palette
        val store: MutableMap<String, Palette> = mutableMapOf()
        private val paletteKey = NamespacedKey(SkyBlock.instance, "palette")

        private val fireTechDisk = ItemStack(Material.BLAZE_POWDER, 1)
                .renamed("bold{red{フォイエ Lv.1}}")
                .enchanted(Enchantment.LUCK, 1)
                .addTag(paletteKey, "fire")
        private val windTechDisk = ItemStack(Material.END_CRYSTAL, 1)
                .renamed("bold{green{シフタ Lv.1}}")
                .enchanted(Enchantment.LUCK, 1)
                .addTag(paletteKey, "wind")
        private val iceTechDisk = ItemStack(Material.ICE, 1)
                .renamed("bold{blue{バータ Lv.1}}")
                .enchanted(Enchantment.LUCK, 1)
                .addTag(paletteKey, "ice")
        private val healDisk = ItemStack(Material.NETHER_STAR, 1)
                .renamed("bold{yellow{レスタ Lv.1}}")
                .enchanted(Enchantment.LUCK, 1)
                .addTag(paletteKey, "heal")

        var presets: Array<ItemStack?> = arrayOf(fireTechDisk, windTechDisk, iceTechDisk, null, null, null, null, null, healDisk)
    }
}