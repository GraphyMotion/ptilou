# Ptilou 🦉

Application Android éducative et gratuite pour les enfants de **CE1** (7-8 ans), calée sur les repères Eduscol du cycle 2. Elle fonctionne entièrement hors-ligne, et aucune donnée ne quitte la tablette.

## Modules

- **Calcul** : additions et soustractions (en ligne ou **posées en colonnes**), tables ×2/×5/×10, division-partage, numération jusqu'à 1000 (comparer, suites, avant/après, décomposer), petits problèmes. 3 niveaux, avec plus de pièces aux niveaux difficiles.
- **Lecture** : compter les syllabes (avec lecture du mot à voix haute), reconnaître l'orthographe d'un mot, comprendre une phrase.
- **Écriture** : vraies dictées (le mot est lu par la synthèse vocale, jamais affiché), mots fréquents et mots-outils.
- **Découverte du monde** : jours et mois, saisons, corps et sens, animaux et vivant.

## Motivation et cadre

- Pièces, badges, série de jours, jauge de session, garde-robe de la mascotte (16 accessoires).
- Deuxième chance en cas d'erreur, et réponse révélée sans point au deuxième échec.
- Mini-jeux (Mémory, Bulles) débloqués par paliers de 10 points venant d'**au moins 2 thèmes**, pour éviter que l'enfant ne fasse que ce qu'il maîtrise déjà.
- **Espace parent** (protégé par une question de calcul) : compétences fragiles ou évitées, temps d'écran quotidien (30 min par défaut, extensible).

## Compiler

Ouvrir le dossier dans Android Studio, ou :

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

Le build release attend un keystore décrit dans `local.properties` (`PTILOU_KEYSTORE_FILE`, `PTILOU_KEYSTORE_PASSWORD`, `PTILOU_KEY_ALIAS`, `PTILOU_KEY_PASSWORD`). Ce fichier n'est jamais versionné.

Stack : Kotlin, Jetpack Compose, Hilt, Room, DataStore, TextToSpeech natif.

## Licences

- **Code** : [MIT](LICENSE).
- **Listes de mots** des fichiers `GenerateurExerciceLecture.kt` et `GenerateurExerciceEcriture.kt` : en partie dérivées de [Lexique 3.83](http://www.lexique.org) (New, Pallier et al.), sous licence [CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/). Ces listes dérivées restent sous CC BY-SA 4.0.
