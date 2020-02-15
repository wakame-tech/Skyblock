package tech.wakame.skyblock.advancements

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import eu.endercentral.crazy_advancements.Advancement
import eu.endercentral.crazy_advancements.AdvancementDisplay
import eu.endercentral.crazy_advancements.AdvancementVisibility
import eu.endercentral.crazy_advancements.NameKey
import eu.endercentral.crazy_advancements.manager.AdvancementManager
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.server.v1_15_R1.Criterion
import net.minecraft.server.v1_15_R1.CriterionInstance
import net.minecraft.server.v1_15_R1.MinecraftKey
import org.bukkit.Material
import tech.wakame.skyblock.Skyblock
import tech.wakame.skyblock.advancements.dsl.AdvancementsDSL
import tech.wakame.skyblock.advancements.dsl.advancement
import java.io.File

class SkillAdvancements(private val datapackRootPath: String) {
    private fun test() {
        val manager = AdvancementManager.getNewAdvancementManager()
        val rootDisplay = AdvancementDisplay(Material.STONE, "root", "desc", AdvancementDisplay.AdvancementFrame.TASK, false, false, AdvancementVisibility.ALWAYS)
        val root = Advancement(null, NameKey("custom", "root"), rootDisplay)

        val childDisplay = AdvancementDisplay(Material.STONE, "child", "desc", AdvancementDisplay.AdvancementFrame.TASK, false, false, AdvancementVisibility.ALWAYS)
        val child = Advancement(root, NameKey("custom", "child"), childDisplay)

        val criteria: Criterion = Criterion(CriterionInstance {
            MinecraftKey("minecraft", "impossible")
        })
        root.saveCriteria(mapOf("criteria1" to criteria))
        root.saveCriteriaRequirements(arrayOf(arrayOf("criteria1")))

        val json = root.advancementJSON.let {
            GsonBuilder().setPrettyPrinting().create().toJson(JsonParser().parse(it))
        }

        Skyblock.logger.info("==== advancement json ====")
        Skyblock.logger.info(json)
        Skyblock.server.spigot().broadcast(TextComponent(json))

        // dump test
        val file = File(datapackRootPath).resolve("./data/skill/advancements/root.json")
        Skyblock.logger.info(file.absolutePath)
    }

    init {
        val sub = advancement("a") {
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

                merge(sub)

                advancement("child2") {
                    display("skill11-2") {}
                }
            }
        }

        dsl.dumpJson(datapackRootPath)
    }
}