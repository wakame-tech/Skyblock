package tech.wakame.skyblock.util

import org.bukkit.ChatColor
import org.bukkit.inventory.ItemStack

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

//
// ItemStack extensions
//
fun ItemStack.renamed(name: String): ItemStack {
    return this.apply {
        itemMeta = itemMeta?.apply { setDisplayName(name) }
    }
}