package tech.wakame.skyblock

import com.boydti.fawe.`object`.FawePlayer
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitWorld
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.lang.Exception

class Skyblock : JavaPlugin() {
    fun islandsExecutor(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        return try {
            val player = FawePlayer.wrap<Player>(sender)
//          val selection = player.selection
//          val (max, min) = selection.maximumPoint to selection.minimumPoint
//          sender.sendMessage("$max $min")
            true
        } catch (e: Exception) {
            logger.warning("Dependency not found.")
            false
        }
    }

    override fun onEnable() { // Plugin startup logic
        logger.info("Skyblock v0.0.1")
        getCommand("islands")?.setExecutor(this::islandsExecutor)
    }

    override fun onDisable() { // Plugin shutdown logic
    }
}