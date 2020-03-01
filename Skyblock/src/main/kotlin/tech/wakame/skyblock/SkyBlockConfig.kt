package tech.wakame.skyblock

import org.bukkit.configuration.Configuration
import tech.wakame.skyblock.api.Island


object SkyBlockConfig {
    val islands: MutableMap<String, Island> = mutableMapOf()

    fun save(config: Configuration) {
        islands.forEach { (id, island) ->
            val path = "islands.$id"
            config.createSection(path)
            config.set("$path.name", island.name)
            config.set("$path.id", island.id)
            config.set("$path.location", island.location)
        }
    }

    fun load(config: Configuration) {
        if (!config.contains("islands")) {
            config.createSection("islands")
        }

        val ids = config.getConfigurationSection("islands")!!.getKeys(false)

        for (id in ids) {
            val path = "islands.$id"
            val island = Island.fromConfig(config, path) ?: continue
            islands[id] = island
        }
    }

    fun addIsland(island: Island) {
        islands[island.id] = island
    }
}