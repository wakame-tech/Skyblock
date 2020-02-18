package tech.wakame.skyblock.skills

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

class Palette : InventoryHolder {
    private var visible: Boolean = false
    private val buffer: Array<ItemStack?> = Array(9) { null }
    private val palette: Inventory = Bukkit.createInventory(this, 9, "palette")

    init {
        for(i in 0 until 9) {
            palette.setItem(i, ItemStack(Material.STONE, i + 1))
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
}