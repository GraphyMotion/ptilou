package com.ptilou.app.domain.generator

import com.ptilou.app.domain.model.CompetenceDecouverte
import com.ptilou.app.domain.model.ExerciceDecouverte

// Contenu rédigé à la main (banque statique), calibré sur le programme "Questionner le
// monde" du cycle 2 (temps, saisons, corps humain, vivant).
object GenerateurExerciceDecouverte {

    private var dernierJoursEtMois: Modele? = null
    private var dernierSaisons: Modele? = null
    private var dernierCorps: Modele? = null
    private var dernierAnimaux: Modele? = null

    fun genererPour(competence: CompetenceDecouverte): ExerciceDecouverte = when (competence) {
        CompetenceDecouverte.JOURS_ET_MOIS ->
            depuisModele(tirerSansRepeter(banqueJoursEtMois, dernierJoursEtMois).also { dernierJoursEtMois = it })
        CompetenceDecouverte.SAISONS ->
            depuisModele(tirerSansRepeter(banqueSaisons, dernierSaisons).also { dernierSaisons = it })
        CompetenceDecouverte.CORPS_ET_SENS ->
            depuisModele(tirerSansRepeter(banqueCorps, dernierCorps).also { dernierCorps = it })
        CompetenceDecouverte.ANIMAUX_ET_VIVANT ->
            depuisModele(tirerSansRepeter(banqueAnimaux, dernierAnimaux).also { dernierAnimaux = it })
    }

    private data class Modele(
        val competence: CompetenceDecouverte,
        val question: String,
        val propositions: List<String>,
        val indexBonneReponse: Int
    )

    private fun depuisModele(modele: Modele): ExerciceDecouverte {
        val bonneReponse = modele.propositions[modele.indexBonneReponse]
        val propositionsMelangees = modele.propositions.shuffled()
        return ExerciceDecouverte(
            competence = modele.competence,
            question = modele.question,
            propositions = propositionsMelangees,
            bonneReponseIndex = propositionsMelangees.indexOf(bonneReponse)
        )
    }

    private fun question(
        competence: CompetenceDecouverte,
        question: String,
        correct: String,
        distracteurs: List<String>
    ): Modele {
        val propositions = distracteurs + correct
        return Modele(competence, question, propositions, propositions.size - 1)
    }

    private val banqueJoursEtMois = listOf(
        question(CompetenceDecouverte.JOURS_ET_MOIS, "Quel jour vient juste après lundi ?", "mardi", listOf("dimanche", "jeudi")),
        question(CompetenceDecouverte.JOURS_ET_MOIS, "Quel jour vient juste avant dimanche ?", "samedi", listOf("lundi", "mercredi")),
        question(CompetenceDecouverte.JOURS_ET_MOIS, "Combien y a-t-il de jours dans une semaine ?", "7", listOf("5", "10")),
        question(CompetenceDecouverte.JOURS_ET_MOIS, "Quel est le premier mois de l'année ?", "janvier", listOf("décembre", "mars")),
        question(CompetenceDecouverte.JOURS_ET_MOIS, "Quel est le dernier mois de l'année ?", "décembre", listOf("janvier", "novembre")),
        question(CompetenceDecouverte.JOURS_ET_MOIS, "En quel mois fête-t-on Noël ?", "décembre", listOf("juillet", "octobre")),
        question(CompetenceDecouverte.JOURS_ET_MOIS, "Quel jour vient juste après vendredi ?", "samedi", listOf("lundi", "mercredi")),
        question(CompetenceDecouverte.JOURS_ET_MOIS, "Combien y a-t-il de mois dans une année ?", "12", listOf("7", "24")),
        question(CompetenceDecouverte.JOURS_ET_MOIS, "Quel jour vient juste avant mercredi ?", "mardi", listOf("jeudi", "lundi")),
        question(CompetenceDecouverte.JOURS_ET_MOIS, "Quel mois vient juste après janvier ?", "février", listOf("mars", "décembre")),
        question(CompetenceDecouverte.JOURS_ET_MOIS, "Quel mois vient juste avant août ?", "juillet", listOf("septembre", "juin")),
        question(CompetenceDecouverte.JOURS_ET_MOIS, "Combien d'heures y a-t-il dans une journée ?", "24", listOf("12", "60")),
        question(CompetenceDecouverte.JOURS_ET_MOIS, "Quels jours ne va-t-on pas à l'école ?", "samedi et dimanche", listOf("lundi et mardi", "jeudi et vendredi")),
        question(CompetenceDecouverte.JOURS_ET_MOIS, "Combien de minutes y a-t-il dans une heure ?", "60", listOf("10", "100"))
    )

