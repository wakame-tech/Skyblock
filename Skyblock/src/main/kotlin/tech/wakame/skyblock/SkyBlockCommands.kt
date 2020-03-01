package tech.wakame.skyblock

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tech.wakame.skyblock.SkyBlock.Companion.wePlugin
import tech.wakame.skyblock.api.*
import tech.wakame.skyblock.util.Palette
import tech.wakame.skyblock.util.IUI
import tech.wakame.skyblock.util.colored
import tech.wakame.skyblock.util.uuid
import java.lang.Exception
import kotlin.math.roundToInt

class SkyBlockCommands(private val plugin: SkyBlock) {
    init {
        val commands = mapOf(
                "island" to CommandExecutor(::island),
                "islands" to CommandExecutor(::islands),
                "palette" to CommandExecutor(::palette),
                "catalog" to CommandExecutor(::catalog),
                "genskill" to CommandExecutor(::generateAdvancements)
        )

        // command executor
        for ((name, executor) in commands) {
            plugin.getCommand(name)?.setExecutor(executor)
        }
    }

    private fun generateAdvancements(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        plugin.skillManager.generateAllSkills(sender)
        return true
    }

    private fun palette(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        Palette.store.getOrPut(sender.uuid) { Palette() }.also {
            it.open(sender)
        }
        return true
    }

    private fun catalog(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        IUI.catalog.open(sender)
        return true
    }

    /*
    *  Usage
    *   /island save <id> [as <name>]
    *   /island load <id>
    * */
    private fun island(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        sender.sendMessage("args: ${args.joinToString(",")}")
        if (sender !is Player) return false
        if (args.isEmpty() || args[0] !in listOf("save", "load") || args.size < 2) {
            sender.sendMessage("/islands [save/load] <id>")
            return false
        }

        val player = wePlugin.wrapPlayer(sender)
        val id = args[1]


        when (args[0]) {
            "save" -> {
                val clipboard = player.selectRegion()
                val location = sender.location
                val name = if (args.size >= 4) args[3] else id

                if (clipboard == null) {
                    sender.sendMessage("please select 2 positions")
                    return false
                }

                try {
                    saveSchematic(clipboard, player.editSession(), id)
                    SkyBlockConfig.addIsland(Island(name, id, location))
                    sender.sendMessage("new island $name($id) registered!")
                    sender.sendMessage("complete operation")
                } catch (e: Exception) {
                    sender.sendMessage("${ChatColor.RED}internal error")
                    e.printStackTrace()
                    return false
                }
            }
            "load" -> {
                if (!SkyBlockConfig.islands.containsKey(id)) {
                    sender.sendMessage("island $id not found")
                    return false
                }
                // schematic load sample
                val clipboard = readSchematic(id) ?: run {
                    sender.sendMessage("${ChatColor.RED}schematic not found")
                    return false
                }

                player.paste(clipboard)
                sender.sendMessage("complete operation")
            }
        }
        return true
    }

    private fun islands(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        fun Location.round() = Triple(this.x.roundToInt(), this.y.roundToInt(), this.z.roundToInt())

        val message = SkyBlockConfig.islands.map { (id, island) ->
            val (x, y, z) = island.location.round()
            TextComponent().apply {
                addExtra(TextComponent("%-10s".format("${island.name}($id)")).apply {
                    isBold = true
                })
                addExtra(TextComponent("%-15s".format("($x, $y, $z)")).apply {
                    hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(TextComponent("クリックしてテレポート")))
                    clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp @p $x $y $z")
                    color = ChatColor.YELLOW
                })
                addExtra("\n")
            }
        }.toTypedArray()
        sender.spigot().sendMessage(*message)
        return true
    }
}