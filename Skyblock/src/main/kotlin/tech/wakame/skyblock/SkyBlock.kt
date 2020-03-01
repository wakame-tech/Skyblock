package tech.wakame.skyblock

import com.sk89q.worldedit.bukkit.WorldEditPlugin
import de.slikey.effectlib.EffectManager
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import tech.wakame.skyblock.util.SkillManager
import tech.wakame.skyblock.util.broadcast
import java.lang.Exception

class SkyBlock : JavaPlugin() {
    val skillManager: SkillManager = SkillManager(this)
    val effectManager: EffectManager = EffectManager(this)
    lateinit var dataPackPath: String

    override fun onEnable() { // Plugin startup logic
        SkyBlockConfig.load(config)
        SkyBlockEventListener(this)
        SkyBlockCommands(this)

        // bind WorldEdit
        val plugin = server.pluginManager.getPlugin("WorldEdit") as? WorldEditPlugin
        if (plugin == null) {
            logger.warning("need dependency WorldEdit")
            server.broadcast("red{need dependency WorldEdit}")
            throw Exception("need dependency WorldEdit")
        }

        skillManager.status()
        wePlugin = plugin
        instance = this

        // datapacks/<datapack>
        val dataPackFolder = dataFolder.resolve("../../${server.worlds[0].name}/datapacks")
        if (!dataPackFolder.exists()) {
            logger.warning("datapack folder not found")
            server.broadcast("red{datapack folder not found}")
            throw Exception("datapack folder not found")
        }

        dataPackPath = dataPackFolder.resolve("./skyblock").canonicalPath
        server.broadcast("Datapack Path: $dataPackPath")

        server.spigot().broadcast(*welcomeMessage())

        skillManager.status()
    }

    override fun onDisable() { // Plugin shutdown logic
        SkyBlockConfig.save(config)
        saveConfig()
        HandlerList.unregisterAll(this)
        effectManager.dispose()
    }

    private fun welcomeMessage(): Array<TextComponent> {
        return arrayOf(
                TextComponent("SkyBlock ${description.version}").apply {
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
                TextComponent("${SkyBlockConfig.islands.size} islands loaded"),
                TextComponent("\n")
        )
    }

    companion object {
        lateinit var wePlugin: WorldEditPlugin
        lateinit var instance: SkyBlock
    }
}