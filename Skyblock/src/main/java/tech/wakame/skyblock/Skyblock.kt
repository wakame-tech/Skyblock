package tech.wakame.skyblock

import org.bukkit.plugin.java.JavaPlugin

class Skyblock : JavaPlugin() {
    override fun onEnable() { // Plugin startup logic
        logger.info("Skyblock v0.0.1")
    }

    override fun onDisable() { // Plugin shutdown logic
    }
}