package tech.wakame.skyblock

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.bukkit.WorldEditPlugin
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.regions.Region
import com.sk89q.worldedit.session.ClipboardHolder
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File
import java.lang.Exception

class Skyblock : JavaPlugin() {
    lateinit var wePlugin: WorldEditPlugin
    private val islands: MutableMap<String, Island> = mutableMapOf()
    data class Island(val id: String, var name: String, var region: Region)

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

    private fun islandExecutor(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        if (args.isEmpty()) {
            sender.sendMessage("/islands <id>")
            return false
        }

        // schematic save sample
        /* 1. select first and second pos
        *  2. //copy
        *  3. //schem save [-f] <id>
        */

        // schematic load sample
        val player = wePlugin.wrapPlayer(sender)
        val world = BukkitAdapter.adapt(sender.world)
        val id = args[0]
        val clipboard = readSchematic(id) ?: run {
            sender.sendMessage("${ChatColor.RED}schematic not found")
            return false
        }
        sender.sendMessage("+ id: ${id} region: ${clipboard.region}")
        islands[id] = Island(id, id, clipboard.region)
        // https://www.spigotmc.org/threads/1-13-load-paste-schematics-with-the-worldedit-api-simplified.357335/
        val es = wePlugin.worldEdit.editSessionFactory.getEditSession(world, -1)
        val op = ClipboardHolder(clipboard)
                .createPaste(es)
                .to(clipboard.origin)
                .ignoreAirBlocks(true)
                .build()

        try {
            Operations.complete(op)
            es.flushSession()
            sender.sendMessage("complete operation")
        } catch (e: Exception) {
            sender.sendMessage("${ChatColor.RED}internal error")
            e.printStackTrace()
        }
        return true
    }

    private fun islandsExecutor(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        val message = islands.map { (id, island) ->
            "$id: ${island.region}"
        }
        sender.sendMessage((listOf("id  region") + message).toTypedArray())
        return true
    }

    override fun onEnable() { // Plugin startup logic
        logger.info("Skyblock v0.0.1")
        getCommand("island")?.setExecutor(this::islandExecutor)
        getCommand("islands")?.setExecutor(this::islandsExecutor)

        // bind WorldEdit
        val plugin = server.pluginManager.getPlugin("WorldEdit") as? WorldEditPlugin
        if (plugin == null) {
            logger.warning("need dependency WorldEdit")
            throw Exception("need dependency WorldEdit")
        }
        wePlugin = plugin
    }

    override fun onDisable() { // Plugin shutdown logic
    }
}