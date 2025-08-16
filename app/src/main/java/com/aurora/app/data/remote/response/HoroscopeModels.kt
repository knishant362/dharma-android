package com.aurora.app.data.remote.response

import com.google.gson.annotations.SerializedName

data class ZodiacSign(
    val hindiName: String,
    val englishName: String,
    val urlPath: String,
    val dateRange: String = "",
    val iconUrl: String = ""
)

data class MainPrediction(
    val hindi: String,
    @SerializedName("english_translation")
    val englishTranslation: String = ""
)

data class CategoryPrediction(
    val hindi: String,
    @SerializedName("english_translation")
    val englishTranslation: String = ""
)

data class CategoryPredictions(
    @SerializedName("love_relationship")
    val loveRelationship: CategoryPrediction? = null,
    @SerializedName("health_wellbeing")
    val healthWellbeing: CategoryPrediction? = null,
    @SerializedName("money_finance")
    val moneyFinance: CategoryPrediction? = null,
    @SerializedName("career_business")
    val careerBusiness: CategoryPrediction? = null
)

data class ExtractedMetadata(
    @SerializedName("zodiac_icon")
    val zodiacIcon: String = "",
    @SerializedName("promotional_links")
    val promotionalLinks: List<String> = emptyList()
)

data class HoroscopeData(
    @SerializedName("zodiac_sign")
    val zodiacSign: String,
    val date: String,
    val language: String = "Hindi",
    val source: String = "Sanatan Dharam",
    @SerializedName("main_prediction")
    val mainPrediction: MainPrediction,
    @SerializedName("category_predictions")
    val categoryPredictions: CategoryPredictions,
    @SerializedName("key_highlights")
    val keyHighlights: List<String> = emptyList(),
    @SerializedName("warnings_advice")
    val warningsAdvice: List<String> = emptyList(),
    @SerializedName("extracted_metadata")
    val extractedMetadata: ExtractedMetadata = ExtractedMetadata()
)
