package tech.wakame.skyblock

import com.sk89q.worldedit.bukkit.WorldEditPlugin
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import tech.wakame.skyblock.skills.Palette
import tech.wakame.skyblock.skills.SkillManager
import tech.wakame.skyblock.skills.TestSkill1
import tech.wakame.skyblock.util.InventoryUI
import tech.wakame.skyblock.util.Option
import java.lang.Exception
import java.util.logging.Logger

class Skyblock : JavaPlugin() {
    override fun onEnable() { // Plugin startup logic
        Config.load(config)

        server.spigot().broadcast(*welcomeMessage())

        // event handler
        server.pluginManager.registerEvents(SkyblockEventListener(), this)

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

        Skyblock.logger = logger
        Skyblock.wePlugin = plugin
        Skyblock.server = server
        // datapacks/<datapack>
        Skyblock.dataPackRootPath = dataFolder.resolve("../../void/datapacks/skyblock").path

        val skillManager = SkillManager()
        skillManager.register(TestSkill1)
        skillManager.status()
    }

    override fun onDisable() { // Plugin shutdown logic
        Config.save(config)
        saveConfig()
    }

    companion object {
        const val VERSION = "0.0.2"
        const val SkyBlockSettingsName = "settings"

        lateinit var logger: Logger
        lateinit var wePlugin: WorldEditPlugin
        lateinit var server: Server
        lateinit var dataPackRootPath: String

        val commandPalettes: MutableMap<String, Palette> = mutableMapOf()

        fun welcomeMessage(): Array<TextComponent> {
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
    }
}