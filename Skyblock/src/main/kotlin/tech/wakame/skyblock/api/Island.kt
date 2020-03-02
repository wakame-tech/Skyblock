package tech.wakame.skyblock.api

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
            val name = config.getString("$base.name") ?: return null
            val id = config.getString("$base.id")  ?: return null
            val location = config.getLocation("$base.location")  ?: return null
            val portalLocation = config.getLocation("$base.portalLocation")  ?: return null
            val pos1 = config.getLocation("$base.pos1")  ?: return null
            val pos2 = config.getLocation("$base.pos2")  ?: return null
            val region = CuboidRegion(pos1.toBlockVector3(), pos2.toBlockVector3())
            return Island(region, name, id, location, portalLocation)
        }

        fun save(config: Configuration, island: Island) {
            val path = "islands.${island.id}"
            config.createSection(path)
            config.set("$path.name", island.name)
            config.set("$path.id", island.id)
            config.set("$path.location", island.location)
            config.set("$path.portalLocation", island.portalLocation)
            config.set("$path.pos1", island.region.pos1)
            config.set("$path.pos2", island.region.pos2)
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