package tech.wakame.skyblock.api

import org.bukkit.block.Chest
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import tech.wakame.skyblock.util.mapColumned

fun convertChestToMerchantRecipes(chest: Chest): List<MerchantRecipe> {
    return chest.inventory.storageContents
            .mapColumned<ItemStack?, MerchantRecipe?>(9) { (i1, i2, r) ->
                if (i1 == null || r == null) {
                    null
                } else if (i2 == null) {
                    MerchantRecipe(i1, Int.MAX_VALUE).apply {
                        addIngredient(r)
                    }
                } else {
                    MerchantRecipe(i1, Int.MAX_VALUE).apply {
                        addIngredient(i2)
                        addIngredient(r)
                    }
                }
            }
            .filterNotNull()
}