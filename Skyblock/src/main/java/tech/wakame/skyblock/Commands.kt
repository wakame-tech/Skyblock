package tech.wakame.skyblock

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.bukkit.BukkitPlayer
import com.sk89q.worldedit.bukkit.WorldEditPlugin
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.regions.Region
import com.sk89q.worldedit.session.ClipboardHolder
import com.sk89q.worldedit.util.formatting.text.TextComponent
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File
import java.lang.Exception
import java.util.logging.Logger
import kotlin.math.roundToInt

class Island(val id: String, var name: String, var region: Region) {
    companion object {
        private val islands: MutableMap<String, Island> = mutableMapOf()

        fun register(id: String, region: Region) {
            islands[id] = Island(id, id, region)
            print("+ id: $id region: $region")
        }

        fun list(): String {
            var text = "  id   location   "
            islands.forEach { (id, island) ->
                val c = island.region.center
                val (x, y, z) = Triple(c.x.roundToInt(), c.y.roundToInt(), c.z.roundToInt())
                val location = TextComponent.builder("($x $y $z)")
                location.clickEvent(ClickEvent.runCommand("tp @p $x $y $z"))
                text += "$id: $location\n"
            }
            return text
        }
    }
}

object Commands {
    lateinit var wePlugin: WorldEditPlugin
    lateinit var logger: Logger
    val commands: Map<String, CommandExecutor> = mapOf(
            "island" to CommandExecutor(::island),
            "islands" to CommandExecutor(::islands)
    )

    fun Clipboard.paste(by: BukkitPlayer) {
        val world = BukkitAdapter.adapt(by.world)
        // https://www.spigotmc.org/threads/1-13-load-paste-schematics-with-the-worldedit-api-simplified.357335/
        val es = wePlugin.worldEdit.editSessionFactory.getEditSession(by.world, -1)
        val op = ClipboardHolder(this)
                .createPaste(es)
                .to(this.origin)
                .ignoreAirBlocks(true)
                .build()

        try {
            Operations.complete(op)
            es.flushSession()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun readSchematic(schematicName: String): Clipboard? {
        val path = "${wePlugin.dataFolder}/schematics/${schematicName}.schem"
        logger.info(path)
        val file = File(path)
        if (!file.exists()) {
            return null
        }
        val format = ClipboardFormats.findByFile(file)
        val reader = format?.getReader(file.inputStream())
        return reader?.read()
    }

    /*
    *  Usage
    *   /island save <id>
    *   /island load <id>
    * */
    private fun island(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        if (args.isEmpty() || args[1] !in listOf("save", "load") || args.size < 2) {
            sender.sendMessage("/islands [save/load] <id>")
            return false
        }

        // schematic save sample
        /* 1. select first and second pos
        *  2. //copy
        *  3. //schem save [-f] <id>
        */
        // schematic load sample
        val player = wePlugin.wrapPlayer(sender)
        val id = args[0]
        val clipboard = readSchematic(id) ?: run {
            sender.sendMessage("${ChatColor.RED}schematic not found")
            return false
        }

        Island.register(id, clipboard.region)

        try {
            clipboard.paste(player)
            sender.sendMessage("complete operation")
        } catch (e: Exception) {
            sender.sendMessage("${ChatColor.RED}internal error")
        }

        return true
    }

    private fun islands(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        sender.sendMessage(Island.list())
        return true
    }
}