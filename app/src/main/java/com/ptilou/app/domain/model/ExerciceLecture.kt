package com.ptilou.app.domain.model

data class ExerciceLecture(
    val competence: CompetenceLecture,
    val texte: String,
    val question: String,
    val propositions: List<String>,
    val bonneReponseIndex: Int
)
