package tech.wakame.skyblock

import java.util.Date
import com.sk89q.worldedit.bukkit.WorldEditPlugin
import de.slikey.effectlib.EffectManager
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import tech.wakame.skyblock.api.Island
import tech.wakame.skyblock.util.SkillManager
import tech.wakame.skyblock.util.broadcast
import tech.wakame.skyblock.util.colored
import java.lang.Exception
import java.text.SimpleDateFormat

class SkyBlock : JavaPlugin() {
    val skillManager: SkillManager = SkillManager(this)
    val effectManager: EffectManager = EffectManager(this)
    val islands: MutableMap<String, Island> = mutableMapOf()
    lateinit var dataPackPath: String

    override fun onEnable() {
        instance = this

        // bind WorldEdit
        val we = server.pluginManager.getPlugin("WorldEdit") as? WorldEditPlugin
        if (we == null) {
            logger.warning("need dependency WorldEdit")
            server.broadcast("red{need dependency WorldEdit}")
            throw Exception("need dependency WorldEdit")
        }
        wePlugin = we

        SkyBlockConfig.load(config)
        SkyBlockEventListener(this)
        SkyBlockCommands(this)

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

        val mainTitle = "yellow{SkyBlock(仮称)}".colored()
        val subTitle = "${description.version}(${SimpleDateFormat("yyyyMMdd").format(Date())})"

        server.onlinePlayers.forEach { player ->
            player.sendTitle(mainTitle, subTitle)
        }
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
                TextComponent("${islands.size} islands loaded"),
                TextComponent("\n")
        )
    }

    companion object {
        lateinit var wePlugin: WorldEditPlugin
        lateinit var instance: SkyBlock
    }
}