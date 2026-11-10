package com.ptilou.app.domain.generator

import com.ptilou.app.domain.model.CompetenceLecture
import com.ptilou.app.domain.model.ExerciceLecture

// Contenu linguistique rédigé à la main (banque statique) : contrairement au calcul, le
// français ne se génère pas de façon procédurale fiable — la qualité pédagogique du texte
// doit être garantie, pas laissée à un algorithme ou une IA non supervisée.
object GenerateurExerciceLecture {

    // Un seul élément mémorisé par compétence (pas par instance de ViewModel) : évite la
    // répétition immédiate même si l'enfant quitte et relance l'exercice tout de suite.
    private var dernierSyllabes: Modele? = null
    private var dernierOrthographe: Modele? = null
    private var dernierComprehension: Modele? = null

    fun genererPour(competence: CompetenceLecture): ExerciceLecture = when (competence) {
        CompetenceLecture.RECONNAISSANCE_SYLLABES ->
            depuisModele(tirerSansRepeter(banqueSyllabes, dernierSyllabes).also { dernierSyllabes = it })
        CompetenceLecture.RECONNAISSANCE_MOTS ->
            depuisModele(tirerSansRepeter(banqueOrthographe, dernierOrthographe).also { dernierOrthographe = it })
        CompetenceLecture.COMPREHENSION_PHRASES ->
            depuisModele(tirerSansRepeter(banqueComprehension, dernierComprehension).also { dernierComprehension = it })
    }

    private data class Modele(
        val competence: CompetenceLecture,
        val texte: String,
        val question: String,
        val propositions: List<String>,
        val indexBonneReponse: Int
    )

    private fun depuisModele(modele: Modele): ExerciceLecture {
        val bonneReponse = modele.propositions[modele.indexBonneReponse]
        val propositionsMelangees = modele.propositions.shuffled()
        return ExerciceLecture(
            competence = modele.competence,
            texte = modele.texte,
            question = modele.question,
            propositions = propositionsMelangees,
            bonneReponseIndex = propositionsMelangees.indexOf(bonneReponse)
        )
    }

    private fun syllabes(mot: String, nombreCorrect: Int): Modele {
        val distracteurs = when (nombreCorrect) {
            1 -> listOf(2, 3)
            2 -> listOf(1, 3)
            3 -> listOf(2, 4)
            else -> listOf(2, 3)
        }
        val propositions = (distracteurs + nombreCorrect).map { it.toString() }
        return Modele(
            competence = CompetenceLecture.RECONNAISSANCE_SYLLABES,
            texte = mot,
            question = "Combien de syllabes entends-tu dans ce mot ?",
            propositions = propositions,
            indexBonneReponse = propositions.size - 1
        )
    }

    // Mots fréquents tirés de Lexique 3.83 (lexique.org, CC BY-SA 4.0), triés à la main.
    // Seuls les mots dont le compte est identique à l'oral ET en frappant les syllabes à
    // l'école sont gardés : pas de « e » muet final (ba-na-ne = 2 ou 3 ?), pas de « e » muet
    // interne (che-val), pas de « i » + voyelle (li-on), sinon une bonne réponse serait
    // comptée fausse selon la façon de compter.
    private val banqueSyllabes = mapOf(
        1 to "chat main nuit jour bras rue eau lit bois train pied mer vent feu nez mur chien " +
            "pluie pain roi doigt pont lait toit banc bain fleur lac loup fruit sac four jus",
        2 to "vélo maison ballon lapin souris tortue enfant matin soleil jardin papier maman papa " +
            "ami bureau chapeau bateau manteau tableau couteau rideau oiseau poisson cadeau " +
            "drapeau bébé panier bouton lundi jeudi copain cousin voisin photo métro dessin " +
            "chanson café château bonjour merci miroir tiroir hiver midi forêt",
        3 to "chocolat kangourou papillon pyjama parapluie escargot ananas cinéma animal numéro " +
            "magasin pantalon hôpital restaurant escalier autobus ascenseur professeur directeur",
        4 to "ordinateur télévision aspirateur rhinocéros alligator"
    ).flatMap { (nombre, mots) -> mots.split(" ").map { syllabes(it, nombre) } }

    private fun orthographe(correct: String, distracteurs: List<String>): Modele {
        val propositions = distracteurs + correct
        return Modele(
            competence = CompetenceLecture.RECONNAISSANCE_MOTS,
            texte = "",
            question = "Quel mot est bien écrit ?",
            propositions = propositions,
            indexBonneReponse = propositions.size - 1
        )
    }

