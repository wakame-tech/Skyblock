package tech.wakame.skyblock

import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard
import com.sk89q.worldedit.regions.CuboidRegion
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tech.wakame.skyblock.SkyBlock.Companion.wePlugin
import tech.wakame.skyblock.api.*
import tech.wakame.skyblock.util.*
import java.lang.Exception

class SkyBlockCommands(private val plugin: SkyBlock) {
    init {
        val commands = mapOf(
                "island" to CommandExecutor(::island),
                "islands" to CommandExecutor(::islands),
                "palette" to CommandExecutor(::palette),
                "catalog" to CommandExecutor(::catalog),
                "genskill" to CommandExecutor(::generateAdvancements),
                "setportal" to CommandExecutor(::setPortalPoint)
        )

        // command executor
        for ((name, executor) in commands) {
            plugin.getCommand(name)?.setExecutor(executor)
        }
    }

    private fun setPortalPoint(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        if (args.size < 3) {
            sender.sendMessage("/setportal <x> <y> <z>")
            return false
        }
        val (x, y, z) = args.map { it.toInt() }
        val block = sender.world.getBlockAt(x, y, z)
        if (block.type != Material.END_PORTAL_FRAME) {
            sender.sendMessage("portal must be END_PORTAL_FRAME")
            return false
        }
        val (key, island) = Island.whereWithin(block.location) ?: run {
            sender.sendMessage("island not found")
            return false
        }

        SkyBlock.instance.islands[key] = island.copy(portalLocation = block.location)

        sender.sendMessage("has been set to $key's portal")

        return true
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
        if (sender !is Player) return false
        fun help(): Boolean {
            sender.sendMessage("/islands (save/load/saveall/loadall) [id]")
            return false
        }

        if (args.isEmpty() || args[0] !in listOf("save", "load", "saveall", "loadall")) {
            return help()
        }

        val player = wePlugin.wrapPlayer(sender)

        when (args[0]) {
            "save" -> {
                if (args.size < 2) {
                    return help()
                }

                val clipboard = player.selectRegion()
                val location = sender.location
                val id = args[1]
                val name = if (args.size >= 4) args[3] else id

                if (clipboard == null) {
                    sender.sendMessage("please select 2 positions")
                    return false
                }

                try {
                    saveSchematic(clipboard, player.editSession(), id)
                    val cr = CuboidRegion(clipboard.region.minimumPoint, clipboard.region.maximumPoint)
                    SkyBlockConfig.addIsland(Island(cr, name, id, location, location))
                    sender.sendMessage("new island blue{$name}($id) registered!".colored())
                    sender.sendMessage("complete operation")
                } catch (e: Exception) {
                    sender.sendMessage("red{internal error}".colored())
                    e.printStackTrace()
                    return false
                }
            }
            "saveall" -> {
                sender.sendMessage("try to save bold{yellow{${plugin.islands.size}}} islands".colored())

                plugin.islands.forEach { (id, island) ->
                    sender.sendMessage("saving bold{yellow{${island}}}".colored())
                    val clipboard = BlockArrayClipboard(island.region)
                    saveSchematic(clipboard, player.editSession(), id)
                    sender.sendMessage("saved bold{yellow{${island}}}!".colored())
                }
            }
            "load" -> {
                if (args.size < 2) {
                    return help()
                }

                val id = args[1]
                if (!SkyBlock.instance.islands.containsKey(id)) {
                    sender.sendMessage("island blue{$id} not found".colored())
                    return false
                }
                // schematic load sample
                val clipboard = readSchematic(id) ?: run {
                    sender.sendMessage("red{schematic not found}".colored())
                    return false
                }

                player.paste(clipboard)
                sender.sendMessage("bold{complete operation}".colored())
            }
            "loadall" -> {
                sender.sendMessage("try to load bold{yellow{${plugin.islands.size}}} islands".colored())

                plugin.islands.forEach { (id, island) ->
                    sender.sendMessage("loading bold{yellow{${island}}}".colored())
                    val clipboard = readSchematic(id)
                    if (clipboard == null) {
                        sender.sendMessage("red{failed to read $id}".colored())
                    } else {
                        player.paste(clipboard)
                        sender.sendMessage("loaded bold{yellow{${island}}}!".colored())
                    }
                }
            }
        }
        return true
    }

    private fun islands(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        val message = SkyBlock.instance.islands.values.map { island ->
            val (x, y, z) = island.location.blockCoords()
            TextComponent().apply {
                addExtra(TextComponent("%-10s".format("$island")))
                addExtra(TextComponent("%-15s".format("yellow{tp here}".colored())).apply {
                    hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(TextComponent("クリックしてテレポート")))
                    clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp @p $x $y $z")
                })
                addExtra("\n")
            }
        }.toTypedArray()
        sender.spigot().sendMessage(*message)
        return true
    }
}