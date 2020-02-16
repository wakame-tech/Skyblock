package tech.wakame.skyblock.advancements.dsl.elements

class Rewards(
        var recipes: Array<String>? = null,
        var loot: Array<String>? = null,
        var experience: Int? = null,
        var function: String? = null
)