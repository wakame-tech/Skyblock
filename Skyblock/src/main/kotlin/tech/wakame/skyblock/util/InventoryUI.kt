package tech.wakame.skyblock.util

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class Option<T>(val icon: Material, var name: String) {
    private var options: List<T>? = listOf()
    private var index: Int = 0
    private var onClick: (Player) -> T? = { null }
    private var decorator: (Option<*>) -> String = { option ->
        "bold{[Config]} bold{yellow{<${option.name}>}} updated to bold{yellow{${option.value}}}."
    }

    fun optioned(options: List<T>) = apply {
        this.options = options
    }

    fun onClick(callback: (Player) -> T?) = apply {
        this.onClick = callback
    }

    fun setDecorator(decorator: (Option<*>) -> String) = apply {
        this.decorator = decorator
    }

    val value
        get() = options?.let { it[index] }

    fun doClickEvent(player: Player) {
        options?.let {
            index = (index + 1) % it.size
        }
        onClick(player)
    }
}

class InventoryUI(displayName: String, private val options: List<Option<*>>) {
    private val ui: Inventory = Bukkit.createInventory(null, (options.size / 9 + 1) * 9, displayName)

    init {
        options.forEach { it.refresh() }
    }

    private fun Option<*>.refresh() {
        ui.setItem(options.indexOf(this), ItemStack(icon).renamed("${ChatColor.YELLOW}$name : ${ChatColor.WHITE}$value"))
    }

    operator fun <T> get(key: String): T? {
        return options.firstOrNull { it.name == key }?.value as? T
    }

    fun toggleOption(slotIndex: Int, player: Player) {
        require(slotIndex < options.size)
        options[slotIndex].run {
            doClickEvent(player)
            refresh()
        }
    }

    fun open(player: Player) {
        player.openInventory(ui)
    }
}
