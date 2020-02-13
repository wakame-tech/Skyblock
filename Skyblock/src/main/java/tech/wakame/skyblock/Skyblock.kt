package tech.wakame.skyblock

import com.sk89q.worldedit.bukkit.WorldEditPlugin
import org.bukkit.plugin.java.JavaPlugin
import java.lang.Exception

class Skyblock : JavaPlugin() {
    override fun onEnable() { // Plugin startup logic
        logger.info("Skyblock v0.0.1")
        for ((name, executor) in Commands.commands) {
            getCommand(name)?.setExecutor(executor)
        }

        // bind WorldEdit
        val plugin = server.pluginManager.getPlugin("WorldEdit") as? WorldEditPlugin
        if (plugin == null) {
            logger.warning("need dependency WorldEdit")
            throw Exception("need dependency WorldEdit")
        }
        Commands.wePlugin = plugin
        Commands.logger = logger
    }

    override fun onDisable() { // Plugin shutdown logic
    }
}