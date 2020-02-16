
import org.bukkit.Material
import org.junit.Test
import tech.wakame.skyblock.advancements.dsl.AdvancementsDSL
import tech.wakame.skyblock.advancements.dsl.advancement
import tech.wakame.skyblock.advancements.dsl.elements.Criterion
import java.io.File
import kotlin.test.assertEquals

class AdvancementsDslTest {
     @Test
    fun test() {
        val child2 = advancement("child2") {
            display ("1")

            criteria {
                criterion("a") {
                    Criterion.impossible
                }
            }

            advancement("grandchild1") {
                display("a")

                criteria {
                    criterion("a") {
                        Criterion.impossible
                    }
                }
            }

            advancement("grandchild2") {
                display("skill1-1-1")

                criteria {
                    criterion("a") {
                        Criterion.impossible
                    }
                }
            }
        }

        val child3 = advancement("child3") {
            display("skill1-1")

            criteria {
                criterion("a") {
                    Criterion.impossible
                }
            }

            advancement("grandchild1") {
                display("skill1-1-1")

                criteria {
                    criterion("a") {
                        Criterion.impossible
                    }
                }
            }
        }

        val dsl = AdvancementsDSL("skill") {
            advancement {
                display("skill1") {
                    description = "desc"
                }

                criteria {
                    // criterion("a") { Criterion.impossible }
                }

                requirements {
                    arrayOf(arrayOf("a"))
                }

                advancement("child1") {
                    display("skill11-2")

                    criteria {
                        criterion("a") {
                            Criterion.placedBlock("minecraft:stone")
                        }
                    }
                }

                merge(child2)

                merge(child3)
            }
        }

        val datapackRootPath = File(".").resolve("./../data/void/datapacks/Skyblock").canonicalPath

        println(datapackRootPath)

        dsl.dumpJson(datapackRootPath)

        assertEquals(3, dsl.rootTree.children.size)
    }
}