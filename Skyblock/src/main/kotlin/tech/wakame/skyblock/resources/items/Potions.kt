package tech.wakame.skyblock.resources.items

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import tech.wakame.skyblock.util.described
import tech.wakame.skyblock.util.effected
import tech.wakame.skyblock.util.renamed

val potions = listOf(
        ItemStack(Material.SPLASH_POTION, 1)
                .renamed("red{エレベーターポーション}")
                .described("yellow{コレを使えば緊急時の上り下りが非常に楽になります！}\n" +
                        "red{※ついでに落下死も増えます！}\n" +
                        "梯子なんていらなかったんや！")
                .effected(PotionEffect(PotionEffectType.JUMP, 200, 50)),
        ItemStack(Material.POTION, 1)
                .renamed("bold{イカスミ}")
                .effected(PotionEffect(PotionEffectType.SLOW, 100, 7))
                .effected(PotionEffect(PotionEffectType.SATURATION, 100, 2))
)