package tech.wakame.skyblock

import com.sk89q.worldedit.bukkit.BukkitPlayer
import com.sk89q.worldedit.bukkit.WorldEditPlugin
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat
import com.sk89q.worldedit.function.operation.ForwardExtentCopy
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.session.ClipboardHolder
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File
import java.lang.Exception
import java.util.logging.Logger
import kotlin.math.roundToInt

object Commands {
    lateinit var wePlugin: WorldEditPlugin
    lateinit var logger: Logger
    val commands: Map<String, CommandExecutor> = mapOf(
            "island" to CommandExecutor(::island),
            "islands" to CommandExecutor(::islands)
    )

    private fun Clipboard.paste(by: BukkitPlayer) {
        // https://www.spigotmc.org/threads/1-13-load-paste-schematics-with-the-worldedit-api-simplified.357335/
        val es = wePlugin.worldEdit.editSessionFactory.getEditSession(by.world, -1)
        val op = ClipboardHolder(this)
                .createPaste(es)
                .to(this.origin)
                .ignoreAirBlocks(true)
                .build()

        try {
            Operations.complete(op)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            es.flushSession()
        }
    }

    private fun copyRegion(id: String, by: BukkitPlayer) {
        val selection = wePlugin.getSession(by.player).getSelection(by.world)
        if (selection == null) {
            by.player.sendMessage("please select 2 positions")
            throw Exception("please select 2 positions")
        }

        val region = CuboidRegion(selection.minimumPoint, selection.maximumPoint)
        logger.info("create copy ${region.width} x ${region.height} x ${region.length}")
        val baClipboard = BlockArrayClipboard(region)
        val es = wePlugin.worldEdit.editSessionFactory.getEditSession(by.world, -1)

        try {
            val forwardExtentCopy = ForwardExtentCopy(es, region, baClipboard, region.minimumPoint)
            forwardExtentCopy.isCopyingEntities = true
            Operations.complete(forwardExtentCopy)

            val path = "${wePlugin.dataFolder}/schematics/${id}.schem"
            val file = File(path)
            logger.info("${baClipboard.region}")
            BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(file.outputStream()).use {
                it.write(baClipboard)
            }
        } catch (e: Exception) {
            throw e
        } finally {
            es.flushSession()
        }
    }

    private fun readSchematic(schematicName: String): Clipboard? {
        val path = "${wePlugin.dataFolder}/schematics/${schematicName}.schem"
        logger.info(path)
        val file = File(path)
        if (!file.exists()) {
            return null
        }

        return BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(file.inputStream()).use {
            it.read()
        }
    }

    /*
    *  Usage
    *   /island save <id>
    *   /island load <id>
    * */
    private fun island(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        if (args.isEmpty() || args[0] !in listOf("save", "load") || args.size < 2) {
            sender.sendMessage("/islands [save/load] <id>")
            return false
        }

        val player = wePlugin.wrapPlayer(sender)
        val id = args[1]

        try {
            when (args[0]) {
                "save" -> {
                    // schematic save sample
                    /* 1. select first and second pos
                    *  2. //copy
                    *  3. //schem save [-f] <id>
                    */
                    val region = copyRegion(id, player)
                    val location = sender.location
                    Config.addIsland(Island(id, id, location))
                    sender.sendMessage("new island $id registered!")
                    sender.sendMessage("complete operation")
                }
                "load" -> {
                    if (!Config.islands.containsKey(id)) {
                        sender.sendMessage("island $id not found")
                        return false
                    }
                    // schematic load sample
                    val clipboard = readSchematic(id) ?: run {
                        sender.sendMessage("${ChatColor.RED}schematic not found")
                        return false
                    }
                    clipboard.paste(player)
                    sender.sendMessage("complete operation")
                }
            }
            return true
        } catch (e: Exception) {
            sender.sendMessage("${ChatColor.RED}internal error")
            e.printStackTrace()
            return false
        }
    }

    private fun islands(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        fun Location.round() = Triple(this.x.roundToInt(), this.y.roundToInt(), this.z.roundToInt())

        val message = Config.islands.map { (id, island) ->
            val (x, y, z) = island.location.round()
            TextComponent().apply {
                addExtra(TextComponent("%-10s".format("${island.name}($id)")).apply {
                    isBold = true
                })
                addExtra(TextComponent("%-15s".format("($x, $y, $z)")).apply {
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