package tech.wakame.skyblock.skills

import tech.wakame.skyblock.advancements.dsl.AdvancementsDSL
import tech.wakame.skyblock.advancements.dsl.elements.Criterion
import tech.wakame.skyblock.advancements.dsl.elements.DisplayFrame

val TestSkill1 = AdvancementsDSL("skyblock", "skill", "test_skill_1") {
    root {
        display("水の呼吸") {
            description = "ゾンビを倒そう"
        }

        criteria {
            criterion("a") {
                Criterion.playerKilledEntity("minecraft:zombie")
            }
        }

        rewards {
            experience = 100
        }

        advancement("2") {
            display("柱") {
                description = "石を置こう"
                frame = DisplayFrame.Goal
            }

            criteria {
                criterion("a") {
                    Criterion.placedBlock("minecraft:stone")
                }
            }
        }
    }
}
