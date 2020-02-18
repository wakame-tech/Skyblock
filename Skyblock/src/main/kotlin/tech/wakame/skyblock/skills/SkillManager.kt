package tech.wakame.skyblock.skills

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Server
import tech.wakame.skyblock.Skyblock
import tech.wakame.skyblock.advancements.dsl.AdvancementsDSL

fun Server.broadcast(vararg message: String) {
    this.spigot().broadcast(*message.map { TextComponent(it) }.toTypedArray())
}

class SkillManager {
    private val skills: MutableList<AdvancementsDSL> = mutableListOf()

    fun register(dsl: AdvancementsDSL) {
        skills += dsl
    }

    fun generateAllSkills() {
        skills.forEach { dsl ->
            dsl.dumpJson(Skyblock.dataPackRootPath)
        }
    }

    fun status() {
        Skyblock.server.broadcast(
                "[SkillManager] there are ${ChatColor.YELLOW}${skills.size}${ChatColor.RESET} skills",
                *skills.map { skill ->
                    "- ${ChatColor.YELLOW}${skill.name}"
                }.toTypedArray()
        )
    }
}