    private val banqueSaisons = listOf(
        question(CompetenceDecouverte.SAISONS, "Quelle saison vient juste après l'hiver ?", "le printemps", listOf("l'été", "l'automne")),
        question(CompetenceDecouverte.SAISONS, "En quelle saison fait-il le plus chaud ?", "l'été", listOf("l'hiver", "l'automne")),
        question(CompetenceDecouverte.SAISONS, "En quelle saison les feuilles tombent-elles des arbres ?", "l'automne", listOf("le printemps", "l'été")),
        question(CompetenceDecouverte.SAISONS, "En quelle saison fait-il le plus froid ?", "l'hiver", listOf("l'été", "le printemps")),
        question(CompetenceDecouverte.SAISONS, "À quelle saison les fleurs poussent-elles le plus ?", "le printemps", listOf("l'hiver", "l'automne")),
        question(CompetenceDecouverte.SAISONS, "Combien y a-t-il de saisons dans une année ?", "4", listOf("2", "6")),
        question(CompetenceDecouverte.SAISONS, "En quelle saison va-t-on souvent à la plage ?", "l'été", listOf("l'hiver", "l'automne")),
        question(CompetenceDecouverte.SAISONS, "Quelle saison vient juste avant l'hiver ?", "l'automne", listOf("le printemps", "l'été")),
        question(CompetenceDecouverte.SAISONS, "En quelle saison peut-il neiger ?", "l'hiver", listOf("l'été", "le printemps")),
        question(CompetenceDecouverte.SAISONS, "En quel mois commence l'été ?", "juin", listOf("janvier", "octobre")),
        question(CompetenceDecouverte.SAISONS, "En quelle saison fait-on la rentrée des classes ?", "l'automne", listOf("l'hiver", "le printemps")),
        question(CompetenceDecouverte.SAISONS, "Quel fruit ramasse-t-on souvent en automne ?", "la châtaigne", listOf("la cerise", "la fraise")),
        question(CompetenceDecouverte.SAISONS, "Quelle saison vient juste après l'été ?", "l'automne", listOf("l'hiver", "le printemps")),
        question(CompetenceDecouverte.SAISONS, "Que met-on quand il fait très froid ?", "un bonnet et des gants", listOf("un maillot de bain", "des sandales"))
    )

