package tech.wakame.skyblock.advancements.dsl.elements

import tech.wakame.skyblock.advancements.dsl.Key

data class Advancement(
        val parent: Advancement? = null,
        val name: Key,
        val display: Display? = null,
        val requirements: Array<Array<String>>? = null,
        val criteria: Map<String, Criterion> = mapOf(),
        val rewards: Unit? = null
) {
    override fun toString(): String {
        return "$name parent: ${parent?.name ?: "---"}"
    }
}