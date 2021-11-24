package com.example.trackingapp.ui.theme

import androidx.compose.material.Colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)

val BlackishGray = Color(0xFF333542)
val DarkBlackishGray = Color(0xFF444653)
val LightBlackishGray = Color(0xFF6E707C)
val milkGreen = Color(0XFF0BCB85)

val Colors.BackgroundColor : Color
    @Composable
    get() = BlackishGray

val Colors.TextFieldColor : Color
    @Composable
    get() = DarkBlackishGray

val Colors.TextFieldTextColor : Color
    @Composable
    get() = LightBlackishGray

val Colors.ButtonColor : Color
    @Composable
    get() = milkGreen