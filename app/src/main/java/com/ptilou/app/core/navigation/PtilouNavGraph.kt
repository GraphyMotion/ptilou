package com.ptilou.app.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ptilou.app.feature.accueil.AccueilScreen
import com.ptilou.app.feature.calcul.CalculDifficulteScreen
import com.ptilou.app.feature.calcul.CalculExerciceScreen
import com.ptilou.app.feature.calcul.CalculMenuScreen
import com.ptilou.app.feature.decouverte.DecouverteExerciceScreen
import com.ptilou.app.feature.decouverte.DecouverteMenuScreen
import com.ptilou.app.feature.ecriture.EcritureExerciceScreen
import com.ptilou.app.feature.ecriture.EcritureMenuScreen
import com.ptilou.app.feature.garderobe.GarderobeScreen
import com.ptilou.app.feature.lecture.LectureExerciceScreen
import com.ptilou.app.feature.lecture.LectureMenuScreen
import com.ptilou.app.core.ui.components.EcranFatigue
import com.ptilou.app.feature.jeux.BullesScreen
import com.ptilou.app.feature.jeux.JeuxScreen
import com.ptilou.app.feature.memory.MemoryScreen
import com.ptilou.app.feature.parent.ParentScreen
import com.ptilou.app.feature.parent.TempsViewModel
import com.ptilou.app.feature.progression.ProgressionScreen
import kotlinx.coroutines.delay

object PtilouDestinations {
    const val ACCUEIL = "accueil"
    const val JEUX = "jeux"
    const val MEMORY = "memory"
    const val BULLES = "bulles"
    const val PARENT = "parent"
    const val CALCUL_MENU = "calcul_menu"
    const val CALCUL_DIFFICULTE = "calcul_difficulte/{skillCode}"
    const val CALCUL_EXERCICE = "calcul_exercice/{skillCode}/{niveau}"
    const val LECTURE_MENU = "lecture_menu"
    const val LECTURE_EXERCICE = "lecture_exercice/{skillCode}"
    const val ECRITURE_MENU = "ecriture_menu"
    const val ECRITURE_EXERCICE = "ecriture_exercice/{skillCode}"
    const val DECOUVERTE_MENU = "decouverte_menu"
    const val DECOUVERTE_EXERCICE = "decouverte_exercice/{skillCode}"
    const val PROGRESSION = "progression"
    const val GARDEROBE = "garderobe"

    fun calculDifficulte(skillCode: String) = "calcul_difficulte/$skillCode"
    fun calculExercice(skillCode: String, niveau: Int) = "calcul_exercice/$skillCode/$niveau"
    fun lectureExercice(skillCode: String) = "lecture_exercice/$skillCode"
    fun ecritureExercice(skillCode: String) = "ecriture_exercice/$skillCode"
    fun decouverteExercice(skillCode: String) = "decouverte_exercice/$skillCode"
}

