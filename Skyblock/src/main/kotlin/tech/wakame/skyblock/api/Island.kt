package tech.wakame.skyblock.api

import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.regions.CuboidRegion
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.Configuration
import org.bukkit.entity.Player
import tech.wakame.skyblock.SkyBlock
import tech.wakame.skyblock.util.colored
import tech.wakame.skyblock.util.filledEnderEye
import tech.wakame.skyblock.util.simple
import tech.wakame.skyblock.util.toBlockVector3

/**
 *  [location] テレポートする地点
 *  [potalLocation] エンダーポータルの位置
 */
data class Island(val region: CuboidRegion, val name: String, val id: String, val location: Location, val portalLocation: Location) {
    companion object {
        fun fromConfig(config: Configuration, base: String): Island? {
            fun Configuration.getBlockVector3(path: String): BlockVector3? {
                val x = getInt("$path.x") ?: return null
                val y = getInt("$path.y") ?: return null
                val z = getInt("$path.z") ?: return null
                return BlockVector3.at(x, y, z)
            }

            val name = config.getString("$base.name") ?: return null
            val id = config.getString("$base.id")  ?: return null
            val location = config.getLocation("$base.location")  ?: return null
            val portalLocation = config.getLocation("$base.portalLocation")  ?: return null
//            val pos1 = config.getBlockVector3("$base.pos1") ?: return null
//            val pos2 = config.getBlockVector3("$base.pos2") ?: return null
            // restore
            val clipboard = readSchematic(id) ?: return null
            val region = CuboidRegion(clipboard.minimumPoint, clipboard.maximumPoint)

            // val region = CuboidRegion(pos1.toBlockVector3(), pos2.toBlockVector3())
            return Island(region, name, id, location, portalLocation)
        }

        fun save(config: Configuration, island: Island) {
            fun Configuration.setBlockVector3(path: String, vec: BlockVector3) {
                set("$path.x", vec.blockX)
                set("$path.y", vec.blockY)
                set("$path.z", vec.blockZ)
            }

            val path = "islands.${island.id}"
            config.createSection(path)
            config.set("$path.name", island.name)
            config.set("$path.id", island.id)
            config.set("$path.location", island.location)
            config.set("$path.portalLocation", island.portalLocation)
            config.setBlockVector3("$path.pos1", island.region.pos1)
            config.setBlockVector3("$path.pos2", island.region.pos2)
        }

        fun capturedIslands(): List<Island> {
            return SkyBlock.instance.islands.values.filter { it.portalLocation.block.filledEnderEye() == true }
        }

        fun capture(portalLocation: Location, player: Player) {
            val target = SkyBlock.instance.islands.values.firstOrNull { it.portalLocation.block == portalLocation.block } ?: return
            player.sendTitle("yellow{島を攻略した!!}".colored(), "攻略率 ${capturedIslands().size + 1} / ${SkyBlock.instance.islands.size}")
        }

        fun uncaptureAllIslands() {
            TODO()
        }

        fun captureAllIslands() {
            TODO()
        }

        fun whereWithin(location: Location): Pair<String, Island>? {
            return SkyBlock.instance.islands.entries.firstOrNull { (_, island) ->
                island.region.contains(location.toBlockVector3())
            }?.toPair()
        }
    }

    override fun toString(): String {
        val state = when(portalLocation.block.filledEnderEye()) {
            null -> "gray{不明}"
            false -> "red{未攻略}"
            true -> "yellow{攻略済み}"
        }

        return "blue{$name}($id) [状態: $state]: ${location.simple()}".colored()
    }
}