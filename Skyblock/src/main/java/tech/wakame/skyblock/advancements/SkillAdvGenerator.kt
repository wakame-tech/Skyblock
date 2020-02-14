package tech.wakame.skyblock.advancements

import eu.endercentral.crazy_advancements.*
import org.bukkit.Material

// https://www.spigotmc.org/resources/crazy-advancements-api.51741/
class SkillAdvGenerator(advancementsPath: String) {
    init {
        // test
        val manager = CrazyAdvancements.getNewAdvancementManager()
        val rootDisplay = AdvancementDisplay(Material.ARMOR_STAND, "My Custom Advancements", "With cool additions", AdvancementDisplay.AdvancementFrame.TASK, false, false, AdvancementVisibility.ALWAYS)
        val root = Advancement(null, NameKey("custom", "root"), rootDisplay)

        val childrenDisplay = AdvancementDisplay(Material.ENDER_EYE, "test", "desc2", AdvancementDisplay.AdvancementFrame.GOAL, true, true, AdvancementVisibility.VANILLA)
        childrenDisplay.setCoordinates(1.0F, 0.0F)
        val children = Advancement(root, NameKey("custom", "right"), childrenDisplay)
        // children.criteria ???
    }
}