# 📱 Carnet de Restaurants — App Android Native (Kotlin)

## Ce que fait l'application
- Liste de vos restaurants en grille avec photos
- Fiche détail : photo, adresse (ouvre Google Maps), téléphone, cuisine, notes, notation étoiles
- Commentaires avec photos sur chaque restaurant
- Recherche par nom, adresse, cuisine, téléphone
- Stockage 100% hors ligne (Room Database sur le téléphone)

---

## 🖥️ Comment compiler et obtenir l'APK

### Étape 1 — Installer Android Studio
1. Allez sur : https://developer.android.com/studio
2. Téléchargez **Android Studio Hedgehog** (compatible Windows 10/11)
   ⚠️ Windows 7 n'est plus supporté — utilisez un PC Windows 10, ou un service en ligne
3. Installez Android Studio (suivez l'assistant)

### Alternative si vous avez Windows 7 : utiliser GitHub Codespaces (gratuit en ligne)
1. Créez un compte gratuit sur github.com
2. Uploadez ce dossier CarnetRestaurants sur GitHub
3. Ouvrez un Codespace (bouton vert "Code" > "Codespaces")
4. Dans le terminal, tapez :
   ```
   cd CarnetRestaurants
   chmod +x gradlew
   ./gradlew assembleDebug
   ```
5. Le fichier APK se trouve dans : app/build/outputs/apk/debug/app-debug.apk

### Étape 2 — Ouvrir le projet dans Android Studio
1. Lancez Android Studio
2. Cliquez "Open" et sélectionnez le dossier "CarnetRestaurants"
3. Attendez que Gradle synchronise (2-5 minutes la première fois)

### Étape 3 — Générer l'APK
1. Menu : **Build > Build Bundle(s) / APK(s) > Build APK(s)**
2. Attendez la compilation
3. Cliquez "locate" quand c'est terminé
4. L'APK est dans : `app/build/outputs/apk/debug/app-debug.apk`

### Étape 4 — Installer sur votre téléphone
1. Transférez l'APK sur votre téléphone (câble USB, email, WhatsApp)
2. Sur le téléphone : Paramètres > Sécurité > Autoriser les sources inconnues
3. Ouvrez le fichier APK et appuyez "Installer"
4. L'app "Mes Restaurants" apparaît sur votre écran !

---

## 📁 Structure du projet
```
CarnetRestaurants/
├── app/src/main/
│   ├── java/com/carnet/restaurants/
│   │   ├── model/          (Restaurant.kt, Commentaire.kt)
│   │   ├── database/       (AppDatabase, DAOs)
│   │   ├── repository/     (RestaurantRepository)
│   │   ├── viewmodel/      (ListeViewModel, DetailViewModel, FormViewModel)
│   │   ├── adapter/        (RestaurantAdapter, CommentaireAdapter)
│   │   └── ui/             (MainActivity, ListeFragment, DetailFragment, FormFragment)
│   └── res/
│       ├── layout/         (tous les layouts XML)
│       ├── navigation/     (nav_graph.xml)
│       ├── menu/           (menus)
│       └── values/         (strings, colors, themes)
```

---

## 🔧 Technologies utilisées
- **Kotlin** — langage natif Android
- **Room** — base de données SQLite locale (hors ligne)
- **ViewModel + LiveData** — architecture MVVM
- **Navigation Component** — navigation entre écrans
- **Glide** — chargement et affichage des photos
- **Material Design 3** — interface moderne
