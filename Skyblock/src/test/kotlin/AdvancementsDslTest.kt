
import org.junit.Test
import tech.wakame.skyblock.skills.TestSkill1

class AdvancementsDslTest {
    @Test
    fun test() {
        TestSkill1.dumpJson("./../data/void/datapacks/skyblock")
    }
}