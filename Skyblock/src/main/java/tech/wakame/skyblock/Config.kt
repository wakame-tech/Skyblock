package tech.wakame.skyblock

import org.bukkit.Location
import org.bukkit.configuration.Configuration
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

data class Island(val name: String, val id: String, val location: Location) {
    companion object {
        fun fromConfig(section: ConfigurationSection, base: String): Island? {
            val name = section.getString("$base.name")
            val id = section.getString("$base.id")
            val location = section.getLocation("$base.location")
            if (name == null || id == null || location == null) {
                return null
            }
            return Island(name, id, location)
        }
    }

    override fun toString(): String {
        return "$name($id): $location"
    }
}

object Config {
    lateinit var config: Configuration
    val islands: MutableMap<String, Island> = mutableMapOf()

    fun save() {
        config.set("version", "0.0.1")
        islands.forEach { (id, island) ->
            val path = "islands.$id"
            config.createSection(path)
            config.set("$path.name", island.name)
            config.set("$path.id", island.id)
            config.set("$path.location", island.location)
        }
    }

    fun load(configFile: File) {
        config = YamlConfiguration.loadConfiguration(configFile)

        if (!config.contains("islands")) {
            config.createSection("islands")
        }

        val ids = config.getConfigurationSection("islands")!!.getKeys(false)

        for (id in ids) {
            val path = "islands.$id"
            val section = config.getConfigurationSection(path) ?: continue
            val island = Island.fromConfig(section, path) ?: continue
            islands[id] = island
            print("$path read $island")
        }
    }

    fun addIsland(island: Island) {
        islands[island.id] = island
    }
}