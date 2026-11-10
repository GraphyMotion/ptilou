package com.ptilou.app.core.miseajour

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

data class Nouveaute(val version: String, val urlApk: String)

private const val URL_DERNIERE_VERSION = "https://api.github.com/repos/GraphyMotion/ptilou/releases/latest"

// Mise à jour depuis les Releases GitHub du dépôt public : pas de token, pas de
// bibliothèque réseau (HttpURLConnection + org.json fournis par Android). Le tag de la
// release (« v1.3 ») doit correspondre au versionName. Aucune donnée de l'enfant n'est
// envoyée : seule la dernière version publiée est demandée.
@Singleton
class MiseAJour @Inject constructor(@ApplicationContext private val context: Context) {

    val versionActuelle: String =
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "0"

    suspend fun verifier(): Nouveaute? = withContext(Dispatchers.IO) {
        val json = ouvrir(URL_DERNIERE_VERSION).run {
            setRequestProperty("Accept", "application/vnd.github+json")
            try {
                if (responseCode != HttpURLConnection.HTTP_OK) throw IOException("GitHub a répondu HTTP $responseCode")
                inputStream.bufferedReader().use { it.readText() }
            } finally {
                disconnect()
            }
        }
        val release = JSONObject(json)
        val version = release.getString("tag_name").trimStart('v', 'V')
        val assets = release.getJSONArray("assets")
        val apk = (0 until assets.length()).map { assets.getJSONObject(it) }
            .firstOrNull { it.getString("name").endsWith(".apk", ignoreCase = true) }
            ?: return@withContext null
        if (estPlusRecente(version, versionActuelle)) Nouveaute(version, apk.getString("browser_download_url")) else null
    }

    suspend fun telecharger(nouveaute: Nouveaute, onProgres: (Float) -> Unit): File = withContext(Dispatchers.IO) {
        val fichier = File(context.cacheDir, "ptilou-maj.apk")
        // browser_download_url redirige vers le stockage GitHub (https → https : suivi auto).
        val connexion = ouvrir(nouveaute.urlApk)
        try {
            if (connexion.responseCode != HttpURLConnection.HTTP_OK) {
                throw IOException("Téléchargement : HTTP ${connexion.responseCode}")
            }
            val total = connexion.contentLengthLong
            connexion.inputStream.use { entree ->
                fichier.outputStream().use { sortie ->
                    val tampon = ByteArray(DEFAULT_BUFFER_SIZE)
                    var lu = 0L
                    while (true) {
                        val n = entree.read(tampon)
                        if (n < 0) break
                        sortie.write(tampon, 0, n)
                        lu += n
                        if (total > 0) onProgres(lu.toFloat() / total)
                    }
                }
            }
        } finally {
            connexion.disconnect()
        }
        fichier
    }

    // Retourne false si Android doit d'abord autoriser Ptilou à installer des applis
    // (les réglages s'ouvrent ; il faut ensuite relancer l'installation).
    fun installer(fichier: File): Boolean {
        if (!context.packageManager.canRequestPackageInstalls()) {
            context.startActivity(
                Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${context.packageName}"))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            return false
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", fichier)
        context.startActivity(
            Intent(Intent.ACTION_VIEW)
                .setDataAndType(uri, "application/vnd.android.package-archive")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        return true
    }

    private fun ouvrir(url: String) = (URL(url).openConnection() as HttpURLConnection).apply {
        connectTimeout = 15_000
        readTimeout = 30_000
    }
}

// Comparaison numérique segment par segment : "1.10" > "1.9" (l'ordre alphabétique dirait l'inverse).
internal fun estPlusRecente(distante: String, locale: String): Boolean {
    fun segments(v: String) = v.split('.', '-').map { s -> s.takeWhile(Char::isDigit).toIntOrNull() ?: 0 }
    val d = segments(distante)
    val l = segments(locale)
    for (i in 0 until maxOf(d.size, l.size)) {
        val dv = d.getOrElse(i) { 0 }
        val lv = l.getOrElse(i) { 0 }
        if (dv != lv) return dv > lv
    }
    return false
}
