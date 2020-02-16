package tech.wakame.skyblock.advancements.dsl.elements

import tech.wakame.skyblock.advancements.dsl.Key

class Criterion(
        val trigger: Key = Key("minecraft" , "impossible"),
        val conditions: Map<String, String> = mapOf()
) {
    companion object {
        val impossible = Criterion(trigger = Key("minecraft", "impossible"))

        fun placedBlock(id: String) = Criterion(
                trigger = Key("minecraft", "placed_block"),
                conditions = mapOf("block" to id)
        )
    }
}