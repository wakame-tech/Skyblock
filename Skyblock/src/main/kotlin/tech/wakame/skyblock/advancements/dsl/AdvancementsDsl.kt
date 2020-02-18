package tech.wakame.skyblock.advancements.dsl

import com.google.gson.*
import tech.wakame.skyblock.advancements.dsl.elements.Advancement
import tech.wakame.skyblock.advancements.dsl.elements.Criterion
import tech.wakame.skyblock.advancements.dsl.elements.Display
import tech.wakame.skyblock.advancements.dsl.elements.Rewards
import java.io.File

data class Key(val namespace: String, val key: String) {
    override fun toString() = "$namespace:$key"
}

@DslMarker
annotation class AdvancementMarker

@AdvancementMarker
class AdvancementsDSL(private val datapackName: String, private val namespace: String, private val treeName: String, init: AdvancementRoot.() -> AdvancementTree) {
    private var rootTree: AdvancementTree = init(AdvancementRoot(namespace, treeName))

    val name: String
    get() = treeName

    val advancementSerializer = JsonSerializer<Advancement> { src, _, context ->
        JsonObject().apply {
            addProperty("name", src.name.toString())
            if (src.display != null) {
                add("display", context.serialize(src.display))
            }
            if (src.requirements != null) {
                add("requirements", context.serialize(src.requirements))
            }
            if (src.rewards != null) {
                add("rewards", context.serialize(src.rewards))
            }
            add("criteria", context.serialize(src.criteria))
            if (src.parent != null) {
                addProperty("parent", src.parent.name.toString())
            }
        }
    }

    val criterionSerializer = JsonSerializer<Criterion> { src, _, context ->
        JsonObject().apply {
            addProperty("trigger", src.trigger.toString())
            if (src.conditions.isNotEmpty()) {
                add("conditions", context.serialize(src.conditions))
            }
        }
    }

    // dataPackRootPath includes datapack name
    fun dumpJson(dataPackRootPath: String) {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(Advancement::class.java, advancementSerializer)
        gsonBuilder.registerTypeAdapter(Criterion::class.java, criterionSerializer)
        gsonBuilder.setPrettyPrinting()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        val gson = gsonBuilder.create()

        println("[Tree $treeName]")
        rootTree.traverse { adv, _ ->
            val json = gson.toJson(adv)
            val folder = File(dataPackRootPath).resolve("./data/${namespace}/advancements/${treeName}")
            if (!folder.exists())
                folder.mkdirs()
            val key = adv.name.key.split("/").last()
            val file = folder.resolve("./${key}.json")
            file.writeText(json)
            println("[DUMP] ${adv.name} at ${file.toRelativeString(File(dataPackRootPath))}")
        }

        rootTree.traverse { adv, d ->
            println("  ".repeat(d) + adv.toString())
        }
    }
}

@AdvancementMarker
class AdvancementRoot(private val namespace: String, private val treeName: String) {
    fun root(init: AdvancementContext.() -> Unit): AdvancementTree {
        return AdvancementContext(null, namespace, treeName, "root").apply(init).build()
    }
}

fun advancement(key: String, init: AdvancementContext.() -> Unit): ChildContext {
    return key to init
}

data class AdvancementTree(val self: Advancement, val children: List<AdvancementTree>) {
    fun traverse(depth: Int = 0, action: (Advancement, Int) -> Unit) {
        action(self, depth)
        if (this.children.isEmpty()) {
            return
        }
        this.children.forEach {
            it.traverse(depth + 1, action)
        }
    }
}

typealias ChildContext = Pair<String, AdvancementContext.() -> Unit>

@AdvancementMarker
class AdvancementContext(private val parent: Advancement?, private val namespace: String, private val treeName: String, private val key: String) {
    private val children: MutableList<ChildContext> = mutableListOf()
    var display: Display? = Display()
    private var requirements: Array<Array<String>>? = null
    private var criteria: Map<String, Criterion> = mapOf()
    private var rewards: Rewards? = null

    fun display(title: String = key, init: Display.() -> Unit = {}) {
        display = Display(title = title).apply(init)
    }

    fun criteria(init: AdvancementCriteriaContext.() -> Unit) {
        criteria = AdvancementCriteriaContext().apply(init).build()
    }

    fun requirements(init: AdvancementRequirementsContext.() -> Array<Array<String>>) {
        requirements = AdvancementRequirementsContext().let(init)
    }

    fun advancement(key: String, init: AdvancementContext.() -> Unit) {
        children += key to init
    }

    fun merge(context: ChildContext) {
        children += context
    }

    fun rewards(init: Rewards.() -> Unit) {
        rewards = Rewards().apply(init)
    }

    fun build(): AdvancementTree {
        val name = Key(namespace, "${treeName}/$key")

        if (criteria.isEmpty()) {
            throw Exception("Advancement $name Criteria cannot be empty")
        }

        val self = Advancement(parent, name, display, requirements, criteria)

        // lazy initialize
        val children = children.map { (key, init) ->
            AdvancementContext(self, namespace, treeName, if (key == "") this.key else key).apply(init).build()
        }

        return AdvancementTree(self, children)
    }
}

@AdvancementMarker
class AdvancementRequirementsContext {
    private val requirements: Array<Array<String>> = arrayOf()

    fun build() = requirements
}

@AdvancementMarker
class AdvancementCriteriaContext {
    private val criteria: MutableMap<String, Criterion> = mutableMapOf()

    fun criterion(name: String, init: () -> Criterion) {
        criteria += name to init()
    }

    fun build() = criteria.toMap()
}
