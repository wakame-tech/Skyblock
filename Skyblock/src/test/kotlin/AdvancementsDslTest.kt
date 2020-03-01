
import org.junit.Test
import tech.wakame.skyblock.resources.skills.FireSkill

class AdvancementsDslTest {
    @Test
    fun test() {
        FireSkill.dumpJson("./../data/void/datapacks/skyblock")
    }
}