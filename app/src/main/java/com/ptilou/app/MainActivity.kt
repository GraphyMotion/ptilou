package com.ptilou.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.ptilou.app.core.navigation.PtilouNavGraph
import com.ptilou.app.core.ui.theme.PtilouTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        masquerBarresSysteme()
        setContent {
            PtilouTheme {
                PtilouNavGraph()
            }
        }
    }

    // Plein écran immersif : la barre de navigation de la tablette (gestes ou boutons) ne
    // doit pas gêner l'affichage — le retour à l'accueil se fait via l'icône maison de l'app.
    // Un balayage la fait réapparaître brièvement (BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE),
    // donc on la recache à chaque fois que la fenêtre reprend le focus.
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) masquerBarresSysteme()
    }

    private fun masquerBarresSysteme() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }
}
