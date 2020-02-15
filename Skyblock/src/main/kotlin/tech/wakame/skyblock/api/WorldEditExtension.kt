package tech.wakame.skyblock.api

import com.sk89q.worldedit.EditSession
import com.sk89q.worldedit.bukkit.BukkitPlayer
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat
import com.sk89q.worldedit.function.operation.ForwardExtentCopy
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.regions.Region
import com.sk89q.worldedit.session.ClipboardHolder
import tech.wakame.skyblock.Skyblock
import java.io.File
import kotlin.Exception

fun BukkitPlayer.paste(clipboard: Clipboard) {
    // https://www.spigotmc.org/threads/1-13-load-paste-schematics-with-the-worldedit-api-simplified.357335/
    val es = Skyblock.wePlugin.worldEdit.editSessionFactory.getEditSession(this.world, -1)
    val op = ClipboardHolder(clipboard)
            .createPaste(es)
            .to(clipboard.origin)
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

// == //copy
fun BukkitPlayer.selectRegion(): BlockArrayClipboard? {
    val selection: Region = Skyblock.wePlugin.getSession(this.player).getSelection(this.world) ?: return null
    val region = CuboidRegion(selection.minimumPoint, selection.maximumPoint)
    return BlockArrayClipboard(region)
}

fun BukkitPlayer.editSession(): EditSession {
    return Skyblock.wePlugin.worldEdit.editSessionFactory.getEditSession(this.world, -1)
}

// == //schem save <id>
fun saveSchematic(clipboard: BlockArrayClipboard, editSession: EditSession, id: String) {
    try {
        val forwardExtentCopy = ForwardExtentCopy(editSession, clipboard.region, clipboard, clipboard.region.minimumPoint)
        forwardExtentCopy.isCopyingEntities = true
        Operations.complete(forwardExtentCopy)

        val path = "${Skyblock.wePlugin.dataFolder}/schematics/${id}.schem"
        val file = File(path)
        BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(file.outputStream()).use {
            it.write(clipboard)
        }
    } catch (e: Exception) {
        throw e
    } finally {
        // don't forget
        editSession.flushSession()
    }
}

fun readSchematic(id: String): Clipboard? {
    val path = "${Skyblock.wePlugin.dataFolder}/schematics/${id}.schem"
    val file = File(path)
    if (!file.exists()) {
        return null
    }

    return BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(file.inputStream()).use {
        it.read()
    }
}