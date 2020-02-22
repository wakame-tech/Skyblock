package tech.wakame.skyblock

import com.sk89q.worldedit.bukkit.WorldEditPlugin
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Server
import org.bukkit.plugin.java.JavaPlugin
import tech.wakame.skyblock.skills.SkillManager
import tech.wakame.skyblock.skills.FireSkill
import java.lang.Exception
import java.util.logging.Logger

class SkyBlock : JavaPlugin() {
    override fun onEnable() { // Plugin startup logic
        VERSION = description.version

        Config.load(config)

        server.spigot().broadcast(*welcomeMessage())

        SkyBlockEventListener(this)

        // command executor
        for ((name, executor) in Commands.commands) {
            getCommand(name)?.setExecutor(executor)
        }

        // bind WorldEdit
        val plugin = server.pluginManager.getPlugin("WorldEdit") as? WorldEditPlugin
        if (plugin == null) {
            logger.warning("need dependency WorldEdit")
            throw Exception("need dependency WorldEdit")
        }

        SkyBlock.logger = logger
        SkyBlock.wePlugin = plugin
        SkyBlock.server = server
        // datapacks/<datapack>
        SkyBlock.dataPackRootPath = dataFolder.resolve("../../void/datapacks/skyblock").path

        val skillManager = SkillManager()
        skillManager.register(FireSkill)
        skillManager.status()
    }

    override fun onDisable() { // Plugin shutdown logic
        Config.save(config)
        saveConfig()
    }

    companion object {
        lateinit var VERSION: String
        const val SkyBlockSettingsName = "settings"

        lateinit var logger: Logger
        lateinit var wePlugin: WorldEditPlugin
        lateinit var server: Server
        lateinit var dataPackRootPath: String

        fun welcomeMessage(): Array<TextComponent> {
            return arrayOf(
                    TextComponent("SkyBlock $VERSION").apply {
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
    }
}