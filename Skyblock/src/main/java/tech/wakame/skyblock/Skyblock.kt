package tech.wakame.skyblock

import com.sk89q.worldedit.bukkit.WorldEditPlugin
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.plugin.java.JavaPlugin
import java.lang.Exception
import java.util.logging.Logger

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
        Config.load(config)

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

        Skyblock.logger = logger
        Skyblock.wePlugin = plugin
    }

    override fun onDisable() { // Plugin shutdown logic
        Config.save(config)
        saveConfig()
    }

    companion object {
        lateinit var logger: Logger
        lateinit var wePlugin: WorldEditPlugin
    }
}