@Composable
fun PtilouNavGraph() {
    val navController = rememberNavController()
    val retourAccueil: () -> Unit = {
        navController.popBackStack(PtilouDestinations.ACCUEIL, inclusive = false)
    }
    // Retour à l'étape précédente (ex : Facile -> retour au choix de niveau) : un simple
    // pop d'un cran suffit, quel que soit l'écran, puisque la pile de navigation reflète
    // exactement le parcours (Accueil -> Menu -> Difficulté -> Exercice).
    val retourArriere: () -> Unit = { navController.popBackStack() }

    // Temps d'écran : une minute comptée par minute passée app au premier plan uniquement
    // (repeatOnLifecycle s'arrête quand la tablette est en veille ou l'app en arrière-plan).
    val temps: TempsViewModel = hiltViewModel()
    val minutesRestantes by temps.minutesRestantes.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while (true) {
                delay(60_000)
                temps.ajouterMinute()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
    NavHost(navController = navController, startDestination = PtilouDestinations.ACCUEIL) {
        composable(PtilouDestinations.ACCUEIL) {
            AccueilScreen(
                onOuvrirCalcul = { navController.navigate(PtilouDestinations.CALCUL_MENU) },
                onOuvrirLecture = { navController.navigate(PtilouDestinations.LECTURE_MENU) },
                onOuvrirEcriture = { navController.navigate(PtilouDestinations.ECRITURE_MENU) },
                onOuvrirDecouverte = { navController.navigate(PtilouDestinations.DECOUVERTE_MENU) },
                onOuvrirProgression = { navController.navigate(PtilouDestinations.PROGRESSION) },
                onOuvrirGarderobe = { navController.navigate(PtilouDestinations.GARDEROBE) },
                onOuvrirJeu = { navController.navigate(PtilouDestinations.JEUX) }
            )
        }
        composable(PtilouDestinations.CALCUL_MENU) {
            CalculMenuScreen(
                onCompetenceChoisie = { skillCode ->
                    navController.navigate(PtilouDestinations.calculDifficulte(skillCode))
                },
                onRetourAccueil = retourAccueil,
                onRetourArriere = retourArriere
            )
        }
        composable(
            route = PtilouDestinations.CALCUL_DIFFICULTE,
            arguments = listOf(navArgument("skillCode") { type = NavType.StringType })
        ) { backStackEntry ->
            val skillCode = checkNotNull(backStackEntry.arguments?.getString("skillCode"))
            CalculDifficulteScreen(
                skillCode = skillCode,
                onNiveauChoisi = { niveau ->
                    navController.navigate(PtilouDestinations.calculExercice(skillCode, niveau))
                },
                onRetourAccueil = retourAccueil,
                onRetourArriere = retourArriere
            )
        }
        composable(
            route = PtilouDestinations.CALCUL_EXERCICE,
            arguments = listOf(
                navArgument("skillCode") { type = NavType.StringType },
                navArgument("niveau") { type = NavType.IntType }
            )
        ) {
            CalculExerciceScreen(onRetourAccueil = retourAccueil, onRetourArriere = retourArriere)
        }
        composable(PtilouDestinations.LECTURE_MENU) {
            LectureMenuScreen(
                onCompetenceChoisie = { skillCode ->
                    navController.navigate(PtilouDestinations.lectureExercice(skillCode))
                },
                onRetourAccueil = retourAccueil,
                onRetourArriere = retourArriere
            )
        }
        composable(
            route = PtilouDestinations.LECTURE_EXERCICE,
            arguments = listOf(navArgument("skillCode") { type = NavType.StringType })
        ) {
            LectureExerciceScreen(onRetourAccueil = retourAccueil, onRetourArriere = retourArriere)
        }
        composable(PtilouDestinations.ECRITURE_MENU) {
            EcritureMenuScreen(
                onCompetenceChoisie = { skillCode ->
                    navController.navigate(PtilouDestinations.ecritureExercice(skillCode))
                },
                onRetourAccueil = retourAccueil,
                onRetourArriere = retourArriere
            )
        }
        composable(
            route = PtilouDestinations.ECRITURE_EXERCICE,
            arguments = listOf(navArgument("skillCode") { type = NavType.StringType })
        ) {
            EcritureExerciceScreen(onRetourAccueil = retourAccueil, onRetourArriere = retourArriere)
        }
        composable(PtilouDestinations.DECOUVERTE_MENU) {
            DecouverteMenuScreen(
                onCompetenceChoisie = { skillCode ->
                    navController.navigate(PtilouDestinations.decouverteExercice(skillCode))
                },
                onRetourAccueil = retourAccueil,
                onRetourArriere = retourArriere
            )
        }
        composable(
            route = PtilouDestinations.DECOUVERTE_EXERCICE,
            arguments = listOf(navArgument("skillCode") { type = NavType.StringType })
        ) {
            DecouverteExerciceScreen(onRetourAccueil = retourAccueil, onRetourArriere = retourArriere)
        }
        composable(PtilouDestinations.PROGRESSION) {
            ProgressionScreen(
                onRetourAccueil = retourAccueil,
                onRetourArriere = retourArriere,
                onOuvrirParent = { navController.navigate(PtilouDestinations.PARENT) }
            )
        }
        composable(PtilouDestinations.PARENT) {
            ParentScreen(onRetourAccueil = retourAccueil, onRetourArriere = retourArriere)
        }
        composable(PtilouDestinations.JEUX) {
            JeuxScreen(
                onOuvrirMemory = { navController.navigate(PtilouDestinations.MEMORY) },
                onOuvrirBulles = { navController.navigate(PtilouDestinations.BULLES) },
                onRetourAccueil = retourAccueil,
                onRetourArriere = retourArriere
            )
        }
        composable(PtilouDestinations.BULLES) {
            BullesScreen(onRetourAccueil = retourAccueil, onRetourArriere = retourArriere)
        }
        composable(PtilouDestinations.GARDEROBE) {
            GarderobeScreen(onRetourAccueil = retourAccueil, onRetourArriere = retourArriere)
        }
        composable(PtilouDestinations.MEMORY) {
            MemoryScreen(onRetourAccueil = retourAccueil, onRetourArriere = retourArriere)
        }
    }

    if (minutesRestantes <= 0) {
        EcranFatigue(onAccorderBonus = temps::accorderBonus)
    }
    }
}
