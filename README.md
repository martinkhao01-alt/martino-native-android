# 📱 Carnet de Restaurants — App Android Native (Kotlin)

## Fonctionnalités
- Liste restaurants en grille avec photo, nom, note étoiles, badge prix
- Fiche détail : photo, adresse (Google Maps), téléphone, site internet, cuisine, notes
- Fourchette de prix : € Pas cher / €€ Bon prix / €€€ Très cher
- Commentaires avec photos sur chaque restaurant
- Recherche par nom, adresse, cuisine, téléphone
- Stockage 100% hors ligne (Room Database)

---

## 🚀 Option 1 — GitHub Actions (recommandé, sans rien installer)

### Étape 1 — Uploader sur GitHub
1. Créez un compte sur github.com
2. Créez un nouveau repository (bouton "New")
3. Uploadez ce dossier complet

### ⚠️ Étape 2 — Remplacer le gradle-wrapper.jar
Le fichier `gradle/wrapper/gradle-wrapper.jar` inclus est un placeholder.
**Avant de pusher**, téléchargez le vrai jar ici :
https://github.com/gradle/gradle/raw/v8.0.0/gradle/wrapper/gradle-wrapper.jar
Et remplacez le fichier dans `gradle/wrapper/`.

**Alternative sans jar** : modifiez `.github/workflows/build-apk.yml` et remplacez
`./gradlew assembleDebug` par :
```yaml
- name: Build with Gradle directly
  uses: gradle/actions/setup-gradle@v3
  with:
    gradle-version: '8.0'
- run: gradle assembleDebug
```

### Étape 3 — Lancer la compilation
1. Allez dans l'onglet **Actions** de votre repository GitHub
2. Cliquez sur **"Build Android APK"**
3. Cliquez **"Run workflow"**
4. Attendez ~5 minutes
5. Téléchargez l'APK dans **Artifacts**

---

## 🖥️ Option 2 — Android Studio (Windows 10/11)
1. Téléchargez Android Studio : https://developer.android.com/studio
2. Ouvrez ce dossier dans Android Studio
3. Attendez la synchronisation Gradle
4. **Build > Build APK(s)**
5. L'APK est dans `app/build/outputs/apk/debug/`

---

## 📁 Structure du projet
```
CarnetRestaurants/
├── .github/workflows/build-apk.yml   ← GitHub Actions
├── gradle/wrapper/
│   ├── gradle-wrapper.jar            ← ⚠️ Remplacer par le vrai jar
│   └── gradle-wrapper.properties
├── gradlew / gradlew.bat
├── build.gradle
├── settings.gradle
└── app/
    ├── build.gradle
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/carnet/restaurants/
        │   ├── model/       Restaurant.kt, Commentaire.kt
        │   ├── database/    AppDatabase, DAOs
        │   ├── repository/  RestaurantRepository
        │   ├── viewmodel/   ListeVM, DetailVM, FormVM
        │   ├── adapter/     RestaurantAdapter, CommentaireAdapter
        │   └── ui/          MainActivity, ListeFragment, DetailFragment, FormFragment
        └── res/             layouts, drawables, navigation, values
```
