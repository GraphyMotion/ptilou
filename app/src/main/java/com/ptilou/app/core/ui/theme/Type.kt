package com.ptilou.app.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Tailles de police volontairement grandes : lisibilité pour un lecteur débutant CE1
val PtilouTypography = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 40.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 32.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 26.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 22.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 18.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
)
