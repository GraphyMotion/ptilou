package com.ptilou.app.feature.memory

data class CarteMemory(
    val id: Int,
    val symbole: String,
    val retournee: Boolean = false,
    val trouvee: Boolean = false
)
