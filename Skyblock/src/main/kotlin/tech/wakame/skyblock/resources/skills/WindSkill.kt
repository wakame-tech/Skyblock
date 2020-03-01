package tech.wakame.skyblock.resources.skills

import tech.wakame.skyblock.advancements.dsl.AdvancementsDSL
import tech.wakame.skyblock.advancements.dsl.elements.Criterion
import tech.wakame.skyblock.advancements.dsl.elements.DisplayIcon

val WindSkill = AdvancementsDSL("skyblock", "skill", "wind_skill") {
    root {
        display("風の心") {
            icon = DisplayIcon("minecraft:cobweb")
            description = "風魔法見習い"
        }
        criteria { criterion("c1") { Criterion.impossible } }
        rewards { experience = 100 }

        advancement("w-1") {
            display("蜃気楼") {
                icon = DisplayIcon("minecraft:cobweb")
            }
            criteria { criterion("c1") { Criterion.impossible } }
            rewards { experience = 100 }

            advancement("w-2") {
                display("吹き飛ばし") {
                    icon = DisplayIcon("minecraft:cobweb")
                }
                criteria { criterion("c1") { Criterion.impossible } }
                rewards { experience = 100 }
            }

            advancement("w-3") {
                display("疾風") {
                    icon = DisplayIcon("minecraft:cobweb")
                }
                criteria { criterion("c1") { Criterion.impossible } }
            }

            advancement("w-4") {
                display("飛翔") {
                    icon = DisplayIcon("minecraft:cobweb")
                }
                criteria { criterion("c1") { Criterion.impossible } }

                advancement("w-5") {
                    display("翼を授ける") {
                        icon = DisplayIcon("minecraft:cobweb")
                    }
                    criteria { criterion("c1") { Criterion.impossible } }
                }
                criteria { criterion("c1") { Criterion.impossible } }

                advancement("w-6") {
                    display("風の剣") {
                        icon = DisplayIcon("minecraft:cobweb")
                    }
                    criteria { criterion("c1") { Criterion.impossible } }

                    advancement("w-7") {
                        display("衝撃波") {
                            icon = DisplayIcon("minecraft:cobweb")
                        }

                        criteria { criterion("c1") { Criterion.impossible } }
                    }
                    criteria { criterion("c1") { Criterion.impossible } }
                }
            }
        }
    }
}
