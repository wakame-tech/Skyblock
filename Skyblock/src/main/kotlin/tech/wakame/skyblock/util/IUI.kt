package tech.wakame.skyblock.util

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import tech.wakame.skyblock.resources.items.potions
import tech.wakame.skyblock.resources.items.weapons
import tech.wakame.skyblock.resources.skills.FireSkill
import kotlin.math.ceil

class IUI(private val content: List<ItemStack>): InventoryHolder {
    enum class FilterType {
        All, Potion, Weapon, KeyItem
    }

    private val size = 9 * 6
    private var currentPage = 0
    private var currentFilter = FilterType.All
    private val prevIndex = 45
    private val allIndex = 46
    private val potionIndex = 47
    private val weaponIndex = 48
    private val keyItemIndex = 49
    private val nextIndex = 53
    private val pages: Int
    get() = ceil(content.size.toFloat() / (size - 9).toFloat()).toInt()

    override fun getInventory(): Inventory {
        val content = content.chunked(size - 9)[currentPage]
        return Bukkit.createInventory(this, size, "catalog").apply {
            addItem(*filtering(content).toTypedArray())
            setItem(prevIndex, ItemStack(Material.PAPER).renamed("Prev"))
            setItem(nextIndex, ItemStack(Material.PAPER).renamed("Next"))
            setItem(allIndex, ItemStack(Material.BARRIER).renamed("All"))
            setItem(potionIndex, ItemStack(Material.POTION).renamed("All"))
            setItem(weaponIndex, ItemStack(Material.STONE_SWORD).renamed("Weapon"))
            setItem(keyItemIndex, ItemStack(Material.NAME_TAG).renamed("KeyItem"))
        }
    }

    private fun filtering(contents: List<ItemStack>): List<ItemStack> {
        return when(currentFilter) {
            FilterType.All -> contents
            FilterType.Potion -> contents.filter {
                it.type in listOf(Material.POTION, Material.SPLASH_POTION)
            }
            FilterType.Weapon -> contents.filter {
                it.type in WEAPONS
            }
            FilterType.KeyItem -> contents.filter {
                it.type in arrayOf<Material>()
            }
        }
    }

    fun open(player: Player) {
        player.openInventory(inventory)
    }

    fun onClick(event: InventoryClickEvent) {
        val slotIndex = event.rawSlot
        if (event.whoClicked !is Player) {
            return
        }
        if (event.clickedInventory?.holder !is IUI) {
            return
        }

        val player = event.whoClicked as Player

        when(slotIndex) {
            in 0 until size - 9 -> {
                val target: ItemStack = inventory.contents[slotIndex] ?: return
                player.sendMessage("$target")
                player.inventory.addItem(target)
            }
            prevIndex -> {
                if (currentPage in 1 until pages) {
                    currentPage--
                }
                player.openInventory(inventory)
            }
            nextIndex -> {
                if (currentPage in 0 until pages - 1) {
                    currentPage++
                }
                player.openInventory(inventory)
            }
            allIndex -> {
                currentFilter = FilterType.All
                player.openInventory(inventory)
            }
            potionIndex -> {
                currentFilter = FilterType.Potion
                player.openInventory(inventory)
            }
            weaponIndex-> {
                currentFilter = FilterType.Weapon
                player.openInventory(inventory)
            }
            keyItemIndex -> {
                currentFilter = FilterType.KeyItem
                player.openInventory(inventory)
            }
        }

        event.isCancelled = true
    }

    companion object {
        val catalog = IUI(weapons + potions + FireSkill.catalog())
    }
}