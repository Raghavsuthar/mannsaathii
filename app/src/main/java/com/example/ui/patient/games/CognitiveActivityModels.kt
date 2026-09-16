package com.example.ui.patient.games

import com.example.data.model.FamilyMember
import com.example.data.model.Memory
import com.example.ui.viewmodel.UiState

enum class ActivityType {
    VISUAL_MATCHING,
    COLOR_IDENTIFICATION,
    SIMPLE_SEQUENCING,
    COUNTING,
    CATEGORY_SORTING,
    HOUSEHOLD_OBJECTS,
    MEMORY_RECALL_3_ITEMS,
    COUNT_OR_SHAPES,
    REMINISCENCE,
    FAMILY_CONNECTION
}

data class ActivityOption(
    val id: String,
    val textEn: String,
    val textHi: String,
    val textGu: String,
    val emoji: String = "",
    val colorHex: Long? = null,
    val isCorrect: Boolean = true,
    val photoUri: String? = null,
    val subtitleEn: String = "",
    val subtitleHi: String = "",
    val subtitleGu: String = ""
) {
    fun getText(lang: String): String = when (lang) {
        "hi" -> textHi.ifBlank { textEn }
        "gu" -> textGu.ifBlank { textEn }
        else -> textEn
    }

    fun getSubtitle(lang: String): String = when (lang) {
        "hi" -> subtitleHi.ifBlank { subtitleEn }
        "gu" -> subtitleGu.ifBlank { subtitleEn }
        else -> subtitleEn
    }
}

data class CognitiveActivity(
    val id: Int,
    val type: ActivityType,
    val iconEmoji: String,
    val titleKey: String,
    val questionEn: String,
    val questionHi: String,
    val questionGu: String,
    val targetHeroEmoji: String? = null,
    val targetHeroColorHex: Long? = null,
    val targetHeroLabelEn: String? = null,
    val targetHeroLabelHi: String? = null,
    val targetHeroLabelGu: String? = null,
    val observationItems: List<ActivityOption> = emptyList(), // For 3-item memory recall
    val isReminiscence: Boolean = false, // All choices are celebrated
    val options: List<ActivityOption>
) {
    fun getQuestion(lang: String): String = when (lang) {
        "hi" -> questionHi.ifBlank { questionEn }
        "gu" -> questionGu.ifBlank { questionEn }
        else -> questionEn
    }

    fun getTargetLabel(lang: String): String? = when (lang) {
        "hi" -> targetHeroLabelHi ?: targetHeroLabelEn
        "gu" -> targetHeroLabelGu ?: targetHeroLabelEn
        else -> targetHeroLabelEn
    }
}

object CognitiveActivityProvider {

