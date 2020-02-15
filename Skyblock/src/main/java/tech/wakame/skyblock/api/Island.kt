package tech.wakame.skyblock.api

import org.bukkit.Location
import org.bukkit.configuration.Configuration

data class Island(val name: String, val id: String, val location: Location) {
    companion object {
        fun fromConfig(config: Configuration, base: String): Island? {
            val name = config.getString("$base.name")
            val id = config.getString("$base.id")
            val location = config.getLocation("$base.location")
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