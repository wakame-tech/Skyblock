package tech.wakame.skyblock.advancements.dsl.elements

import com.google.gson.annotations.SerializedName

class DisplayIcon(val item: String)

enum class DisplayFrame {
    @SerializedName("task")
    Task,
    @SerializedName("challenge")
    Challenge,
    @SerializedName("goal")
    Goal
}

class Display(
        var icon: DisplayIcon = DisplayIcon("minecraft:stone"),
        var title: String = "",
        var description: String = "",
        var frame: DisplayFrame = DisplayFrame.Task,
        var showToast: Boolean = false,
        var announceToChat: Boolean = false,
        var hidden: Boolean = true,
        var background: String? = null
)