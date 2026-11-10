package com.ptilou.app.domain.model

data class ExerciceDecouverte(
    val competence: CompetenceDecouverte,
    val question: String,
    val propositions: List<String>,
    val bonneReponseIndex: Int
)
