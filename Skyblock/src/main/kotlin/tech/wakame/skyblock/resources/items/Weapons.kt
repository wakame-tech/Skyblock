package tech.wakame.skyblock.resources.items

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import tech.wakame.skyblock.util.colored
import tech.wakame.skyblock.util.described
import tech.wakame.skyblock.util.enchanted
import tech.wakame.skyblock.util.renamed

val weapons = listOf(
        ItemStack(Material.WOODEN_SWORD, 1)
                .renamed("ひのきの棒"),
        ItemStack(Material.TRIDENT, 1)
                .renamed("aqua{italic{グングニル}}")
                .enchanted(Enchantment.DAMAGE_ALL, 10000)
                .enchanted(Enchantment.LOYALTY)
                .enchanted(Enchantment.MENDING)
                .enchanted(Enchantment.CHANNELING),
        ItemStack(Material.END_CRYSTAL)
                .renamed("bold{red{赤い彗星}}")
                .enchanted(Enchantment.DAMAGE_ALL, 40)
                .enchanted(Enchantment.FIRE_ASPECT, 15)
                .enchanted(Enchantment.LOYALTY)
                .described("italic{red{情けないMS}}"),
        ItemStack(Material.LILY_PAD)
                .renamed("bold{green{-{葉っぱ一枚あればいい}}}")
                .enchanted(Enchantment.KNOCKBACK, 10)
)