package tech.wakame.skyblock.util

import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player
import tech.wakame.skyblock.SkyBlock
import tech.wakame.skyblock.advancements.dsl.AdvancementsDSL
import tech.wakame.skyblock.resources.skills.FireSkill
import tech.wakame.skyblock.resources.skills.WindSkill
import java.lang.Exception

class SkillManager(private val plugin: SkyBlock) {
    private val skills: MutableList<AdvancementsDSL> = mutableListOf(FireSkill, WindSkill)

    init {
        status()
    }

    fun generateAllSkills(operator: Player) {
        skills.apply {
            val gen = mapNotNull { dsl ->
                try {
                    dsl.dumpJson(plugin.dataPackPath)
                    dsl
                } catch (e: Exception) {
                    operator.sendMessage("red{${e.message}}".colored())
                    operator.sendMessage("red{${dsl.name} failed to generate}".colored())
                    null
                }
            }
            clear()
            addAll(gen)
        }

        operator.sendMessage("generated ${skills.size} skills")
    }

    fun status() {
        plugin.server.broadcast(
                "[SkillManager] there are yellow{${skills.size}} skills".colored(),
                *skills.map { skill ->
                    "- bold{yellow{${skill.name}}}".colored()
                }.toTypedArray()
        )
    }
}