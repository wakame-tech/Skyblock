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
import java.io.File

@DslMarker
annotation class AdvancementMarker

@AdvancementMarker
class AdvancementsDSL(private val namespace: String, private val dsl: AdvancementContext.() -> Unit) {
    fun dump(): Nothing = TODO()

    init {
        val rootTree = AdvancementContext(null, namespace, "root").apply(dsl).build()

        // debug
        fun traverse(tree: AdvancementTree, depth: Int = 0) {
            println("\t".repeat(depth) + tree.self.display.title)
            if (tree.children.isEmpty()) {
                return
            }
            tree.children.forEach { traverse(it, depth + 1) }
        }

        traverse(rootTree)
    }
}

@AdvancementMarker
data class AdvancementDisplayBuilder(
        var icon: Material = Material.STONE,
        var title: String = "",
        var description: String = "",
        var frame: AdvancementDisplay.AdvancementFrame = AdvancementDisplay.AdvancementFrame.TASK,
        var showToast: Boolean = false,
        var announceChat: Boolean = false,
        var visibility: AdvancementVisibility = AdvancementVisibility.ALWAYS
) {
    fun build() = AdvancementDisplay(icon, title, description, frame, showToast, announceChat, visibility)
}

data class AdvancementTree(val self: Advancement, val children: List<AdvancementTree>)

@AdvancementMarker
class AdvancementContext(private val parent: Advancement?, private val namespace: String, private val key: String) {
    private val childrenContexts: MutableList<Pair<String, AdvancementContext.() -> Unit>> = mutableListOf()
    var display: AdvancementDisplay = DEFAULT_DISPLAY
    private var requirements: Array<Array<String>> = arrayOf(arrayOf())
    // var rewards: AdvancementRewards = ???
    private var criteria: Map<String, Criterion> = mapOf()

    fun display(title: String, init: AdvancementDisplayBuilder.() -> Unit) {
        display = AdvancementDisplayBuilder(title=title).also(init).build()
    }

    fun criteria(init: AdvancementCriteriaContext.() -> Unit) {
        criteria = AdvancementCriteriaContext().apply(init).build()
    }

    fun requirements(init: AdvancementRequirementsContext.() -> Array<Array<String>>) {
        requirements = AdvancementRequirementsContext().let(init)
    }

    fun advancement(key: String, init: AdvancementContext.() -> Unit) {
        childrenContexts += key to init
    }

    fun rewards(init: AdvancementsRewardContext.() -> Unit): Nothing = TODO()

    fun build(): AdvancementTree {
        val nameKey = NameKey(namespace, key)
        val self = Advancement(parent, nameKey, display)
        self.saveCriteriaRequirements(requirements)

        // lazy initialize
        val children = childrenContexts.map { (key, init) ->
            AdvancementContext(self, namespace, key).apply(init).build()
        }

        return AdvancementTree(self, children)
    }

    companion object {
        val DEFAULT_DISPLAY = AdvancementDisplay(Material.STONE, "root", "desc", AdvancementDisplay.AdvancementFrame.TASK, false, false, AdvancementVisibility.ALWAYS)
    }
}

@AdvancementMarker
class AdvancementRequirementsContext {
    private val requirements: Array<Array<String>> = arrayOf()

    fun build(): Array<Array<String>> = requirements
}

@AdvancementMarker
class AdvancementCriteriaContext {
    private val criteria: MutableMap<String, Criterion> = mutableMapOf()

    infix operator fun String.minus(criterion: Criterion) {
        criteria += this to criterion
    }

    // helper
    val impossible = Criterion(CriterionInstance {
        MinecraftKey("minecraft", "impossible")
    })


    fun build(): Map<String, Criterion> = criteria.toMap()
}

@AdvancementMarker
class AdvancementsRewardContext

class SkillAdvancements(private val datapackPath: String) {
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
        val file = File(datapackPath).resolve("./data/skill/advancements/root.json")
        Skyblock.logger.info(file.absolutePath)
        file.writeText(json)
    }

    init {
        AdvancementsDSL("skill") {
            advancement("root") {
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
                    display("skill1-1") {}

                    advancement("grandchild1") {
                        display("skill1-1-1") {}
                    }
                }

                advancement("child2") {
                    display("skill11-2") {}
                }
            }
        }
    }
}