    private val banqueCorps = listOf(
        question(CompetenceDecouverte.CORPS_ET_SENS, "Avec quelle partie du corps sent-on les odeurs ?", "le nez", listOf("l'oreille", "le pied")),
        question(CompetenceDecouverte.CORPS_ET_SENS, "Avec quelle partie du corps entend-on les sons ?", "les oreilles", listOf("les yeux", "les mains")),
        question(CompetenceDecouverte.CORPS_ET_SENS, "Avec quelle partie du corps voit-on ?", "les yeux", listOf("le nez", "les genoux")),
        question(CompetenceDecouverte.CORPS_ET_SENS, "Combien avons-nous de sens ?", "5", listOf("3", "8")),
        question(CompetenceDecouverte.CORPS_ET_SENS, "Avec quelle partie du corps goûte-t-on les aliments ?", "la langue", listOf("le coude", "l'oreille")),
        question(CompetenceDecouverte.CORPS_ET_SENS, "Combien de doigts avons-nous à une main ?", "5", listOf("4", "6")),
        question(CompetenceDecouverte.CORPS_ET_SENS, "Quel organe fait circuler le sang dans le corps ?", "le cœur", listOf("le foie", "l'oreille")),
        question(CompetenceDecouverte.CORPS_ET_SENS, "Avec quelle partie du corps sent-on le chaud et le froid ?", "la peau", listOf("les cheveux", "les ongles")),
        question(CompetenceDecouverte.CORPS_ET_SENS, "Quel organe nous sert à respirer ?", "les poumons", listOf("l'estomac", "les oreilles")),
        question(CompetenceDecouverte.CORPS_ET_SENS, "Que faut-il faire avant de manger ?", "se laver les mains", listOf("se coucher", "courir")),
        question(CompetenceDecouverte.CORPS_ET_SENS, "Combien de fois par jour faut-il se brosser les dents ?", "2 fois", listOf("1 fois par semaine", "jamais")),
        question(CompetenceDecouverte.CORPS_ET_SENS, "Avec quoi mâche-t-on les aliments ?", "les dents", listOf("le nez", "les yeux")),
        question(CompetenceDecouverte.CORPS_ET_SENS, "Que protège le crâne ?", "le cerveau", listOf("le cœur", "le ventre")),
        question(CompetenceDecouverte.CORPS_ET_SENS, "Pourquoi faut-il bien dormir la nuit ?", "pour reposer son corps", listOf("pour avoir faim", "pour grandir moins vite"))
    )

    private val banqueAnimaux = listOf(
        question(CompetenceDecouverte.ANIMAUX_ET_VIVANT, "Quel animal pond des œufs ?", "la poule", listOf("la vache", "le chien")),
        question(CompetenceDecouverte.ANIMAUX_ET_VIVANT, "De quoi les plantes ont-elles besoin pour pousser ?", "d'eau et de lumière", listOf("de bonbons", "de sable sec")),
        question(CompetenceDecouverte.ANIMAUX_ET_VIVANT, "Quel animal donne du lait ?", "la vache", listOf("la poule", "le poisson")),
        question(CompetenceDecouverte.ANIMAUX_ET_VIVANT, "Comment s'appelle le petit du chien ?", "le chiot", listOf("le chaton", "le poussin")),
        question(CompetenceDecouverte.ANIMAUX_ET_VIVANT, "Comment s'appelle le petit de la poule ?", "le poussin", listOf("le chiot", "le veau")),
        question(CompetenceDecouverte.ANIMAUX_ET_VIVANT, "Quel animal vit dans l'eau et respire avec des branchies ?", "le poisson", listOf("le chat", "l'oiseau")),
        question(CompetenceDecouverte.ANIMAUX_ET_VIVANT, "Quel animal a des plumes et peut voler ?", "l'oiseau", listOf("le lapin", "la tortue")),
        question(CompetenceDecouverte.ANIMAUX_ET_VIVANT, "Comment s'appelle le petit de la vache ?", "le veau", listOf("l'agneau", "le poulain")),
        question(CompetenceDecouverte.ANIMAUX_ET_VIVANT, "Comment s'appelle le petit du mouton ?", "l'agneau", listOf("le veau", "le chaton")),
        question(CompetenceDecouverte.ANIMAUX_ET_VIVANT, "Que devient une chenille ?", "un papillon", listOf("une abeille", "un escargot")),
        question(CompetenceDecouverte.ANIMAUX_ET_VIVANT, "Que devient un têtard ?", "une grenouille", listOf("un poisson", "un lézard")),
        question(CompetenceDecouverte.ANIMAUX_ET_VIVANT, "Quel animal fabrique du miel ?", "l'abeille", listOf("la fourmi", "la mouche")),
        question(CompetenceDecouverte.ANIMAUX_ET_VIVANT, "Combien de pattes a une araignée ?", "8", listOf("6", "4")),
        question(CompetenceDecouverte.ANIMAUX_ET_VIVANT, "Que mange la vache ?", "de l'herbe", listOf("de la viande", "des poissons"))
    )
}
