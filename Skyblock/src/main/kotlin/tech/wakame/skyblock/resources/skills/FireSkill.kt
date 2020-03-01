package tech.wakame.skyblock.resources.skills

import tech.wakame.skyblock.advancements.dsl.AdvancementsDSL
import tech.wakame.skyblock.advancements.dsl.elements.Criterion
import tech.wakame.skyblock.advancements.dsl.elements.DisplayFrame
import tech.wakame.skyblock.advancements.dsl.elements.DisplayIcon

val FireSkill = AdvancementsDSL("skyblock", "skill", "fire_skill") {
    root {
        display("炎の心") {
            icon = DisplayIcon("minecraft:fire_charge")
            description = "炎魔法見習い"
            background = "minecraft:textures/block/quartz_block_top.png"
        }
        criteria { criterion("c1") { Criterion.impossible } }
        rewards { experience = 100 }

        advancement("f-1") {
            display("炎上網") {
                icon = DisplayIcon("minecraft:fire_charge")
                description = "前方に炎を発生させる"
            }

            criteria { criterion("c1") { Criterion.impossible } }

            advancement("f-2") {
                display("火柱") {
                    icon = DisplayIcon("minecraft:fire_charge")
                    description = "相手の上から溶岩を降らせる"
                }
                criteria { criterion("c1") { Criterion.impossible } }

                advancement("f-4") {
                    display("炎帝") {
                        icon = DisplayIcon("minecraft:fire_charge")
                        description = "前方に大爆発"
                    }
                    criteria { criterion("c1") { Criterion.impossible } }
                }

                advancement("f-6") {
                    display("爆炎") {
                        icon = DisplayIcon("minecraft:fire_charge")
                        description = "前方に爆発を起こす"
                    }
                    criteria { criterion("c1") { Criterion.impossible } }
                }
            }

            advancement("f-3") {
                display("火炎弾") {
                    icon = DisplayIcon("minecraft:fire_charge")
                    description = "前方に火の玉を発射"
                }
                criteria { criterion("c1") { Criterion.impossible } }

                advancement("f-5") {
                    display("炎化") {
                        icon = DisplayIcon("minecraft:fire_charge")
                        description = "火炎態勢を付与"
                    }
                    criteria { criterion("c1") { Criterion.impossible } }

                    advancement("f-7") {
                        display("再生の炎") {
                            icon = DisplayIcon("minecraft:fire_charge")
                            description = "再生の効果を付与"
                            frame = DisplayFrame.Goal
                        }
                        criteria { criterion("c1") { Criterion.impossible } }
                    }
                }
            }
        }
    }
}
