package tech.wakame.skyblock.advancements

import tech.wakame.skyblock.advancements.dsl.AdvancementsDSL
import tech.wakame.skyblock.advancements.dsl.advancement
import tech.wakame.skyblock.advancements.dsl.elements.Criterion

class SkillAdvancements(private val datapackRootPath: String) {
    init {
        val sub = advancement("a") {
            display("skill1-1")

            advancement("grandchild1") {
                display("skill1-1-1")
            }
        }

        val dsl = AdvancementsDSL("skyblock", "skill", "test") {
            root {
                display("skill1") {
                    description = "desc"
                }

                criteria {
                    criterion("a") { Criterion.impossible }
                }

                requirements {
                    arrayOf(arrayOf("a"))
                }

                merge(sub)

                advancement("child2") {
                    display("skill11-2")
                }
            }
        }

        dsl.dumpJson(datapackRootPath)
    }
}