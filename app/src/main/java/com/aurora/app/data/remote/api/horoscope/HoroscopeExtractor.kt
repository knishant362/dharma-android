package com.aurora.app.data.remote.api.horoscope

import com.aurora.app.data.remote.response.CategoryPrediction
import com.aurora.app.data.remote.response.CategoryPredictions
import com.aurora.app.data.remote.response.ExtractedMetadata
import com.aurora.app.data.remote.response.HoroscopeData
import com.aurora.app.data.remote.response.MainPrediction
import com.aurora.app.data.remote.response.ZodiacSign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import timber.log.Timber
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HoroscopeExtractor {

    private val client = OkHttpClient()

    suspend fun extractHoroscopeData(url: String): Result<HoroscopeData> =
        withContext(Dispatchers.IO) {
            try {
                val html = fetchHtmlContent(url)
                val document = Jsoup.parse(html)
                val horoscopeData = parseHoroscopeData(document, url)
                Result.success(horoscopeData)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun extractZodiacSigns(baseUrl: String): List<ZodiacSign> =
        withContext(Dispatchers.IO) {
            try {
                // Fetch the main horoscope page
                val html = fetchHtmlContent(baseUrl)
                val document = Jsoup.parse(html)

                val zodiacSigns = mutableListOf<ZodiacSign>()

                // Method 1: Look for zodiac sign selection elements
                val signSelectors = listOf(
                    "a[href*='/daily-horoscope/']", // Links to daily horoscope pages
                    ".zodiac-sign", // Common zodiac sign class
                    ".sign-item", // Sign items
                    "select[name*='sign'] option", // Dropdown options
                    ".horoscope-sign-list a", // Horoscope sign list
                    "div:contains(राशि) a", // Divs containing "राशि" with links
                    ".sign-selector a" // Sign selector links
                )

                for (selector in signSelectors) {
                    val elements = document.select(selector)
                    for (element in elements) {
                        val zodiacSign = parseZodiacSignElement(element)
                        if (zodiacSign != null && !zodiacSigns.any { it.englishName == zodiacSign.englishName }) {
                            zodiacSigns.add(zodiacSign)
                        }
                    }

                    // If we found signs, break early
                    if (zodiacSigns.isNotEmpty()) break
                }

                // Method 2: If no signs found, try to extract from text content
                if (zodiacSigns.isEmpty()) {
                    zodiacSigns.addAll(extractZodiacFromText(document))
                }

                // Method 3: Fallback to predefined list with website validation
                if (zodiacSigns.isEmpty()) {
                    zodiacSigns.addAll(getDefaultZodiacSigns())
                }

                zodiacSigns

            } catch (e: Exception) {
                // Return default list if extraction fails
                getDefaultZodiacSigns()
            }
        }

    private fun parseZodiacSignElement(element: org.jsoup.nodes.Element): ZodiacSign? {
        val href = element.attr("href")
        val text = element.text().trim()

        // Extract zodiac sign from URL path
        val zodiacPattern = "/daily-horoscope/([a-z]+)/?".toRegex()
        val match = zodiacPattern.find(href)

        if (match != null) {
            val englishName = match.groupValues[1]
            val hindiName = extractHindiNameFromText(text, englishName)
            val iconUrl = element.select("img").attr("src")

            return ZodiacSign(
                hindiName = hindiName,
                englishName = englishName,
                urlPath = href,
                iconUrl = iconUrl
            )
        }

        return null
    }

    private fun extractZodiacFromText(document: Document): List<ZodiacSign> {
        val zodiacSigns = mutableListOf<ZodiacSign>()
        val text = document.text()

        // Look for zodiac signs mentioned in text
        val zodiacMappings = mapOf(
            "मेष" to "aries",
            "वृषभ" to "taurus",
            "मिथुन" to "gemini",
            "कर्क" to "cancer",
            "सिंह" to "leo",
            "कन्या" to "virgo",
            "तुला" to "libra",
            "वृश्चिक" to "scorpio",
            "धनु" to "sagittarius",
            "मकर" to "capricorn",
            "कुम्भ" to "aquarius",
            "मीन" to "pisces"
        )

        for ((hindi, english) in zodiacMappings) {
            if (text.contains(hindi)) {
                zodiacSigns.add(
                    ZodiacSign(
                        hindiName = "$hindi राशि",
                        englishName = english,
                        urlPath = "/hindi/horoscopes/daily-horoscope/$english/"
                    )
                )
            }
        }

        return zodiacSigns
    }

    private fun extractHindiNameFromText(text: String, englishName: String): String {
        val zodiacMappings = mapOf(
            "aries" to "मेष राशि",
            "taurus" to "वृषभ राशि",
            "gemini" to "मिथुन राशि",
            "cancer" to "कर्क राशि",
            "leo" to "सिंह राशि",
            "virgo" to "कन्या राशि",
            "libra" to "तुला राशि",
            "scorpio" to "वृश्चिक राशि",
            "sagittarius" to "धनु राशि",
            "capricorn" to "मकर राशि",
            "aquarius" to "कुम्भ राशि",
            "pisces" to "मीन राशि"
        )

        // Try to extract Hindi name from text, fallback to mapping
        val hindiPattern = "[${
            zodiacMappings.values.joinToString("").toSet().joinToString("")
        }]+\\s*राशि".toRegex()
        val match = hindiPattern.find(text)

        return match?.value ?: zodiacMappings[englishName]
        ?: "${englishName.capitalize(Locale.ROOT)} राशि"
    }

    private fun getDefaultZodiacSigns(): List<ZodiacSign> {
        return listOf(
            ZodiacSign(
                "मेष राशि",
                "aries",
                "/hindi/horoscopes/daily-horoscope/aries/",
                "मार्च 21-अप्रैल 20"
            ),
            ZodiacSign(
                "वृषभ राशि",
                "taurus",
                "/hindi/horoscopes/daily-horoscope/taurus/",
                "अप्रैल 21-मई 21"
            ),
            ZodiacSign(
                "मिथुन राशि",
                "gemini",
                "/hindi/horoscopes/daily-horoscope/gemini/",
                "मई 22-जून 21"
            ),
            ZodiacSign(
                "कर्क राशि",
                "cancer",
                "/hindi/horoscopes/daily-horoscope/cancer/",
                "जून 22-जुलाई 22"
            ),
            ZodiacSign(
                "सिंह राशि",
                "leo",
                "/hindi/horoscopes/daily-horoscope/leo/",
                "जुलाई 23-अगस्त 23"
            ),
            ZodiacSign(
                "कन्या राशि",
                "virgo",
                "/hindi/horoscopes/daily-horoscope/virgo/",
                "अगस्त 24-सितंबर 22"
            ),
            ZodiacSign(
                "तुला राशि",
                "libra",
                "/hindi/horoscopes/daily-horoscope/libra/",
                "सितंबर 23-अक्टूबर 23"
            ),
            ZodiacSign(
                "वृश्चिक राशि",
                "scorpio",
                "/hindi/horoscopes/daily-horoscope/scorpio/",
                "अक्टूबर 24-नवंबर 22"
            ),
            ZodiacSign(
                "धनु राशि",
                "sagittarius",
                "/hindi/horoscopes/daily-horoscope/sagittarius/",
                "नवंबर 23-दिसंबर 21"
            ),
            ZodiacSign(
                "मकर राशि",
                "capricorn",
                "/hindi/horoscopes/daily-horoscope/capricorn/",
                "दिसंबर 22-जनवरी 20"
            ),
            ZodiacSign(
                "कुम्भ राशि",
                "aquarius",
                "/hindi/horoscopes/daily-horoscope/aquarius/",
                "जनवरी 21-फ़रवरी 18"
            ),
            ZodiacSign(
                "मीन राशि",
                "pisces",
                "/hindi/horoscopes/daily-horoscope/pisces/",
                "फरवरी 19-मार्च 20"
            )
        )
    }

    private suspend fun fetchHtmlContent(url: String): String = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .addHeader("Accept-Language", "hi-IN,hi;q=0.9,en-US;q=0.8,en;q=0.7")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            response.body?.string() ?: throw IOException("Empty response body")
        }
    }

    private fun parseHoroscopeData(document: Document, url: String): HoroscopeData {
        // Extract zodiac sign from URL or content
        val zodiacSign = extractZodiacSign(url, document)

        // Extract date (current date if not found)
        val date = extractDate(document)

        // Extract main prediction
        val mainPrediction = extractMainPrediction(document)

        // Extract category predictions
        val categoryPredictions = extractCategoryPredictions(document)

        // Extract highlights and advice
        val highlights = extractKeyHighlights(document)
        val advice = extractWarningsAdvice(document)

        // Extract metadata
        val metadata = extractMetadata(document)

        val horoscopeData = HoroscopeData(
            zodiacSign = zodiacSign,
            date = date,
            mainPrediction = mainPrediction,
            categoryPredictions = categoryPredictions,
            keyHighlights = highlights,
            warningsAdvice = advice,
            extractedMetadata = metadata
        )
        Timber.e("parseHoroscopeData: ${horoscopeData}")
        return horoscopeData
    }

    private fun extractZodiacSign(url: String, document: Document): String {
        // Extract from URL path
        val zodiacFromUrl = when {
            url.contains("/aries/") -> "मेष राशि (Aries)"
            url.contains("/taurus/") -> "वृषभ राशि (Taurus)"
            url.contains("/gemini/") -> "मिथुन राशि (Gemini)"
            url.contains("/cancer/") -> "कर्क राशि (Cancer)"
            url.contains("/leo/") -> "सिंह राशि (Leo)"
            url.contains("/virgo/") -> "कन्या राशि (Virgo)"
            url.contains("/libra/") -> "तुला राशि (Libra)"
            url.contains("/scorpio/") -> "वृश्चिक राशि (Scorpio)"
            url.contains("/sagittarius/") -> "धनु राशि (Sagittarius)"
            url.contains("/capricorn/") -> "मकर राशि (Capricorn)"
            url.contains("/aquarius/") -> "कुम्भ राशि (Aquarius)"
            url.contains("/pisces/") -> "मीन राशि (Pisces)"
            else -> "Unknown"
        }

        // Try to extract from document title or headings as fallback
        return if (zodiacFromUrl != "Unknown") {
            zodiacFromUrl
        } else {
            document.select("h1, h2, title").text().takeIf { it.isNotEmpty() } ?: "राशि"
        }
    }

    private fun extractDate(document: Document): String {
        // Strategy 1: Look in horoscope-description
        val horoscopeDescription = document.select(".horoscope-description p").firstOrNull()
        if (horoscopeDescription != null) {
            val dateText = horoscopeDescription.text().trim()
            val datePattern = "\\d{1,2}-\\d{1,2}-\\d{4}".toRegex()
            val match = datePattern.find(dateText)
            if (match != null) {
                return match.value
            }
        }

        // Strategy 2: Look in horoscope-date class
        val horoscopeDateDiv = document.select(".horoscope-date").firstOrNull()
        if (horoscopeDateDiv != null) {
            val dateText = horoscopeDateDiv.text()
            val datePattern = "\\d{1,2}-\\d{1,2}-\\d{4}".toRegex()
            val match = datePattern.find(dateText)
            if (match != null) {
                return match.value
            }
        }

        // Strategy 3: General search in document with validation
        val fullText = document.text()
        val datePattern = "\\d{1,2}-\\d{1,2}-\\d{4}".toRegex()
        val matches = datePattern.findAll(fullText)

        for (match in matches) {
            val dateStr = match.value
            val parts = dateStr.split("-")
            if (parts.size == 3) {
                val day = parts[0].toIntOrNull()
                val month = parts[1].toIntOrNull()
                val year = parts[2].toIntOrNull()

                // Validate if it's a reasonable date
                if (day != null && month != null && year != null &&
                    day in 1..31 && month in 1..12 && year > 2020
                ) {
                    return dateStr
                }
            }
        }

        // Fallback to current date
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun extractMainPrediction(document: Document): MainPrediction {
        // Method 1: Use precise CSS selectors based on HTML structure
        val preciseResult = extractUsingCSSSelectors(document)
        if (preciseResult.isNotEmpty()) {
            return MainPrediction(hindi = preciseResult)
        }

        // Method 2: Fallback to content-based extraction
        val fallbackResult = extractUsingContentPatterns(document)

        return MainPrediction(
            hindi = fallbackResult.takeIf { it.isNotEmpty() } ?: "मुख्य भविष्यवाणी उपलब्ध नहीं है।"
        )
    }

    private fun extractUsingCSSSelectors(document: Document): String {
        // Strategy 1: Direct CSS selector for horoscope content
        val horoscopeContentSelectors = listOf(
            ".horoscope-content p:not(.horoscope-strip)",  // Main content, exclude promotional strip
            ".horoscope-content div:not(.horoscope-date) p", // Content paragraphs, exclude date section
            ".choose-horoscope-sign-content-block .horoscope-content p:first-of-type", // First paragraph in horoscope content
            ".entry-content .horoscope-content p", // Entry content horoscope paragraphs
            "[id*='chinese-horoscope-type-block'] p:not(.horoscope-strip)" // Block with specific ID pattern
        )

        for (selector in horoscopeContentSelectors) {
            val elements = document.select(selector)
            for (element in elements) {
                val text = element.text().trim()

                // Skip if it's promotional content
                if (isPromotionalContent(text)) continue

                // Check if it's substantial horoscope content
                if (isValidHoroscopeContent(text)) {
                    return cleanHoroscopeText(text)
                }
            }
        }

        // Strategy 2: Look for horoscope-content class and extract text
        val horoscopeContentDiv = document.select(".horoscope-content").firstOrNull()
        if (horoscopeContentDiv != null) {
            // Remove promotional strips first
            horoscopeContentDiv.select(".horoscope-strip").remove()
            horoscopeContentDiv.select("a[href*='products.ganeshaspeaks.com']").remove()

            // Get all paragraph text
            val paragraphs = horoscopeContentDiv.select("p")
            for (paragraph in paragraphs) {
                val text = paragraph.text().trim()
                if (isValidHoroscopeContent(text) && !isPromotionalContent(text)) {
                    return cleanHoroscopeText(text)
                }
            }

            // If no valid paragraphs, get direct text content
            val directText = horoscopeContentDiv.ownText().trim()
            if (isValidHoroscopeContent(directText)) {
                return cleanHoroscopeText(directText)
            }
        }

        // Strategy 3: Look for entry-content and extract horoscope text
        val entryContent = document.select(".entry-content").firstOrNull()
        if (entryContent != null) {
            // Remove promotional content
            entryContent.select(".horoscope-strip").remove()
            entryContent.select("a[href*='products.ganeshaspeaks.com']").remove()
            entryContent.select("a[href*='utm_source']").remove()

            val paragraphs = entryContent.select("p")
            for (paragraph in paragraphs) {
                val text = paragraph.text().trim()
                if (isValidHoroscopeContent(text) && !isPromotionalContent(text)) {
                    return cleanHoroscopeText(text)
                }
            }
        }

        return ""
    }

    private fun extractUsingContentPatterns(document: Document): String {
        // Remove unwanted elements
        val cleanDoc = document.clone()
        cleanDoc.select("script, style, nav, header, footer, .navbar, .menu, .navigation").remove()
        cleanDoc.select(".horoscope-strip").remove() // Remove promotional strips
        cleanDoc.select("a[href*='products.ganeshaspeaks.com']").remove() // Remove product links

        val fullText = cleanDoc.text()

        // Look for content after date pattern
        val datePattern = "\\d{1,2}-\\d{1,2}-\\d{4}".toRegex()
        val dateMatch = datePattern.find(fullText)

        if (dateMatch != null) {
            val afterDate = fullText.substring(dateMatch.range.last + 1).trim()

            // Find end markers
            val endMarkers = listOf(
                "2025 में बड़े करियर बदलाव",
                "करियर 2025 रिपोर्ट",
                "आज जीवन के विभिन्न क्षेत्रों",
                "प्रेम और संबंध",
                "स्वास्थ्य",
                "धन और वित्त",
                "व्यवसाय और कॅरियर"
            )

            var endIndex = afterDate.length
            for (marker in endMarkers) {
                val markerIndex = afterDate.indexOf(marker)
                if (markerIndex != -1 && markerIndex < endIndex) {
                    endIndex = markerIndex
                }
            }

            if (endIndex > 0) {
                val prediction = afterDate.substring(0, endIndex).trim()
                if (isValidHoroscopeContent(prediction)) {
                    return cleanHoroscopeText(prediction)
                }
            }
        }

        return ""
    }

    private fun isValidHoroscopeContent(text: String): Boolean {
        return text.length > 30 &&
                !text.contains("लॉगिन") &&
                !text.contains("साइनअप") &&
                !text.contains("ग्राहक सेवा") &&
                !text.contains("My Account") &&
                !text.contains("Cart") &&
                !text.contains("हेल्प") &&
                !text.contains("0091-79") &&
                !text.contains("8:00 AM") &&
                !text.contains("English हिन्दी")
    }

    private fun isPromotionalContent(text: String): Boolean {
        val promotionalKeywords = listOf(
            "2025 में बड़े करियर बदलाव",
            "करियर 2025 रिपोर्ट",
            "50% की छूट",
            "अब.*की छूट",
            "रिपोर्ट के साथ तैयारी",
            "products.ganeshaspeaks.com",
            "utm_source",
            "व्हाट्सऐप चैनल",
            "सब्सक्राइब करें"
        )

        return promotionalKeywords.any { keyword ->
            text.contains(keyword) || Regex(keyword).containsMatchIn(text)
        }
    }

    private fun cleanHoroscopeText(text: String): String {
        var cleanText = text

        // Remove promotional phrases
        val unwantedPatterns = listOf(
            "2025 में बड़े करियर बदलाव.*",
            "करियर 2025 रिपोर्ट.*",
            "50% की छूट.*",
            "अब.*की छूट.*",
            "रिपोर्ट के साथ तैयारी.*",
            "व्हाट्सऐप चैनल.*",
            "सब्सक्राइब करें.*",
            "और पढ़ें.*"
        )

        for (pattern in unwantedPatterns) {
            cleanText = cleanText.replace(Regex(pattern), "").trim()
        }

        // Clean up punctuation and spacing
        cleanText = cleanText.replace(Regex("\\s+"), " ")
            .replace("।।", "।")
            .replace("..", ".")
            .replace("।.", "।")
            .trim()

        // Remove trailing dots or punctuation if text doesn't end properly
        if (cleanText.endsWith("..")) {
            cleanText = cleanText.dropLast(2) + "।"
        } else if (cleanText.endsWith(".") && !cleanText.endsWith("।")) {
            cleanText = cleanText.dropLast(1) + "।"
        }

        return cleanText
    }

    private fun extractCategoryPredictions(document: Document): CategoryPredictions {
        // Remove unwanted elements first
        val cleanDoc = document.clone()
        cleanDoc.select("script, style, nav, header, footer, .navbar, .menu").remove()
        cleanDoc.select(".horoscope-strip").remove() // Remove promotional strips
        cleanDoc.select("a[href*='products.ganeshaspeaks.com']").remove() // Remove product links
        cleanDoc.select("a[href*='utm_source']").remove() // Remove tracking links

        val allText = cleanDoc.text()

        // Extract different categories with more specific patterns
        val lovePrediction = extractSpecificCategoryText(
            allText,
            "प्रेम और संबंध",
            listOf("स्वास्थ्य", "धन और वित्त")
        )
        val healthPrediction =
            /*extractSpecificCategoryText(allText, "स्वास्थ्य", listOf("धन और वित्त", "व्यवसाय"))*/
            null
        val moneyPrediction =
            extractSpecificCategoryText(allText, "धन और वित्त", listOf("व्यवसाय", "कॅरियर"))
        val careerPrediction = extractSpecificCategoryText(
            allText,
            "व्यवसाय और कॅरियर",
            listOf("अपने दैनिक", "कोई भिन्न")
        )

        return CategoryPredictions(
            loveRelationship = lovePrediction?.let { CategoryPrediction(it) },
            healthWellbeing = healthPrediction?.let { CategoryPrediction(it) },
            moneyFinance = moneyPrediction?.let { CategoryPrediction(it) },
            careerBusiness = careerPrediction?.let { CategoryPrediction(it) }
        )
    }

    private fun extractSpecificCategoryText(
        fullText: String,
        sectionHeader: String,
        stopWords: List<String>
    ): String? {
        val startIndex = fullText.indexOf(sectionHeader)
        if (startIndex == -1) return null

        // Find where this section ends (either at next section or stop words)
        var endIndex = fullText.length

        // Look for stop words to determine section end
        for (stopWord in stopWords) {
            val stopIndex = fullText.indexOf(stopWord, startIndex + sectionHeader.length)
            if (stopIndex != -1 && stopIndex < endIndex) {
                endIndex = stopIndex
            }
        }

        // Also look for "और पढ़ें" which marks section end
        val readMoreIndex = fullText.indexOf("और पढ़ें", startIndex)
        if (readMoreIndex != -1 && readMoreIndex < endIndex) {
            endIndex = readMoreIndex
        }

        // Look for promotional content markers to end section early
        val promotionalMarkers = listOf(
            "50% की छूट",
            "करियर 2025 रिपोर्ट",
            "2025 में बड़े करियर बदलाव",
            "रिपोर्ट के साथ तैयारी",
            "अब.*की छूट",
            "व्हाट्सऐप चैनल",
            "सब्सक्राइब करें",
            "products.ganeshaspeaks.com",
            "अपने दैनिक व्यक्तिगत राशिफल",
            "कोई भिन्न चिह्न चुनें"
        )

        for (marker in promotionalMarkers) {
            val markerIndex = if (marker.contains(".*")) {
                // Handle regex patterns
                Regex(marker).find(fullText, startIndex + sectionHeader.length)?.range?.first
            } else {
                fullText.indexOf(marker, startIndex + sectionHeader.length)
            }

            if (markerIndex != null && markerIndex != -1 && markerIndex < endIndex) {
                endIndex = markerIndex
            }
        }

        if (endIndex <= startIndex + sectionHeader.length) return null

        val sectionText = fullText.substring(startIndex + sectionHeader.length, endIndex).trim()

        // Clean the text and validate it's meaningful content
        val cleanText = cleanCategoryText(sectionText)

        return if (isValidCategoryContent(cleanText)) {
            cleanText
        } else null
    }

    private fun cleanCategoryText(text: String): String {
        var cleanText = text

        // Remove promotional patterns more aggressively
        val promotionalPatterns = listOf(
            "50% की छूट.*?(?=\\.|।|$)",
            "करियर 2025 रिपोर्ट.*?(?=\\.|।|$)",
            "2025 में बड़े करियर बदलाव.*?(?=\\.|।|$)",
            "रिपोर्ट के साथ तैयारी.*?(?=\\.|।|$)",
            "अब.*?की छूट.*?(?=\\.|।|$)",
            "व्हाट्सऐप चैनल.*?(?=\\.|।|$)",
            "सब्सक्राइब करें.*?(?=\\.|।|$)",
            "और पढ़ें.*?(?=\\.|।|$)",
            "अपने दैनिक व्यक्तिगत राशिफल.*?(?=\\.|।|$)",
            "गणेशास्पीक्स व्हाट्सऐप.*?(?=\\.|।|$)",
            "products\\.ganeshaspeaks\\.com.*?(?=\\.|।|$)",
            "utm_source.*?(?=\\.|।|$)"
        )

        for (pattern in promotionalPatterns) {
            cleanText = cleanText.replace(Regex(pattern), "").trim()
        }

        // Remove navigation and unwanted content
        val unwantedPatterns = listOf(
            "लॉगिन.*?(?=\\.|।|$)",
            "साइनअप.*?(?=\\.|।|$)",
            "हेल्प.*?(?=\\.|।|$)",
            "ग्राहक सेवा.*?(?=\\.|।|$)",
            "My Account.*?(?=\\.|।|$)",
            "Cart.*?(?=\\.|।|$)",
            "0091-79-4900-7777",
            "8:00 AM – 8:00 PM"
        )

        for (pattern in unwantedPatterns) {
            cleanText = cleanText.replace(Regex(pattern), "").trim()
        }

        // Clean up spacing and punctuation
        cleanText = cleanText.replace(Regex("\\s+"), " ")
            .replace("।।", "।")
            .replace("..", ".")
            .replace("।.", "।")
            .trim()

        // Remove incomplete sentences at the end that might be cut-off promotional content
        val sentences = cleanText.split("।").map { it.trim() }
        val validSentences = sentences.filter { sentence ->
            sentence.isNotEmpty() &&
                    sentence.length > 10 &&
                    !isPromotionalSentence(sentence)
        }

        return validSentences.joinToString("। ") + if (validSentences.isNotEmpty()) "।" else ""
    }

    private fun isValidCategoryContent(text: String): Boolean {
        return text.length > 20 &&
                !text.contains("लॉगिन") &&
                !text.contains("साइनअप") &&
                !text.contains("हेल्प") &&
                !text.contains("ग्राहक सेवा") &&
                !text.contains("My Account") &&
                !text.contains("Cart") &&
                !text.contains("0091-79") &&
                !isPromotionalContent(text)
    }

    private fun isPromotionalSentence(sentence: String): Boolean {
        val promotionalKeywords = listOf(
            "50% की छूट",
            "करियर रिपोर्ट",
            "2025 रिपोर्ट",
            "रिपोर्ट के साथ",
            "व्हाट्सऐप",
            "सब्स्क्राइब",
            "चैनल",
            "products.ganeshaspeaks",
            "utm_",
            "छूट के साथ",
            "तैयारी करें"
        )

        return promotionalKeywords.any { keyword ->
            sentence.contains(keyword, ignoreCase = true)
        }
    }

    private fun extractKeyHighlights(document: Document): List<String> {
        val highlights = mutableListOf<String>()

        // Clean document text
        val cleanDoc = document.clone()
        cleanDoc.select("script, style, nav, header, footer").remove()
        val text = cleanDoc.text()

        // Extract key positive phrases from the actual horoscope content
        val positiveKeywords = listOf(
            "आर्थिक लाभ", "खुशीयाली", "सकारात्मक मोड़",
            "उत्तम भोजन", "रचनात्मक", "उत्कृष्टता",
            "स्फूर्तिली", "ताजगीपूर्ण", "प्रवास की तैयारी",
            "नये कार्य", "जिंदादिली", "रोमांटिक भावना"
        )

        // Only add highlights that appear in the main horoscope content
        val mainContent = extractUsingCSSSelectors(document)

        for (keyword in positiveKeywords) {
            if (mainContent.contains(keyword)) {
                highlights.add(keyword)
            }
        }

        // Add highlights from category sections if they exist
        val categoryContent = listOf(
            extractSpecificCategoryText(text, "प्रेम और संबंध", listOf("स्वास्थ्य")),
            extractSpecificCategoryText(text, "स्वास्थ्य", listOf("धन और वित्त")),
            extractSpecificCategoryText(text, "धन और वित्त", listOf("व्यवसाय")),
            extractSpecificCategoryText(text, "व्यवसाय और कॅरियर", listOf("अपने दैनिक"))
        ).filterNotNull().joinToString(" ")

        val additionalKeywords = listOf("संतुष्ट", "शांति", "योग", "आध्यात्मिक")
        for (keyword in additionalKeywords) {
            if (categoryContent.contains(keyword) && !highlights.contains(keyword)) {
                highlights.add(keyword)
            }
        }

        return highlights.distinct()
    }

    private fun extractWarningsAdvice(document: Document): List<String> {
        val advice = mutableListOf<String>()

        // Clean document text
        val cleanDoc = document.clone()
        cleanDoc.select("script, style, nav, header, footer").remove()
        val text = cleanDoc.text()

        // Extract actual advice from horoscope content
        val advicePatterns = listOf(
            "गणेश जी सलाह देते हैं",
            "सलाह देते हैं",
            "कोशिश करें",
            "ध्यान केंद्रित करना चाहिए",
            "अपेक्षा नहीं करनी चाहिए",
            "बचना चाहिए",
            "प्रलोभन.*महंगा पड़ सकता है",
            "हानिकारक प्रभाव पड़ सकता है"
        )

        // Get the main content and category content
        val allContent = listOf(
            extractUsingCSSSelectors(document),
            extractSpecificCategoryText(text, "स्वास्थ्य", listOf("धन और वित्त")) ?: "",
            extractSpecificCategoryText(text, "धन और वित्त", listOf("व्यवसाय")) ?: ""
        ).joinToString(" ")

        for (pattern in advicePatterns) {
            val regex = Regex(pattern)
            if (regex.containsMatchIn(allContent)) {
                // Extract the sentence containing the advice
                val sentences = allContent.split("।").map { it.trim() }
                val adviceSentence = sentences.find { regex.containsMatchIn(it) }
                if (adviceSentence != null && adviceSentence.length > 10) {
                    advice.add(adviceSentence.trim() + "।")
                }
            }
        }

        return advice.distinct().take(5) // Limit to 5 most relevant advice points
    }

    private fun extractMetadata(document: Document): ExtractedMetadata {
        // Extract zodiac icon from horoscope-date section
        val iconUrl = document.select(".horoscope-date img, .horoscope-content img")
            .firstOrNull()?.attr("src") ?: ""

        // Extract promotional links
        val promoLinks =
            document.select("a[href*='products.ganeshaspeaks.com'], .horoscope-strip a")
                .map { it.text().trim() }
                .filter { it.isNotEmpty() && !it.contains("utm_") }

        return ExtractedMetadata(
            zodiacIcon = iconUrl,
            promotionalLinks = promoLinks
        )
    }
}