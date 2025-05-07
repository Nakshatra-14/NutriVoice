# NutriVoice

**Scan Smart, Eat Wise: NutriVoice Guides.**

NutriVoice is an Android app that empowers everyone to make informed food choices. By scanning food barcodes, it delivers nutritional insights, diabetes-friendly recommendations, and personalized, multilingual guidance, inspired by the clarity of Japanese and Singaporean food packaging.

## About the Project

### Inspiration
Growing up, I was fascinated by the clarity of food packaging in Japan and Singapore, where every detail—nutritional values, EcoScore, and Nutri-Score—is printed clearly on the packet, empowering consumers to make informed choices. However, in many parts of the world, including my own community, people often feel confused when selecting food, overwhelmed by complex labels or unclear health impacts. This challenge hit close to home as I watched my father, who has diabetes, struggle to choose safe foods without guidance. Inspired by the simplicity of Japanese and Singaporean packaging and driven by a desire to help people like my father, I created **NutriVoice**—an app that scans food barcodes to deliver clear nutritional insights, diabetes-friendly recommendations, and personalized, multilingual guidance, making healthy eating accessible to everyone, everywhere.

### What I Learned
Building NutriVoice was a journey of growth in both technical and empathetic skills:
- **Technical Skills**: I deepened my expertise in Android development with Kotlin, mastering libraries like Retrofit for API calls to OpenFoodFacts, Room for local database storage, Moshi for JSON parsing, and the Gemini API for generating personalized narratives. I also learned to integrate Android’s Text-to-Speech (TTS) engine to support multiple languages (English, Hindi, Bengali), enhancing accessibility.
- **User-Centric Design**: I gained insights into designing for diverse users, from diabetic individuals needing precise nutritional guidance to non-diabetic users seeking general health insights. Understanding the importance of clear, TTS-friendly narratives taught me to prioritize accessibility.
- **Problem-Solving**: Tackling real-world challenges, like unreliable internet or complex nutritional data, helped me appreciate robust error handling and fallback mechanisms.
- **Empathy**: My father’s experience with diabetes taught me to approach development with empathy, ensuring the app feels like a trusted friend guiding users through food choices.

### How I Built NutriVoice
NutriVoice is an Android app built with Kotlin, designed to simplify food choices through barcode scanning and personalized guidance. Here’s how I brought it to life:
- **Core Features**:
  - **Barcode Scanning**: Users scan food barcodes to fetch detailed data from the OpenFoodFacts API, including nutritional values, Nutri-Score, EcoScore, and allergens.
  - **Nutritional Analysis**: The app calculates a diabetes suitability score based on glycemic index, carbs, sugars, and fiber, displaying results like “Excellent for Diabetes” to guide users.
  - **Personalized Narratives**: Using the Gemini API, NutriVoice generates 8-10 sentence narratives tailored to the user’s profile (name, age, gender, diabetes value), recommending portion sizes, meal pairings, and post-meal activities.
  - **Multilingual TTS**: Narratives are spoken in English, Hindi, or Bengali, making the app accessible to diverse users.
  - **User Profiles**: A Room database stores user data (e.g., diabetes value, preferences) to personalize recommendations.
- **Tech Stack**:
  - **Frontend**: Android XML layouts for clean UI, Glide for image loading, and Lottie (optional) for animations.
  - **Backend**: Retrofit with Moshi for API calls, Room for local storage of user profiles and scan history.
  - **AI**: Gemini API for narrative generation, integrated via OkHttp.
  - **Accessibility**: Android TTS for multilingual narration, with voice selection for clarity.
- **Development Process**:
  - I started by prototyping the barcode scanning feature, integrating ZXing for scanning and Retrofit for OpenFoodFacts API calls.
  - I designed the `DetailActivity` to display scan results, including nutritional data, scores, and narratives, inspired by the clarity of Japanese/Singaporean labels.
  - To address diabetes needs, I implemented a custom scoring algorithm (`DiabatesDetail`) to evaluate food suitability.
  - I added user profile storage with Room to enable personalization, drawing from my father’s need for tailored advice.
  - For accessibility, I integrated TTS with language selection (English, Hindi, Bengali) and ensured narratives were concise and TTS-friendly.
  - I polished the UI with a white-background loading dialog to blend with the app’s clean aesthetic, inspired by modern food packaging.

### Challenges Faced
Building NutriVoice came with several hurdles, each teaching me resilience and creativity:
- **API Reliability**: The OpenFoodFacts API occasionally returned incomplete data or failed due to network issues. I implemented robust error handling (e.g., fallback messages for “Product not found”) and cached recent scans in Room for offline access.
- **Gemini API Integration**: Generating consistent, TTS-friendly narratives was tricky, as the API sometimes produced short or irrelevant responses. I added retry logic (up to 3 attempts) and fallback narratives in multiple languages to ensure reliability.
- **Multilingual TTS**: Configuring Android’s TTS engine for Hindi and Bengali was challenging due to limited high-quality voices. I iterated on voice selection logic to prioritize Wavenet voices and adjusted pitch/speed for clarity.
- **Personalization**: Balancing personalization (e.g., using user profiles) with simplicity was tough. I refined the Gemini API prompt to incorporate user data (age, diabetes value) while keeping narratives concise and relevant.
- **UI Polish**: The initial loading dialog’s white-background GIF clashed with a transparent background, looking unprofessional. I redesigned it with a white window background and full-width layout to blend seamlessly, inspired by clean packaging aesthetics.
- **Global Usability**: Ensuring the app worked globally required testing with diverse barcodes and verifying that nutritional data was universally relevant, which involved extensive API testing.

### Why NutriVoice Matters
NutriVoice bridges the gap between complex food labels and user-friendly guidance, inspired by the clarity of Japanese and Singaporean packaging. Whether you’re managing diabetes like my father or simply seeking healthier choices, NutriVoice empowers you with instant nutritional insights, personalized advice, and multilingual narration. By making healthy eating accessible and engaging for everyone, anywhere, NutriVoice is more than an app—it’s a step toward a healthier, more sustainable world.

## Built With
- **Languages**: Kotlin, XML
- **Frameworks**: Android SDK, Retrofit, Room, Moshi, Glide, OkHttp
- **Platforms**: Android
- **Cloud Services**: None
- **Databases**: Room (SQLite-based local database for user profiles and scan history)
- **APIs**: OpenFoodFacts API (for nutritional data), Gemini API (for personalized narrative generation)
- **Other Technologies**: Android Text-to-Speech (TTS) for multilingual narration (English, Hindi, Bengali), ZXing (for barcode scanning)

## Getting Started

### Prerequisites
- **Android Studio**: Version 2023.3.1 or later (e.g., Flamingo).
- **JDK**: 17 or later.
- **Gemini API Key**: Obtain a key from [Google AI Studio](https://makersuite.google.com/) for narrative generation.
- **Android Device/Emulator**: API level 21 or higher (Lollipop+).

### Installation
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/[YourUsername]/NutriVoice.git

- **Repo Organization**:
  - Android project structure:
    ```
    NutriVoice/
    ├── app/
    │   ├── src/
    │   │   ├── main/
    │   │   │   ├── java/com/example/newscan/
    │   │   │   ├── res/
    │   │   │   └── AndroidManifest.xml
    │   └── build.gradle
    ├── LICENSE
    ├── README.md
    └── build.gradle
    ```
