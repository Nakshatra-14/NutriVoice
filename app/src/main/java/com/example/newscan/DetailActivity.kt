package com.example.newscanexp

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.newscanexp.data.DatabaseModule
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.net.UnknownHostException
import java.util.Locale

// RetrofitInstance object
object RetrofitInstance {
    private const val BASE_URL = "https://world.openfoodfacts.org/"

    private val moshi = Moshi.Builder()
        .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
        .build()

    private val logging = okhttp3.logging.HttpLoggingInterceptor().apply {
        level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ApiService::class.java)
    }
}

// Data classes
@JsonClass(generateAdapter = true)
data class ProductResponse(
    val status: Int,
    val code: String,
    val product: Product
)

@JsonClass(generateAdapter = true)
data class Product(
    val product_name: String,
    val quantity: String?,
    val nutrition_grades: String,
    val nutriments: Nutriments,
    val brands: String,
    val ecoscore_grade: String,
    val selected_images: SelectedImages,
    val nutriscore_data: NutriscoreData,
    val allergens_from_ingredients: String,
    val nutrient_levels: NutrientLevels? = null,
    val ecoscore_data: EcoscoreData? = null,
    val categories_hierarchy: List<String>? = emptyList()
)

@JsonClass(generateAdapter = true)
data class Nutriments(
    val sugars_100g: String? = "0",
    val carbohydrates_100g: String? = "0",
    val fat_100g: String? = "0",
    val salt_100g: String? = "0",
    val `energy-kcal_100g`: String? = "0",
    val `saturated-fat_100g`: String? = "0",
    val proteins_100g: String? = "0",
    val sodium_100g: String? = "0"
)

@JsonClass(generateAdapter = true)
data class NutrientLevels(
    val fat: String? = "unknown",
    val salt: String? = "unknown",
    val `saturated-fat`: String? = "unknown",
    val sugars: String? = "unknown"
)

@JsonClass(generateAdapter = true)
data class EcoscoreData(
    val agribalyse: Agribalyse? = null
)

@JsonClass(generateAdapter = true)
data class Agribalyse(
    val co2_total: Double? = 0.0
)

@JsonClass(generateAdapter = true)
data class SelectedImages(
    val front: Front
)

@JsonClass(generateAdapter = true)
data class Front(
    val display: Display
)

@JsonClass(generateAdapter = true)
data class Display(
    val en: String
)

@JsonClass(generateAdapter = true)
data class NutriscoreData(
    val fiber: String? = "0"
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>? = null
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null
)

interface ApiService {
    @GET("api/v2/product/{barcode}")
    suspend fun getProductDetails(@Path("barcode") barcode: String): ProductResponse
}

class DetailActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var textViewData: TextView
    private lateinit var textViewTitle: TextView
    private lateinit var textViewBrand: TextView
    private lateinit var scoreDetail: TextView
    private lateinit var imageViewProduct: ImageView
    private lateinit var diabetesInfo: TextView
    private lateinit var nutriScoreImg: ImageView
    private lateinit var nutriScoreTitle: TextView
    private lateinit var ecoScoreTitle: TextView
    private lateinit var ecoScoreImg: ImageView
    private lateinit var allergensInfo: TextView
    private lateinit var info: LinearLayout
    private lateinit var imgEndText: TextView
    private lateinit var narrativeTextView: TextView
    private lateinit var replayNarrativeButton: Button
    private lateinit var textToSpeech: TextToSpeech
    private var isTtsInitialized = false
    private var currentNarrative: String? = null
    private var selectedLanguage: String = "English"
    private lateinit var loadingDialog: AlertDialog
    private lateinit var mainContent: LinearLayout

    // Placeholder for Perplexity API integration (not active due to payment barrier)
    private val perplexityApiKey = "YOUR_PERPLEXITY_API_KEY_HERE" // Add Perplexity API key here

    /**
     * Placeholder function to fetch nutritional research using Perplexity API.
     * Not called in current version due to unavailable API key (payment required).
     * Planned to enhance narratives with real-time dietary insights.
     */
    private fun fetchPerplexityResearch(productName: String, userProfile: String): String {
        // Mock implementation for Perplexity API call
        /*
        val client = OkHttpClient()
        val mediaType = MediaType.parse("application/json")
        val requestBody = """
            {
                "model": "sonar-pro",
                "messages": [
                    {"role": "system", "content": "Provide concise nutritional advice."},
                    {"role": "user", "content": "Benefits of $productName for ${userProfile.diabetesValue} users"}
                ]
            }
        """.trimIndent()
        val request = Request.Builder()
            .url("https://api.perplexity.ai/chat/completions")
            .header("Authorization", "Bearer $perplexityApiKey")
            .header("Content-Type", "application/json")
            .post(RequestBody.create(mediaType, requestBody))
            .build()
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val json = response.body()?.string()
                // Parse JSON for research text (e.g., choices[0].message.content)
                return json ?: "Perplexity research unavailable"
            }
        }
        */
        // Fallback until API key is obtained
        return "Perplexity API research pending key activation"
    }

    private val okHttpClient = RetrofitInstance.client
    private val moshi = Moshi.Builder().add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory()).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)

        textViewTitle = findViewById(R.id.textViewTitle)
        textViewData = findViewById(R.id.textViewData)
        textViewBrand = findViewById(R.id.textViewBrand)
        scoreDetail = findViewById(R.id.scoreDetail)
        imageViewProduct = findViewById(R.id.imageViewProduct)
        diabetesInfo = findViewById(R.id.diabetisInfo)
        nutriScoreImg = findViewById(R.id.nutriScoreImg)
        nutriScoreTitle = findViewById(R.id.nutriScoreTitle)
        ecoScoreTitle = findViewById(R.id.ecoScoreTitle)
        ecoScoreImg = findViewById(R.id.ecoScoreImg)
        allergensInfo = findViewById(R.id.allergensInfo)
        info = findViewById(R.id.info)
        imgEndText = findViewById(R.id.imgEndText)
        narrativeTextView = findViewById(R.id.narrativeTextView)
        replayNarrativeButton = findViewById(R.id.replayNarrativeButton)
        mainContent = findViewById(R.id.mainContent)

        mainContent.visibility = View.GONE

        textToSpeech = TextToSpeech(this, this)

        replayNarrativeButton.setOnClickListener {
            currentNarrative?.let { speakNarrative(it) }
        }

        val barcode = intent.getStringExtra("BARCODE")
        if (barcode != null) {
            showLanguageSelectionDialog(barcode)
        } else {
            textViewData.text = "No barcode found"
            narrativeTextView.text = "No narrative available"
            mainContent.visibility = View.VISIBLE
            setResult(RESULT_CANCELED)
            finish()
        }
    }

