package tech.wakame.skyblock

import com.sk89q.worldedit.bukkit.WorldEditPlugin
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.lang.Exception

class Skyblock : JavaPlugin() {
    private fun welcomeMessage(): Array<TextComponent> {
        return arrayOf(
                TextComponent("Skyblock v0.0.1").apply {
                    color = ChatColor.YELLOW
                    isBold = true
                },
                TextComponent("\n"),
                TextComponent("Git repo ").apply {
                    val link = TextComponent("here").apply {
                        isUnderlined = true
                        color = ChatColor.BLUE
                        clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/wakame-tech/Skyblock")
                    }
                    addExtra(link)
                },
                TextComponent("\n"),
                TextComponent("${Config.islands.size} islands loaded"),
                TextComponent("\n")
        )
    }

    override fun onEnable() { // Plugin startup logic
        val configFile = File(dataFolder, "config.yml")
        if (!configFile.exists()) {
            configFile.parentFile.mkdirs()
        }
        Config.load(configFile)
        saveConfig()
        config.setDefaults(Config.config)
        logger.info("config path: ${config.currentPath}")

        server.spigot().broadcast(*welcomeMessage())

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
        Config.save()
        logger.info("config path: ${config.currentPath}")
        saveConfig()
    }
}