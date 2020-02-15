package tech.wakame.skyblock

import com.sk89q.worldedit.bukkit.WorldEditPlugin
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Server
import org.bukkit.plugin.java.JavaPlugin
import tech.wakame.skyblock.advancements.SkillAdvancements
import java.lang.Exception
import java.util.logging.Logger

class Skyblock : JavaPlugin() {
    private fun welcomeMessage(): Array<TextComponent> {
        return arrayOf(
                TextComponent("Skyblock v$VERSION").apply {
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
        Skyblock.server = server

        // test
        // SkillAdvancements(dataFolder.resolve("../../void/datapacks/skyblock").absolutePath)
    }

    override fun onDisable() { // Plugin shutdown logic
        Config.save(config)
        saveConfig()
    }

    companion object {
        const val VERSION = "0.0.2"

        lateinit var logger: Logger
        lateinit var wePlugin: WorldEditPlugin
        lateinit var server: Server
    }
}