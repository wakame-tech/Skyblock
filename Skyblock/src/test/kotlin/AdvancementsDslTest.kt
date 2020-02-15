
import org.bukkit.Material
import org.junit.Test
import tech.wakame.skyblock.advancements.dsl.AdvancementsDSL
import tech.wakame.skyblock.advancements.dsl.advancement
import java.io.File
import kotlin.test.assertEquals

class AdvancementsDslTest {
    @Test
    fun test() {
        val child2 = advancement("child2") {
            display("skill1-1") {}

            advancement("grandchild1") {
                display("skill1-1-1") {}
            }

            advancement("grandchild2") {
                display("skill1-1-1") {}
            }
        }

        val child3 = advancement("child3") {
            display("skill1-1") {}

            advancement("grandchild1") {
                display("skill1-1-1") {}
            }
        }

        val dsl = AdvancementsDSL("skill") {
            advancement {
                display("skill1") {
                    description = "desc"
                    icon = Material.STONE
                }

                criteria {
                    "a" - impossible
                }

                requirements {
                    arrayOf(arrayOf("a"))
                }

                advancement("child1") {
                    display("skill11-2") {}
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