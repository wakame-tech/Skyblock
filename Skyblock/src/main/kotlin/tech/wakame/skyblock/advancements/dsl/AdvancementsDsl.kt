package tech.wakame.skyblock.advancements.dsl

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import eu.endercentral.crazy_advancements.Advancement
import eu.endercentral.crazy_advancements.AdvancementDisplay
import eu.endercentral.crazy_advancements.AdvancementVisibility
import eu.endercentral.crazy_advancements.NameKey
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
class AdvancementsDSL(private val namespace: String, init: AdvancementRoot.() -> AdvancementTree) {
    var rootTree: AdvancementTree = init(AdvancementRoot(namespace))

    fun dumpJson(datapackRootPath: String) {
        // dry run
        rootTree.traverse { adv, _ ->
            val json = adv.self.advancementJSON.let {
                GsonBuilder().setPrettyPrinting().create().toJson(JsonParser().parse(it))
            }
            val key = adv.self.name.key
            val file = File(datapackRootPath).resolve("./data/$namespace/advancements/$key.json")

            file.printWriter().use {
                it.print(json)
            }

            // Skyblock.logger.info("${adv.self.name} dumped ${file.canonicalPath}")
        }

        // message
//        Skyblock.server.spigot().broadcast(TextComponent("namespace: $namespace"))
        rootTree.traverse { adv, d ->
            // Skyblock.server.spigot().broadcast(TextComponent("  ".repeat(d) + adv.toString()))
            println("  ".repeat(d) + adv.toString())
        }
    }
}

@AdvancementMarker
class AdvancementRoot(private val namespace: String) {
    fun advancement(init: AdvancementContext.() -> Unit): AdvancementTree {
        return AdvancementContext(null, namespace, "root").apply(init).build()
    }
}

fun advancement(key: String, init: AdvancementContext.() -> Unit): ChildContext {
    return key to init
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

data class AdvancementTree(val self: Advancement, val children: List<AdvancementTree>) {
    fun traverse(depth: Int = 0, action: (AdvancementTree, Int) -> Unit) {
        action(this, depth)
        if (this.children.isEmpty()) {
            return
        }
        this.children.forEach {
            it.traverse(depth + 1, action)
        }
    }

    override fun toString(): String {
        return "${self.name.key} parent: ${self.parent?.name?.key ?: "---"}"
    }
}

typealias ChildContext = Pair<String, AdvancementContext.() -> Unit>

@AdvancementMarker
class AdvancementContext(private val parent: Advancement?, private val namespace: String, private val key: String) {
    private val childContexts: MutableList<ChildContext> = mutableListOf()
    var display: AdvancementDisplay = DEFAULT_DISPLAY
    private var requirements: Array<Array<String>> = arrayOf(arrayOf())
    // var rewards: AdvancementRewards = ???
    private var criteria: Map<String, Criterion> = mapOf()

    fun display(title: String, init: AdvancementDisplayBuilder.() -> Unit) {
        display = AdvancementDisplayBuilder(title = title).also(init).build()
    }

    fun criteria(init: AdvancementCriteriaContext.() -> Unit) {
        criteria = AdvancementCriteriaContext().apply(init).build()
    }

    fun requirements(init: AdvancementRequirementsContext.() -> Array<Array<String>>) {
        requirements = AdvancementRequirementsContext().let(init)
    }

    fun advancement(key: String, init: AdvancementContext.() -> Unit) {
        childContexts += key to init
    }

    fun merge(context: ChildContext) {
        childContexts += context
    }

    fun rewards(init: AdvancementsRewardContext.() -> Unit): Nothing = TODO()

    fun build(): AdvancementTree {
        val nameKey = NameKey(namespace, key)
        val self = Advancement(parent, nameKey, display)
        self.saveCriteriaRequirements(requirements)

        // lazy initialize
        val children = childContexts.map { (key, init) ->
            AdvancementContext(self, namespace, if (key == "") this.key else key).apply(init).build()
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