//    private fun showLoadingDialog() {
//        val dialogView = layoutInflater.inflate(R.layout.dialog_loading, null)
//        val loadingGif = dialogView.findViewById<ImageView>(R.id.loadingGif)
//
//        Glide.with(this)
//            .asGif()
//            .load(R.drawable.loading)
//            .into(loadingGif)
//
//        loadingDialog = AlertDialog.Builder(this)
//            .setView(dialogView)
//            .setCancelable(false)
//            .create()
//
//        loadingDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
//        loadingDialog.window?.attributes?.gravity = android.view.Gravity.CENTER
//        loadingDialog.show()
//    }

    private fun showLoadingDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_loading, null)
        val loadingGif = dialogView.findViewById<ImageView>(R.id.loadingGif)

        Glide.with(this)
            .asGif()
            .load(R.drawable.loading)
            .into(loadingGif)

        loadingDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Set dialog window attributes
        loadingDialog.window?.setBackgroundDrawableResource(android.R.color.white)
        loadingDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND) // Remove dim background
        loadingDialog.window?.attributes?.gravity = android.view.Gravity.CENTER
        loadingDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        loadingDialog.show()
    }

    private fun hideLoadingDialog() {
        if (::loadingDialog.isInitialized && loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }

    private fun showLanguageSelectionDialog(barcode: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_language_selection, null)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.languageRadioGroup)
        val confirmButton = dialogView.findViewById<Button>(R.id.buttonConfirmLanguage)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.attributes?.gravity = android.view.Gravity.CENTER

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedLanguage = when (checkedId) {
                R.id.radioHindi -> "Hindi"
                R.id.radioBengali -> "Bengali"
                else -> "English"
            }
        }

        confirmButton.setOnClickListener {
            dialog.dismiss()
            showLoadingDialog()
            textToSpeech.shutdown()
            textToSpeech = TextToSpeech(this, this)
            fetchProductDetails(barcode)
        }

        dialog.show()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val locale = when (selectedLanguage) {
                "Hindi" -> Locale("hi", "IN")
                "Bengali" -> Locale("bn", "IN")
                else -> Locale.US
            }
            val result = textToSpeech.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                textToSpeech.setLanguage(Locale.US)
                selectedLanguage = "English"
                narrativeTextView.text = when (locale) {
                    Locale("hi", "IN") -> "त्रुटि: हिन्दी समर्थित नहीं, अंग्रेजी का उपयोग कर रहे हैं"
                    Locale("bn", "IN") -> "ত্রুটি: বাংলা সমর্থিত নয়, ইংরেজি ব্যবহার করা হচ্ছে"
                    else -> "Error: Selected language not supported, using English"
                }
            } else {
                val voices = textToSpeech.voices
                voices?.forEach {
                    println("Available TTS Voice: ${it.name}, Locale: ${it.locale}, Quality: ${it.quality}")
                }
                val preferredVoice = voices?.find {
                    when (selectedLanguage) {
                        "Hindi" -> it.locale == Locale("hi", "IN") && (it.name.contains("Wavenet", true) || it.quality >= 400)
                        "Bengali" -> it.locale == Locale("bn", "IN") && (it.name.contains("Wavenet", true) || it.quality >= 400)
                        else -> it.locale == Locale.US && (it.name.contains("Wavenet", true) || it.name.contains("en-us-x-iob-local", true) || it.quality >= 400)
                    }
                }
                if (preferredVoice != null) {
                    textToSpeech.setVoice(preferredVoice)
                    println("Selected TTS Voice: ${preferredVoice.name}, Locale: ${preferredVoice.locale}, Quality: ${preferredVoice.quality}")
                } else {
                    println("No preferred voice found for $selectedLanguage, using default for $locale")
                }
            }
            textToSpeech.setPitch(1.0f)
            textToSpeech.setSpeechRate(0.9f)
            isTtsInitialized = true
            currentNarrative?.let { speakNarrative(it) }
        } else {
            narrativeTextView.text = "Error: TTS initialization failed"
            isTtsInitialized = false
        }
    }

    private fun speakNarrative(text: String) {
        if (!isTtsInitialized) return
        textToSpeech.stop()
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "NarrativeUtterance")
    }

    override fun onDestroy() {
        textToSpeech.stop()
        textToSpeech.shutdown()
        hideLoadingDialog()
        super.onDestroy()
    }

    private suspend fun generateNarrative(foodInfo: String, retryCount: Int = 0): String {
        val maxRetries = 3
        val language = when (selectedLanguage) {
            "Hindi" -> "Hindi (हिन्दी) using Devanagari script"
            "Bengali" -> "Bengali (বাংলা) using Bengali script"
            else -> "English"
        }

        // Fetch user profile
        val db = DatabaseModule.provideDatabase(this)
        val profile = db.userProfileDao().getProfile()
        val profileInfo = if (profile != null) {
            """
                User Profile:
                Name: ${profile.name},
                Age: ${profile.age},
                Gender: ${profile.gender},
                Current Diabetes Value: ${profile.diabetesValue} mg/dL,
                About: ${profile.about},
                Other Info: ${profile.otherInfo}
            """.trimIndent()
        } else {
            "User Profile: Not available"
        }

        val prompt = """
            you are integrated in a app, where you get details of food ingredients and user detail do the task as following in $language language
            write a narrative about this food: $foodInfo.
            Consider the user's profile: $profileInfo.
            The narrative must have exactly 10 to 15 sentences,
            tell Best time to eat (e.g., lunch) and portion size (e.g., 1/4 cup dry), adjusted based on user's age and diabetes value,
            tell Nutrient concerns for blood sugar (e.g., carbs), tailored to user's diabetes value,
            tell Health benefits (e.g., protein content) in a basic details manner,
            tell GI index of the food (Excellent, Good, Moderate, Poor),
            tell A low-GI Indian meal pairing if needed, considering user's gender or preferences,
            tell EcoScore grade (e.g., A, F) and CO2 total (e.g., 4.1 kg CO2/kg) in one sentence,
            tell A post-meal activity tip (e.g., walk), personalized based on user's age,
            also share your thought on the food for the user
            Use a warm, concise, TTS-friendly tone.
            Use simple terms and normal words to let the user understand better as if they are speaking with a friend.
            Thank the user at the end for using NutriVoice, addressing them by name if available.
        """.trimIndent()

        val payload = """{"contents":[{"parts":[{"text":"$prompt"}]}]}"""
        val requestBody = payload.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(fetchPerplexityResearch("Product Name", "Nax"))
            .post(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = okHttpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: return@withContext "Error: Empty response body"
                    println("Gemini API Response: $responseBody")
                    val adapter = moshi.adapter(GeminiResponse::class.java)
                    val geminiResponse = adapter.fromJson(responseBody)
                    val narrative = geminiResponse?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                        ?.replace("\\n", "\n")?.replace("\\\"", "\"") ?: "Error: No narrative text found"
                    println("Parsed Narrative: $narrative")
                    val sentenceCount = narrative.split("[.?!]".toRegex()).filter { it.trim().isNotEmpty() }.size
                    if (sentenceCount < 4 && retryCount < maxRetries) {
                        println("Narrative too short ($sentenceCount sentences), retrying (${retryCount + 1}/$maxRetries)")
                        generateNarrative(foodInfo, retryCount + 1)
                    } else {
                        narrative
                    }
                } else {
                    val errorBody = response.body?.string() ?: "No error details"
                    println("Gemini API Error: Status ${response.code}, Body: $errorBody")
                    if (retryCount < maxRetries) {
                        println("Retrying due to API error (${retryCount + 1}/$maxRetries)")
                        generateNarrative(foodInfo, retryCount + 1)
                    } else {
                        "Error: Gemini API failed with status ${response.code}: $errorBody"
                    }
                }
            } catch (e: UnknownHostException) {
                "Error: No internet connection"
            } catch (e: java.io.IOException) {
                "Error: Network error - ${e.message}"
            } catch (e: Exception) {
                println("Gemini API Exception: ${e.javaClass.name}, Message: ${e.message}")
                when (selectedLanguage) {
                    "Hindi" -> """
                        इस भोजन को दोपहर के भोजन में 1/4 कप (सूखा) मात्रा में लें। 
                        ब्लड शुगर को नियंत्रित करने के लिए 75 ग्राम कार्ब्स पर ध्यान दें। 
                        इसमें 9.7 ग्राम प्रोटीन है, जो संतुलित आहार में मदद करता है। 
                        इसका ग्लाइसेमिक इंडेक्स अच्छा है, जो डायबिटीज के लिए ठीक है। 
                        इसे मूंग दाल और भिंडी के साथ खाएं। 
                        इसका F इकोस्कोर और 4.1 किग्रा CO2/किग्रा है, इसलिए स्थानीय अनाज चुनें। 
                        खाने के बाद 10 मिनट की सैर करें। 
                        ${profile?.name ?: "आप"} का ध्यान रखने के लिए धन्यवाद!
                    """.trimIndent()
                    "Bengali" -> """
                        এই খাবারটি দুপুরের খাবারে ১/৪ কাপ (শুকনো) পরিমাণে খান। 
                        রক্তে শর্করা নিয়ন্ত্রণে রাখতে ৭৫ গ্রাম কার্বসের উপর নজর রাখুন। 
                        এতে ৯.৭ গ্রাম প্রোটিন আছে, যা সুষম খাদ্যে সাহায্য করে। 
                        এর গ্লাইসেমিক ইনডেক্স ভালো, যা ডায়াবেটিসের জন্য ঠিক আছে। 
                        মুগ ডাল এবং ভিন্ডির সাথে খান। 
                        এর F ইকোস্কোর এবং ৪.১ কেজি CO2/কেজি আছে, তাই স্থানীয় শস্য বেছে নিন। 
                        খাওয়ার পরে ১০ মিনিট হাঁটুন। 
                        ${profile?.name ?: "আপনার"} যত্ন নেওয়ার জন্য ধন্যবাদ!
                    """.trimIndent()
                    else -> """
                        Enjoy this food at lunch in a 1/4 cup dry portion. 
                        Monitor its 75g carbs to manage blood sugar. 
                        It has 9.7g protein, which supports a balanced diet. 
                        Its glycemic index is Good, suitable for diabetes. 
                        Pair with moong dal and bhindi. 
                        Its F EcoScore and 4.1 kg CO2/kg suggest choosing local grains. 
                        Take a 10-minute walk after eating. 
                        Thank you for taking care, ${profile?.name ?: "you"}!
                    """.trimIndent()
                }
            }
        }
    }

    fun DiabatesDetail(giIndex: Double, carbohydrates: Double, sugar: Double, fiber: Double, fat: Double): Double {
        var score: Double = 0.0
        var n = 0
        n = if (giIndex <= 55) 2
        else if (giIndex <= 69) 1
        else 0
        score += n

        n = if (carbohydrates.toInt() <= 10) 2
        else if (carbohydrates.toInt() <= 20) 1
        else 0
        score += n

        n = if (sugar.toInt() <= 5) 2
        else if (sugar.toInt() <= 10) 1
        else 0
        score += n

        n = if (fiber.toInt() >= 3) 2
        else if (fiber.toInt() >= 1) 1
        else 0
        score += n

        n = if (fat.toInt() <= 10) 1
        else 0
        score += n
        return score
    }

    private fun fetchProductDetails(barcode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getProductDetails(barcode)
                withContext(Dispatchers.Main) {
                    if (response.status == 1) {
                        val product = response.product
                        val imageUrl = product.selected_images.front.display.en
                        Glide.with(this@DetailActivity)
                            .load(imageUrl as String?)
                            .into(imageViewProduct)
                        imgEndText.text = "Click on the image\nto Enlarge"
                        val name = "Name: ${product.product_name ?: "Unknown Product"}"
                        textViewTitle.text = name
                        val brand = "Brand: ${product.brands}\nQuantity: ${product.quantity ?: "Unknown Brand"}"
                        textViewBrand.text = brand

                        val flag: Boolean = product.allergens_from_ingredients.isNotEmpty()
                        allergensInfo.text = if (flag) {
                            "☑ Allergens: Yes, for few User, They are:\n${product.allergens_from_ingredients}"
                        } else {
                            "\t\t☒ Allergens: None"
                        }

                        val carb = product.nutriments.carbohydrates_100g?.toDoubleOrNull() ?: 0.0
                        val sugar = product.nutriments.sugars_100g?.toDoubleOrNull() ?: 0.0
                        val fiber = product.nutriscore_data.fiber?.toDoubleOrNull() ?: 0.0
                        val fat = product.nutriments.fat_100g?.toDoubleOrNull() ?: 0.0
                        val energy = product.nutriments.`energy-kcal_100g`?.toDoubleOrNull() ?: 0.0
                        val sodium = product.nutriments.sodium_100g?.toDoubleOrNull() ?: 0.0
                        val co2Total = product.ecoscore_data?.agribalyse?.co2_total ?: 0.0

                        val firstCategory = product.categories_hierarchy?.firstOrNull()
                            ?.removePrefix("en:")?.replace("-", " ")?.capitalizeWords() ?: "Unknown"

                        val gi = if (carb > 0) (((sugar) + ((carb - fiber - sugar) * 0.5)) / carb) * 100 else 0.0
                        val score = DiabatesDetail(gi, carb, sugar, fiber, fat)
                        diabetesInfo.setOnClickListener {
                            val intent = Intent(this@DetailActivity, DiabetisScoreDetailActivity::class.java)
                            intent.putExtra("SCORE", score)
                            startActivity(intent)
                        }
                        val diabatisDetail = when {
                            score >= 8 -> "\t\t☑ Excellent for Diabetes\n"
                            score >= 5 -> "\t\t☑ Good for Diabetes\n"
                            score >= 3 -> "\t\t☑ Moderate for Diabetes\n"
                            else -> "\t\t☒ Poor for Diabetes\n"
                        }
                        diabetesInfo.text = diabatisDetail

                        val nutrientLevels = product.nutrient_levels
                        val spannable = SpannableStringBuilder()
                        val items = listOf(
                            "Glycemic Index(GI)" to "${gi.toInt()}",
                            "Category" to firstCategory,
                            "Energy" to "${product.nutriments.`energy-kcal_100g` ?: "Unknown"} kcal",
                            "Protein" to "${product.nutriments.proteins_100g ?: "Unknown"} g",
                            "Carbohydrate" to "${product.nutriments.carbohydrates_100g ?: "Unknown"} g",
                            "Fat" to "${product.nutriments.fat_100g ?: "Unknown"} g (Level: ${nutrientLevels?.fat ?: "Unknown"})",
                            "Saturated Fat" to "${product.nutriments.`saturated-fat_100g` ?: "Unknown"} g (Level: ${nutrientLevels?.`saturated-fat` ?: "Unknown"})",
                            "Fiber" to "${product.nutriscore_data.fiber ?: "Unknown"} g",
                            "Sugars" to "${product.nutriments.sugars_100g ?: "Unknown"} g (Level: ${nutrientLevels?.sugars ?: "Unknown"})",
                            "Salt" to "${product.nutriments.salt_100g ?: "Unknown"} g (Level: ${nutrientLevels?.salt ?: "Unknown"})",
                            "Sodium" to "${sodium.format(2)} mg",
                            "CO2 Impact" to "${co2Total.format(2)} kg CO2/kg",
                            "Additives" to "None"
                        )
                        items.forEachIndexed { index, (title, value) ->
                            val line = "$title: $value"
                            spannable.append(line)
                            val colonIndex = spannable.indexOf(":", spannable.length - line.length)
                            if (colonIndex != -1) {
                                spannable.setSpan(
                                    StyleSpan(Typeface.BOLD),
                                    spannable.length - line.length,
                                    spannable.length - line.length + title.length,
                                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                            }
                            if (index < items.size - 1) spannable.append("\n")
                        }
                        textViewData.text = spannable

                        val diabetesLevel = when {
                            score >= 8 -> "Excellent"
                            score >= 5 -> "Good"
                            score >= 3 -> "Moderate"
                            else -> "Poor"
                        }
                        val foodInfo = """
                            Food: ${product.product_name ?: "Unknown"},
                            nutrients: calories:${product.nutriments.`energy-kcal_100g` ?: "0"} kcal,
                            carbs:${product.nutriments.carbohydrates_100g ?: "0"}g,
                            sugars:${product.nutriments.sugars_100g ?: "0"}g,
                            fiber:${product.nutriscore_data.fiber ?: "0"}g,
                            fat:${product.nutriments.fat_100g ?: "0"}g,
                            sodium:${sodium}mg,
                            glycemic_index: $diabetesLevel,
                            ecoscore_grade: ${product.ecoscore_grade.uppercase()},
                            co2_total: ${co2Total.format(2)} kg CO2/kg
                        """.trimIndent().replace("\n", " ")
                        val narrative = generateNarrative(foodInfo)
                        currentNarrative = narrative
                        narrativeTextView.text = narrative
                        replayNarrativeButton.visibility = View.VISIBLE
                        speakNarrative(narrative)

                        nutriScoreTitle.text = "\t\tNUTRI-SCORE"
                        val nutriDetail = when (product.nutrition_grades) {
                            "a" -> getString(R.string.nutriScore_a)
                            "b" -> getString(R.string.nutriScore_b)
                            "c" -> getString(R.string.nutriScore_c)
                            "d" -> getString(R.string.nutriScore_d)
                            else -> getString(R.string.nutriScore_e)
                        }
                        scoreDetail.text = nutriDetail
                        scoreDetail.setOnClickListener {
                            val intent = Intent(this@DetailActivity, NutriInfoActivity::class.java)
                            intent.putExtra("NUTRI-SCORE", product.nutrition_grades)
                            startActivity(intent)
                        }
                        val nutriScore = when (product.nutrition_grades) {
                            "a" -> R.drawable.nutriscore_a
                            "b" -> R.drawable.nutriscore_b
                            "c" -> R.drawable.nutriscore_c
                            "d" -> R.drawable.nutriscore_d
                            else -> R.drawable.nutriscore_e
                        }
                        Glide.with(this@DetailActivity)
                            .load(nutriScore)
                            .into(nutriScoreImg)

                        ecoScoreTitle.text = "ECO\nSCORE"
                        val ecoScore = when (product.ecoscore_grade) {
                            "a" -> R.drawable.ecoscore_a
                            "b" -> R.drawable.ecoscore_b
                            "c" -> R.drawable.ecoscore_c
                            "d" -> R.drawable.ecoscore_d
                            else -> R.drawable.ecoscore_e
                        }
                        Glide.with(this@DetailActivity)
                            .load(ecoScore)
                            .into(ecoScoreImg)

                        imageViewProduct.setOnClickListener {
                            val intent = Intent(this@DetailActivity, ViewImgActivity::class.java)
                            intent.putExtra("IMG-URL", imageUrl as String?)
                            startActivity(intent)
                        }

                        info.setOnClickListener {
                            val intent = Intent(this@DetailActivity, InfoActivity::class.java)
                            startActivity(intent)
                        }

                        hideLoadingDialog()
                        mainContent.visibility = View.VISIBLE

                        // Return result to MainActivity
                        val resultIntent = Intent()
                        resultIntent.putExtra("BARCODE", barcode)
                        resultIntent.putExtra("PRODUCT_NAME", product.product_name ?: "Unknown Product")
                        setResult(RESULT_OK, resultIntent)
                    } else {
                        textViewData.text = "Product not found"
                        narrativeTextView.text = "No narrative available"
                        hideLoadingDialog()
                        mainContent.visibility = View.VISIBLE
                        setResult(RESULT_CANCELED)
                    }
                }
            } catch (e: UnknownHostException) {
                withContext(Dispatchers.Main) {
                    textViewData.text = "\t\tNetwork error: Please check your\n                 internet connection."
                    narrativeTextView.text = "Error: No internet connection"
                    hideLoadingDialog()
                    mainContent.visibility = View.VISIBLE
                    setResult(RESULT_CANCELED)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    textViewData.text = "\t\tAn error occurred:\n      ${e.message}"
                    narrativeTextView.text = "Error: Failed to generate narrative"
                    hideLoadingDialog()
                    mainContent.visibility = View.VISIBLE
                    setResult(RESULT_CANCELED)
                }
            }
        }
    }

    private fun Double.format(digits: Int) = "%.${digits}f".format(this)

    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize() }
}

fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}
