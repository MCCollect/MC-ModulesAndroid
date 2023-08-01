package com.mc.mcmodules.view.logcat.models

data class Log (
    val image: Int,
    val date: String,
    val description: String,
    val type: String = "undefined",
)
