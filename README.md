# My Museum Adventure

**My Museum Adventure** is an Android application developed as part of the **Human Computer Interaction** course (A.Y. 2023/2024) at **Politecnico di Torino**, taught by **Professor Luigi De Russis**.

The app aims to transform the museum visit into an engaging and interactive experience by combining traditional exploration with gamified elements like themed tours and riddles.

## 👥 Team Members
- **Calabrese Flavia**
- **Di Santo Francesco**
- **Giugliano Salvatore**
- **Rosace Giovanna**

## 🌟 Key Features

### 🏛️ Themed Tours
Users can choose from various narrative themes to guide their visit. Each theme provides a unique perspective on the museum's collection:
- **Adventure**
- **Fantasy**
- **Horror**
- **Romantic**
- **Sci-fi**
- **Comic**

### 🧩 Riddles & Gamification
The visit is structured as a quest. Users must solve riddles to find the next artwork in their tour. Solving riddles rewards players with medals and achievements, encouraging active participation.

### 📷 Artwork Scanning
Using the device's camera, users can scan artworks to verify if they have found the correct solution to a riddle. The app uses image recognition (via external API integration) to identify paintings and sculptures.

### 🗺️ Interactive Map
A planimetry of the museum helps users navigate through different zones. It highlights interactive areas and allows users to report classification errors, leveraging crowdsourced data to improve map accuracy.

### 🎧 Multimedia Content
Each artwork is accompanied by detailed descriptions and audio guides (available in multiple languages, including English and Italian) to provide a rich educational experience.

## 🛠️ Technical Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Local Database**: Room (for storing themes, riddles, and zone data)
- **Camera**: CameraX & Accompanist Permissions
- **Networking**: OkHttp (for image recognition API calls)
- **Navigation**: Jetpack Navigation

## 📁 Project Structure
- `app/src/main/java`: Source code organized by feature (artwork, camera, riddles, themes, zone).
- `app/src/main/res/raw`: JSON data files for artworks, riddles, and zones, along with audio guide assets.
- `app/src/main/res/drawable`: UI assets including custom medals and icons for different themes.
- `Report.pdf`: Detailed documentation of the design process, usability testing, and project requirements.
- `Usability Test.pdf`: Results and analysis of the usability testing sessions.

## 🚀 Getting Started 
1. Clone the repository.
2. Open the project in **Android Studio**.
3. Build and run the app on an Android device or emulator (API level 24 or higher recommended).
4. Ensure camera permissions are granted for the artwork scanning feature.

---
*Developed for the Human Computer Interaction course at Politecnico di Torino.*