    private val banqueOrthographe = listOf(
        orthographe("chaise", listOf("chaize", "chèse", "chaisse")),
        orthographe("oiseau", listOf("oizo", "oizeau", "oiso")),
        orthographe("bateau", listOf("bato", "batau", "bateaut")),
        orthographe("gâteau", listOf("gato", "gâto", "gateau")),
        orthographe("fleur", listOf("fleure", "flure", "fleurre")),
        orthographe("maison", listOf("maisson", "mézon", "maizon")),
        orthographe("école", listOf("ecole", "aicole", "aicolle")),
        orthographe("crayon", listOf("crayont", "craion", "crayond")),
        orthographe("vélo", listOf("vélot", "véllo", "velo")),
        orthographe("soleil", listOf("soleye", "solail", "souleil")),
        orthographe("lune", listOf("lunne", "lûne", "lonne")),
        orthographe("arbre", listOf("arbe", "arbrre", "arber")),
        orthographe("livre", listOf("livrre", "lifre", "livr")),
        orthographe("cahier", listOf("caier", "cahiez", "cayer")),
        orthographe("voiture", listOf("voiturre", "vouature", "voitur")),
        orthographe("jardin", listOf("jardain", "gardin", "jardind"))
    )

    private fun comprehension(texte: String, question: String, correct: String, distracteurs: List<String>): Modele {
        val propositions = distracteurs + correct
        return Modele(
            competence = CompetenceLecture.COMPREHENSION_PHRASES,
            texte = texte,
            question = question,
            propositions = propositions,
            indexBonneReponse = propositions.size - 1
        )
    }

    private val banqueComprehension = listOf(
        comprehension(
            "Léa met son manteau car il fait froid dehors.",
            "Pourquoi Léa met-elle son manteau ?",
            "Il fait froid",
            listOf("Il fait chaud", "Elle va se baigner")
        ),
        comprehension(
            "Tom range ses jouets dans le grand coffre bleu.",
            "Où Tom range-t-il ses jouets ?",
            "Dans le grand coffre bleu",
            listOf("Sous son lit", "Dans le jardin")
        ),
        comprehension(
            "Le chat dort sur le canapé du salon.",
            "Où dort le chat ?",
            "Sur le canapé",
            listOf("Dans la cuisine", "Dehors")
        ),
        comprehension(
            "Maman prépare des crêpes pour le goûter.",
            "Que prépare maman ?",
            "Des crêpes",
            listOf("Une soupe", "Des gâteaux")
        ),
        comprehension(
            "Il pleut, alors Sarah prend son parapluie.",
            "Pourquoi Sarah prend-elle son parapluie ?",
            "Parce qu'il pleut",
            listOf("Parce qu'il fait beau", "Parce qu'elle a froid")
        ),
        comprehension(
            "Le petit chien court après la balle rouge.",
            "Après quoi court le chien ?",
            "La balle rouge",
            listOf("Le chat", "Sa gamelle")
        ),
        comprehension(
            "Nino dessine un grand soleil jaune sur sa feuille.",
            "Que dessine Nino ?",
            "Un grand soleil jaune",
            listOf("Une maison", "Un bateau")
        ),
        comprehension(
            "À la récréation, les enfants jouent dans la cour.",
            "Où jouent les enfants à la récréation ?",
            "Dans la cour",
            listOf("Dans la classe", "À la cantine")
        ),
        comprehension(
            "Paul boit un grand verre de lait au petit-déjeuner.",
            "Que boit Paul au petit-déjeuner ?",
            "Un grand verre de lait",
            listOf("Un jus d'orange", "Un chocolat chaud")
        ),
        comprehension(
            "La maîtresse écrit la date au tableau.",
            "Qui écrit la date au tableau ?",
            "La maîtresse",
            listOf("Un élève", "Le directeur")
        ),
        comprehension(
            "Le soir, Emma lit une histoire avant de dormir.",
            "Que fait Emma le soir avant de dormir ?",
            "Elle lit une histoire",
            listOf("Elle regarde la télé", "Elle mange un gâteau")
        ),
        comprehension(
            "Les poissons nagent dans le grand aquarium bleu.",
            "Où nagent les poissons ?",
            "Dans le grand aquarium bleu",
            listOf("Dans la mer", "Dans une flaque d'eau")
        ),
        comprehension(
            "Hugo grimpe tout en haut du grand toboggan.",
            "Où grimpe Hugo ?",
            "Tout en haut du toboggan",
            listOf("Sur un arbre", "Sur le mur")
        ),
        comprehension(
            "En hiver, on met des gants pour ne pas avoir froid aux mains.",
            "Pourquoi met-on des gants en hiver ?",
            "Pour ne pas avoir froid aux mains",
            listOf("Pour jouer au ballon", "Pour dormir")
        ),
        comprehension(
            "Le boulanger sort des croissants chauds du four.",
            "Que sort le boulanger du four ?",
            "Des croissants chauds",
            listOf("Des baguettes froides", "Des bonbons")
        ),
        comprehension(
            "Mia range ses crayons de couleur dans une trousse rouge.",
            "Où Mia range-t-elle ses crayons ?",
            "Dans une trousse rouge",
            listOf("Dans un sac bleu", "Sur la table")
        )
    )
}
