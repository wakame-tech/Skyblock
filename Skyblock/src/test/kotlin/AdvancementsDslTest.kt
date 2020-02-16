
import org.junit.Test
import tech.wakame.skyblock.advancements.dsl.AdvancementsDSL
import tech.wakame.skyblock.advancements.dsl.elements.Criterion

class AdvancementsDslTest {
     @Test
    fun test() {
        val dsl = AdvancementsDSL("skyblock","skill", "skill1") {
            root {
                display("skill1") {
                    description = "desc"
                }

                criteria {
                    criterion("a") { Criterion.impossible }
                }

                rewards {
                    experience = 100
                }

                advancement("child1") {
                    display("skill11-2")

                    criteria {
                        criterion("a") {
                            Criterion.placedBlock("minecraft:stone")
                        }
                    }
                }
            }
        }

        dsl.dumpJson("./../data/void/datapacks")
    }
}