    fun getActivities(uiState: UiState): List<CognitiveActivity> {
        val lang = uiState.profile.language
        val realFamily: List<FamilyMember> = uiState.familyMembers
        val realMemories: List<Memory> = uiState.memories

        return listOf(
            // 1. Visual Matching: Lotus
            CognitiveActivity(
                id = 1,
                type = ActivityType.VISUAL_MATCHING,
                iconEmoji = "🌸",
                titleKey = "game_1_name",
                questionEn = "Look at this sacred Lotus flower. Tap the matching flower:",
                questionHi = "इस पवित्र कमल के फूल को देखें। इससे मिलता-जुलता फूल चुनें:",
                questionGu = "આ પવિત્ર કમળના ફૂલને જુઓ. આના જેવું જ ફૂલ પસંદ કરો:",
                targetHeroEmoji = "🌸",
                targetHeroColorHex = 0xFFFFD1DC,
                targetHeroLabelEn = "Pink Lotus",
                targetHeroLabelHi = "गुलाबी कमल",
                targetHeroLabelGu = "ગુલાબી કમળ",
                options = listOf(
                    ActivityOption(
                        id = "lotus",
                        textEn = "Lotus",
                        textHi = "कमल",
                        textGu = "કમળ",
                        emoji = "🌸",
                        isCorrect = true
                    ),
                    ActivityOption(
                        id = "sunflower",
                        textEn = "Sunflower",
                        textHi = "सूरजमुखी",
                        textGu = "સૂર્યમુખી",
                        emoji = "🌻",
                        isCorrect = false
                    ),
                    ActivityOption(
                        id = "rose",
                        textEn = "Red Rose",
                        textHi = "लाल गुलाब",
                        textGu = "લાલ ગુલાબ",
                        emoji = "🌹",
                        isCorrect = false
                    )
                )
            ),

            // 2. Color & Object Identification: Red Apple
            CognitiveActivity(
                id = 2,
                type = ActivityType.COLOR_IDENTIFICATION,
                iconEmoji = "🍎",
                titleKey = "game_2_name",
                questionEn = "Which one is the sweet, crisp Red Apple?",
                questionHi = "इनमें से मीठा और लाल सेब कौन सा है?",
                questionGu = "આમાંથી મીઠું અને લાલ સફરજન કયું છે?",
                targetHeroEmoji = "🍎",
                targetHeroColorHex = 0xFFFFD2CF,
                targetHeroLabelEn = "Sweet Red Apple",
                targetHeroLabelHi = "मीठा लाल सेब",
                targetHeroLabelGu = "મીઠું લાલ સફરજન",
                options = listOf(
                    ActivityOption(
                        id = "apple",
                        textEn = "Red Apple",
                        textHi = "लाल सेब",
                        textGu = "લાલ સફરજન",
                        emoji = "🍎",
                        colorHex = 0xFFFFE5E5,
                        isCorrect = true
                    ),
                    ActivityOption(
                        id = "banana",
                        textEn = "Yellow Banana",
                        textHi = "पीला केला",
                        textGu = "પીળું કેળું",
                        emoji = "🍌",
                        colorHex = 0xFFFFF9DB,
                        isCorrect = false
                    ),
                    ActivityOption(
                        id = "grapes",
                        textEn = "Green Grapes",
                        textHi = "हरे अंगूर",
                        textGu = "લીલી દ્રાક્ષ",
                        emoji = "🍇",
                        colorHex = 0xFFEBFBEE,
                        isCorrect = false
                    )
                )
            ),

            // 3. Color Identification: Green Chai Cup
            CognitiveActivity(
                id = 3,
                type = ActivityType.COLOR_IDENTIFICATION,
                iconEmoji = "🟢",
                titleKey = "game_3_name",
                questionEn = "Tap the Green (લીલો / हरा) cup:",
                questionHi = "हरे रंग का कप छुएं:",
                questionGu = "લીલા રંગનો કપ અડો:",
                targetHeroEmoji = "🍵",
                targetHeroColorHex = 0xFFD7F3DF,
                targetHeroLabelEn = "Soothing Green",
                targetHeroLabelHi = "शांत हरा रंग",
                targetHeroLabelGu = "શાંત લીલો રંગ",
                options = listOf(
                    ActivityOption(
                        id = "green_cup",
                        textEn = "Green Cup",
                        textHi = "हरा कप",
                        textGu = "લીલો કપ",
                        emoji = "🍵",
                        colorHex = 0xFFC7EBC9,
                        isCorrect = true
                    ),
                    ActivityOption(
                        id = "red_cup",
                        textEn = "Red Mug",
                        textHi = "लाल मग",
                        textGu = "લાલ મગ",
                        emoji = "🥤",
                        colorHex = 0xFFFFC9C9,
                        isCorrect = false
                    ),
                    ActivityOption(
                        id = "blue_cup",
                        textEn = "Blue Glass",
                        textHi = "नीला ग्लास",
                        textGu = "વાદળી ગ્લાસ",
                        emoji = "🥛",
                        colorHex = 0xFFD0EBFF,
                        isCorrect = false
                    )
                )
            ),

            // 4. Category Sorting: Food vs Non-Food
            CognitiveActivity(
                id = 4,
                type = ActivityType.CATEGORY_SORTING,
                iconEmoji = "🍲",
                titleKey = "game_4_name",
                questionEn = "Which one is warm, delicious food to eat?",
                questionHi = "इनमें से गरमा-गरम स्वादिष्ट भोजन कौन सा है?",
                questionGu = "આમાંથી ગરમા-ગરમ સ્વાદિષ્ટ ભોજન કયું છે?",
                targetHeroEmoji = "🍲",
                targetHeroColorHex = 0xFFFFF3BF,
                targetHeroLabelEn = "Warm Meal",
                targetHeroLabelHi = "गरमा-गरम खाना",
                targetHeroLabelGu = "ગરમા-ગરમ ભોજન",
                options = listOf(
                    ActivityOption(
                        id = "khichdi",
                        textEn = "Khichdi & Kadhi",
                        textHi = "खिचड़ी और कढ़ी",
                        textGu = "ખીચડી અને કઢી",
                        emoji = "🍲",
                        isCorrect = true
                    ),
                    ActivityOption(
                        id = "saree",
                        textEn = "Bandhani Saree",
                        textHi = "बांधनी साड़ी",
                        textGu = "બાંધણી સાડી",
                        emoji = "👗",
                        isCorrect = false
                    ),
                    ActivityOption(
                        id = "chair",
                        textEn = "Wooden Chair",
                        textHi = "लकड़ी की कुर्सी",
                        textGu = "લાકડાની ખુરશી",
                        emoji = "🪑",
                        isCorrect = false
                    )
                )
            ),

            // 5. Simple Sequencing: Morning Routine
            CognitiveActivity(
                id = 5,
                type = ActivityType.SIMPLE_SEQUENCING,
                iconEmoji = "🌅",
                titleKey = "game_5_name",
                questionEn = "What do we gently do first upon waking up in the morning?",
                questionHi = "सुबह सोकर उठने के बाद सबसे पहले हम क्या करते हैं?",
                questionGu = "સવારે ઊંઘમાંથી જાગ્યા પછી સૌથી પહેલા આપણે શું કરીએ છીએ?",
                targetHeroEmoji = "🌅",
                targetHeroColorHex = 0xFFFFECC8,
                targetHeroLabelEn = "Morning Sunrise",
                targetHeroLabelHi = "सुबह का सवेरा",
                targetHeroLabelGu = "સવારનો સૂર્યોદય",
                options = listOf(
                    ActivityOption(
                        id = "brush",
                        textEn = "Wash Face & Brush Teeth",
                        textHi = "मुंह धोना और ब्रश करना",
                        textGu = "મોં ધોવું અને બ્રશ કરવું",
                        emoji = "🪥",
                        isCorrect = true
                    ),
                    ActivityOption(
                        id = "night_sleep",
                        textEn = "Go to Bed for Night Sleep",
                        textHi = "रात को सोने जाना",
                        textGu = "રાત્રે સૂઈ જવું",
                        emoji = "🌙",
                        isCorrect = false
                    ),
                    ActivityOption(
                        id = "heavy_market",
                        textEn = "Heavy Grocery Shopping",
                        textHi = "बाजार में भारी खरीदारी",
                        textGu = "બજારમાં ભારે ખરીદી",
                        emoji = "🛍️",
                        isCorrect = false
                    )
                )
            ),

            // 6. Familiar Indian Household Objects
            CognitiveActivity(
                id = 6,
                type = ActivityType.HOUSEHOLD_OBJECTS,
                iconEmoji = "☕",
                titleKey = "game_6_name",
                questionEn = "What do we use to brew warm morning masala tea (chai)?",
                questionHi = "सुबह की गरमा-गरम मसाला चाय बनाने के लिए हम क्या उपयोग करते हैं?",
                questionGu = "સવારની ગરમા-ગરમ મસાલા ચા બનાવવા આપણે શેનો ઉપયોગ કરીએ છીએ?",
                targetHeroEmoji = "🫖",
                targetHeroColorHex = 0xFFFCE1D1,
                targetHeroLabelEn = "Tea Kettle",
                targetHeroLabelHi = "चाय की केतली",
                targetHeroLabelGu = "ચાની કીટલી",
                options = listOf(
                    ActivityOption(
                        id = "kettle",
                        textEn = "Chai Kettle & Pan",
                        textHi = "चाय की केतली और तपेली",
                        textGu = "ચાની કીટલી અને તપેલી",
                        emoji = "🫖",
                        isCorrect = true
                    ),
                    ActivityOption(
                        id = "comb",
                        textEn = "Hair Comb",
                        textHi = "बालों की कंघी",
                        textGu = "વાળની કાંસકી",
                        emoji = "🪮",
                        isCorrect = false
                    ),
                    ActivityOption(
                        id = "keys",
                        textEn = "House Keyring",
                        textHi = "घर की चाबियां",
                        textGu = "ઘરની ચાવીઓ",
                        emoji = "🔑",
                        isCorrect = false
                    )
                )
            ),

            // 7. Gentle 3-Item Memory Recall (Tray Observation)
            CognitiveActivity(
                id = 7,
                type = ActivityType.MEMORY_RECALL_3_ITEMS,
                iconEmoji = "👀",
                titleKey = "game_7_name",
                questionEn = "Take a moment to observe these 3 comforting items on the puja tray:",
                questionHi = "पूजा की थाली में रखी इन 3 वस्तुओं को ध्यान से देखें:",
                questionGu = "પૂજાની થાળીમાં રાખેલી આ 3 વસ્તુઓને ધ્યાનથી જુઓ:",
                targetHeroEmoji = "🪔",
                observationItems = listOf(
                    ActivityOption("p1", "Diya (Deepak)", "दीपक", "દીવો", "🪔"),
                    ActivityOption("p2", "Fresh Flower", "सुगंधित फूल", "તાજું ફૂલ", "🌸"),
                    ActivityOption("p3", "Sacred Bell", "मंदिर की घंटी", "પૂજાની ઘંટી", "🔔")
                ),
                options = listOf(
                    ActivityOption(
                        id = "diya",
                        textEn = "Diya (Deepak)",
                        textHi = "दीपक",
                        textGu = "દીવો",
                        emoji = "🪔",
                        isCorrect = true
                    ),
                    ActivityOption(
                        id = "airplane",
                        textEn = "Airplane",
                        textHi = "हवाई जहाज",
                        textGu = "વિમાન",
                        emoji = "✈️",
                        isCorrect = false
                    ),
                    ActivityOption(
                        id = "car",
                        textEn = "Motor Car",
                        textHi = "मोटर गाड़ी",
                        textGu = "મોટર કાર",
                        emoji = "🚗",
                        isCorrect = false
                    )
                )
            ),

            // 8. Counting: Counting Marigold Flowers
            CognitiveActivity(
                id = 8,
                type = ActivityType.COUNTING,
                iconEmoji = "🔢",
                titleKey = "game_8_name",
                questionEn = "How many golden marigold flowers are arranged below?",
                questionHi = "नीचे गेंदे के कितने सुनहरे फूल सजे हुए हैं?",
                questionGu = "નીચે ગલગોટાના કેટલા સોનેરી ફૂલ ગોઠવાયેલા છે?",
                targetHeroEmoji = "🌼 🌼 🌼",
                targetHeroColorHex = 0xFFFFF3BF,
                targetHeroLabelEn = "3 Golden Marigolds",
                targetHeroLabelHi = "3 सुनहरे गेंदे के फूल",
                targetHeroLabelGu = "3 સોનેરી ગલગોટાના ફૂલ",
                options = listOf(
                    ActivityOption(
                        id = "count_3",
                        textEn = "3 Flowers",
                        textHi = "3 फूल",
                        textGu = "3 ફૂલ",
                        emoji = "3️⃣",
                        isCorrect = true
                    ),
                    ActivityOption(
                        id = "count_2",
                        textEn = "2 Flowers",
                        textHi = "2 फूल",
                        textGu = "2 ફૂલ",
                        emoji = "2️⃣",
                        isCorrect = false
                    ),
                    ActivityOption(
                        id = "count_5",
                        textEn = "5 Flowers",
                        textHi = "5 फूल",
                        textGu = "5 ફૂલ",
                        emoji = "5️⃣",
                        isCorrect = false
                    )
                )
            ),

            // 9. Reminiscence: Peaceful Evening Choice (No wrong answers!)
            CognitiveActivity(
                id = 9,
                type = ActivityType.REMINISCENCE,
                iconEmoji = "🕊️",
                titleKey = "game_9_name",
                isReminiscence = true,
                questionEn = if (realMemories.isNotEmpty()) {
                    "Thinking of warm days: ${realMemories.first().titleEn}. What brings peace to your evening?"
                } else {
                    "What brings peace and warmth to your heart in the evening?"
                },
                questionHi = if (realMemories.isNotEmpty()) {
                    "सुखद पलों की याद: ${realMemories.first().titleHi.ifBlank { realMemories.first().titleEn }}। शाम को आपके मन को क्या शांति देता है?"
                } else {
                    "शाम के समय आपके मन को क्या सबसे अधिक शांति और सुख देता है?"
                },
                questionGu = if (realMemories.isNotEmpty()) {
                    "મીઠી યાદો: ${realMemories.first().titleGu.ifBlank { realMemories.first().titleEn }}। સાંજે તમારા મનને શું શાંતિ આપે છે?"
                } else {
                    "સાંજના સમયે તમારા મનને શું સૌથી વધુ શાંતિ આપે છે?"
                },
                targetHeroEmoji = "✨",
                targetHeroColorHex = 0xFFEDE3FF,
                targetHeroLabelEn = "Peaceful Sunset Moment",
                targetHeroLabelHi = "शांतिदायक शाम",
                targetHeroLabelGu = "શાંતિપૂર્ણ સાંજ",
                options = listOf(
                    ActivityOption(
                        id = "bhajan",
                        textEn = "Listening to Devotional Songs / Bhajans",
                        textHi = "सुंदर भजन या भक्ति संगीत सुनना",
                        textGu = "સુંદર ભજન કે ભક્તિ સંગીત સાંભળવું",
                        emoji = "🎶",
                        isCorrect = true
                    ),
                    ActivityOption(
                        id = "tea_garden",
                        textEn = "Sipping Warm Chai in the Balcony",
                        textHi = "बालकनी में बैठकर गरमा-गरम चाय पीना",
                        textGu = "બાલકનીમાં બેસીને ગરમ ચા પીવી",
                        emoji = "☕",
                        isCorrect = true
                    ),
                    ActivityOption(
                        id = "family_chat",
                        textEn = "Gentle Chat with Family & Grandkids",
                        textHi = "बच्चों और परिवार के साथ प्यारी बातचीत",
                        textGu = "બાળકો અને પરિવાર સાથે વહાલી વાતો",
                        emoji = "👨‍👩‍👧",
                        isCorrect = true
                    )
                )
            ),

            // 10. Family Connection (Uses Real App Data from UiState!)
            createFamilyActivity(realFamily)
        )
    }

