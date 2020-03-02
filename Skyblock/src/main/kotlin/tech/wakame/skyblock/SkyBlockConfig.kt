package tech.wakame.skyblock

import org.bukkit.configuration.Configuration
import tech.wakame.skyblock.api.Island


object SkyBlockConfig {
    fun save(config: Configuration) {
        SkyBlock.instance.islands.forEach { (_, island) ->
            Island.save(config, island)
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
            SkyBlock.instance.islands[id] = island
        }
    }

    fun addIsland(island: Island) {
        SkyBlock.instance.islands[island.id] = island
    }
}