    private fun createFamilyActivity(familyMembers: List<FamilyMember>): CognitiveActivity {
        val primaryMember = familyMembers.firstOrNull()

        if (primaryMember != null) {
            val otherMembers = familyMembers.drop(1).take(2)
            val correctOpt = ActivityOption(
                id = "fam_${primaryMember.id}",
                textEn = primaryMember.name,
                textHi = primaryMember.name,
                textGu = primaryMember.name,
                subtitleEn = primaryMember.relationshipEn,
                subtitleHi = primaryMember.relationshipHi,
                subtitleGu = primaryMember.relationshipGu,
                emoji = primaryMember.iconEmoji,
                photoUri = primaryMember.photoUri,
                colorHex = primaryMember.photoColorHex,
                isCorrect = true
            )

            val otherOptions = if (otherMembers.isNotEmpty()) {
                otherMembers.map { member ->
                    ActivityOption(
                        id = "fam_${member.id}",
                        textEn = member.name,
                        textHi = member.name,
                        textGu = member.name,
                        subtitleEn = member.relationshipEn,
                        subtitleHi = member.relationshipHi,
                        subtitleGu = member.relationshipGu,
                        emoji = member.iconEmoji,
                        photoUri = member.photoUri,
                        colorHex = member.photoColorHex,
                        isCorrect = false
                    )
                }
            } else {
                listOf(
                    ActivityOption(
                        id = "fam_distractor_1",
                        textEn = "Old Neighbor",
                        textHi = "पुराने पड़ोसी",
                        textGu = "જૂના પડોશી",
                        emoji = "🧑",
                        isCorrect = false
                    ),
                    ActivityOption(
                        id = "fam_distractor_2",
                        textEn = "Postman Uncle",
                        textHi = "डाकिया काका",
                        textGu = "ટપાલી કાકા",
                        emoji = "📦",
                        isCorrect = false
                    )
                )
            }

            return CognitiveActivity(
                id = 10,
                type = ActivityType.FAMILY_CONNECTION,
                iconEmoji = primaryMember.iconEmoji,
                titleKey = "game_10_name",
                questionEn = "Who is your beloved ${primaryMember.relationshipEn} who loves you so much?",
                questionHi = "आपकी प्यारी ${primaryMember.relationshipHi} कौन हैं, जो आपसे बहुत प्यार करती हैं?",
                questionGu = "તમારા વહાલા ${primaryMember.relationshipGu} કોણ છે, જે તમને ખૂબ પ્રેમ કરે છે?",
                targetHeroEmoji = primaryMember.iconEmoji,
                targetHeroColorHex = primaryMember.photoColorHex,
                targetHeroLabelEn = "${primaryMember.name} (${primaryMember.relationshipEn})",
                targetHeroLabelHi = "${primaryMember.name} (${primaryMember.relationshipHi})",
                targetHeroLabelGu = "${primaryMember.name} (${primaryMember.relationshipGu})",
                options = listOf(correctOpt) + otherOptions
            )
        } else {
            // Default beloved family fallback
            return CognitiveActivity(
                id = 10,
                type = ActivityType.FAMILY_CONNECTION,
                iconEmoji = "👩",
                titleKey = "game_10_name",
                questionEn = "Which card is your caring daughter Meena?",
                questionHi = "आपकी प्यारी बेटी मीना का कार्ड कौन सा है?",
                questionGu = "તમારી વહાલી દીકરી મીનાનું કાર્ડ કયું છે?",
                targetHeroEmoji = "👩",
                targetHeroColorHex = 0xFFFCE1D1,
                targetHeroLabelEn = "Meena (Daughter)",
                targetHeroLabelHi = "मीना (बेटी)",
                targetHeroLabelGu = "મીના (દીકરી)",
                options = listOf(
                    ActivityOption(
                        id = "fam_meena",
                        textEn = "Meena",
                        textHi = "मीना",
                        textGu = "મીના",
                        subtitleEn = "Daughter",
                        subtitleHi = "बेटी",
                        subtitleGu = "દીકરી",
                        emoji = "👩",
                        colorHex = 0xFF00695C,
                        isCorrect = true
                    ),
                    ActivityOption(
                        id = "fam_raj",
                        textEn = "Raj",
                        textHi = "राज",
                        textGu = "રાજ",
                        subtitleEn = "Son",
                        subtitleHi = "बेटा",
                        subtitleGu = "દીકરો",
                        emoji = "👨",
                        colorHex = 0xFF1565C0,
                        isCorrect = false
                    ),
                    ActivityOption(
                        id = "fam_pooja",
                        textEn = "Pooja",
                        textHi = "पूजा",
                        textGu = "પૂજા",
                        subtitleEn = "Granddaughter",
                        subtitleHi = "पोती",
                        subtitleGu = "પૌત્રી",
                        emoji = "👧",
                        colorHex = 0xFFE91E63,
                        isCorrect = false
                    )
                )
            )
        }
    }
}
