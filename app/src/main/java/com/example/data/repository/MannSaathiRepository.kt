package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class MannSaathiRepository(private val db: AppDatabase) {
    val patientProfile: Flow<PatientProfile?> = db.patientDao().getProfile()
    val routines: Flow<List<RoutineItem>> = db.routineDao().getAllRoutines()
    val medications: Flow<List<Medication>> = db.medicationDao().getAllMedications()
    val appointments: Flow<List<Appointment>> = db.appointmentDao().getAllAppointments()
    val familyMembers: Flow<List<FamilyMember>> = db.familyDao().getAllFamilyMembers()
    val memories: Flow<List<Memory>> = db.memoryDao().getAllMemories()
    val moodEntries: Flow<List<MoodEntry>> = db.moodDao().getAllMoodEntries()
    val emergencyContacts: Flow<List<EmergencyContact>> = db.emergencyContactDao().getAllContacts()
    val caregiverNotes: Flow<List<CaregiverNote>> = db.caregiverNoteDao().getAllNotes()

    suspend fun getProfileDirect(): PatientProfile? = db.patientDao().getProfileDirect()

    suspend fun updateProfile(profile: PatientProfile) {
        db.patientDao().insertOrUpdateProfile(profile)
    }

    // Routines
    suspend fun setRoutineCompletion(id: Long, completed: Boolean) {
        db.routineDao().setCompletion(id, completed)
    }

    suspend fun setRoutineDelayed(id: Long, delayed: Boolean) {
        db.routineDao().setDelayed(id, delayed)
    }

    suspend fun saveRoutine(item: RoutineItem) {
        if (item.id == 0L) {
            db.routineDao().insertRoutine(item)
        } else {
            db.routineDao().updateRoutine(item)
        }
    }

    suspend fun deleteRoutine(item: RoutineItem) {
        db.routineDao().deleteRoutine(item)
    }

    // Medications
    suspend fun updateMedicationStatus(id: Long, status: MedicationStatus, takenTime: String?) {
        db.medicationDao().updateStatus(id, status, takenTime)
    }

    suspend fun saveMedication(med: Medication): Long {
        return if (med.id == 0L) {
            db.medicationDao().insertMedication(med)
        } else {
            db.medicationDao().updateMedication(med)
            med.id
        }
    }

    suspend fun deleteMedication(med: Medication) {
        db.medicationDao().deleteMedication(med)
    }

    // Appointments
    suspend fun saveAppointment(appt: Appointment) {
        if (appt.id == 0L) {
            db.appointmentDao().insertAppointment(appt)
        } else {
            db.appointmentDao().updateAppointment(appt)
        }
    }

    suspend fun deleteAppointment(appt: Appointment) {
        db.appointmentDao().deleteAppointment(appt)
    }

    // Family
    suspend fun saveFamilyMember(member: FamilyMember) {
        if (member.id == 0L) {
            db.familyDao().insertFamilyMember(member)
        } else {
            db.familyDao().updateFamilyMember(member)
        }
    }

    suspend fun deleteFamilyMember(member: FamilyMember) {
        db.familyDao().deleteFamilyMember(member)
    }

    // Memories
    suspend fun saveMemory(memory: Memory) {
        val now = System.currentTimeMillis()
        if (memory.id == 0L) {
            db.memoryDao().insertMemory(memory.copy(createdAt = now, updatedAt = now))
        } else {
            db.memoryDao().updateMemory(memory.copy(updatedAt = now))
        }
    }

    suspend fun deleteMemory(memory: Memory) {
        db.memoryDao().deleteMemory(memory)
    }

    // Mood
    suspend fun addMoodEntry(entry: MoodEntry) {
        db.moodDao().insertMoodEntry(entry)
    }

    // Emergency Contacts
    suspend fun saveEmergencyContact(contact: EmergencyContact) {
        if (contact.id == 0L) {
            db.emergencyContactDao().insertContact(contact)
        } else {
            db.emergencyContactDao().updateContact(contact)
        }
    }

    suspend fun deleteEmergencyContact(contact: EmergencyContact) {
        db.emergencyContactDao().deleteContact(contact)
    }

    // Caregiver Notes
    suspend fun addCaregiverNote(note: CaregiverNote) {
        db.caregiverNoteDao().insertNote(note)
    }

    suspend fun deleteCaregiverNote(note: CaregiverNote) {
        db.caregiverNoteDao().deleteNote(note)
    }

    // Preload / Reset Demo Data
    suspend fun loadDemoData() {
        // Clear old records
        db.routineDao().clearAll()
        db.medicationDao().clearAll()
        db.appointmentDao().clearAll()
        db.familyDao().clearAll()
        db.memoryDao().clearAll()
        db.moodDao().clearAll()
        db.emergencyContactDao().clearAll()
        db.caregiverNoteDao().clearAll()

        // Seed Patient Profile (Kamla Ben, 72, Ahmedabad, Meena)
        val demoProfile = PatientProfile(
            id = 1,
            name = "Kamla Ben",
            preferredName = "Ba",
            age = 72,
            city = "Ahmedabad",
            caregiverName = "Meena",
            caregiverRelationship = "Daughter",
            caregiverPhone = "+91 98765 43210",
            doctorName = "Dr. Rajesh Patel",
            doctorPhone = "+91 98123 45678",
            language = "gu",
            caregiverPin = "1234",
            isDemoMode = true,
            largeTextMode = false,
            highContrastMode = false,
            pictureMode = false
        )
        db.patientDao().insertOrUpdateProfile(demoProfile)

        // Seed Routines
        db.routineDao().insertRoutines(
            listOf(
                RoutineItem(
                    titleEn = "Wake up gently & morning prayer",
                    titleHi = "आराम से उठें और सुबह की प्रार्थना",
                    titleGu = "શાંતિથી જાગો અને સવારની પ્રાર્થના",
                    time = "07:00 AM",
                    period = "MORNING",
                    iconEmoji = "🌅",
                    caregiverVoiceTextEn = "Good morning Ba! Wishing you a peaceful start to the day.",
                    caregiverVoiceTextHi = "सुप्रभात बा! आपका दिन मंगलमय और सुखद रहे।",
                    caregiverVoiceTextGu = "શુભ સવાર બા! આજનો દિવસ તમારા માટે આનંદદાયક રહે.",
                    sortOrder = 1
                ),
                RoutineItem(
                    titleEn = "Brush teeth & wash face",
                    titleHi = "दांत ब्रश करें और चेहरा धोएं",
                    titleGu = "દાંત સાફ કરો અને મોં ધોવો",
                    time = "07:15 AM",
                    period = "MORNING",
                    iconEmoji = "🪥",
                    caregiverVoiceTextEn = "Ba, warm water and toothbrush are ready by the sink.",
                    caregiverVoiceTextHi = "बा, वॉशबेसिन पर टूथब्रश और गर्म पानी तैयार है।",
                    caregiverVoiceTextGu = "બા, વૉશબેસિન પર બ્રશ અને ગરમ પાણી તૈયાર છે.",
                    sortOrder = 2
                ),
                RoutineItem(
                    titleEn = "Warm Breakfast & Chai",
                    titleHi = "गरम नाश्ता और चाय",
                    titleGu = "ગરમ નાસ્તો અને ચા",
                    time = "07:30 AM",
                    period = "MORNING",
                    iconEmoji = "🍵",
                    caregiverVoiceTextEn = "Ba, your fresh thepla and warm ginger tea are ready on the dining table.",
                    caregiverVoiceTextHi = "बा, मेज पर आपके लिए गरम थेपला और अदरक वाली चाय तैयार है।",
                    caregiverVoiceTextGu = "બા, ડાઇનિંગ ટેબલ પર તમારા માટે તાજા થેપલા અને આદુવાળી ચા તૈયાર છે.",
                    sortOrder = 3
                ),
                RoutineItem(
                    titleEn = "Morning Medicines",
                    titleHi = "सुबह की दवाई",
                    titleGu = "સવારની દવાઓ",
                    time = "08:00 AM",
                    period = "MORNING",
                    iconEmoji = "💊",
                    caregiverVoiceTextEn = "Ba, time to take your morning tablets with a glass of water.",
                    caregiverVoiceTextHi = "बा, पानी के साथ सुबह की गोली लेने का समय हो गया है।",
                    caregiverVoiceTextGu = "બા, પાણી સાથે સવારની ગોળીઓ લેવાનો સમય થઈ ગયો છે.",
                    sortOrder = 4
                ),
                RoutineItem(
                    titleEn = "Gentle Garden Walk",
                    titleHi = "बगीचे में हल्की सैर",
                    titleGu = "બગીચામાં હળવો વૉક",
                    time = "10:00 AM",
                    period = "MORNING",
                    iconEmoji = "🚶‍♀️",
                    caregiverVoiceTextEn = "Let's take a peaceful 15 minute stroll in the front garden sunshine.",
                    caregiverVoiceTextHi = "आइए 15 मिनट धूप में बगीचे की ताज़ी हवा लें।",
                    caregiverVoiceTextGu = "ચાલો 15 મિનિટ બગીચામાં સુંદર તાજી હવામાં ચાલીએ.",
                    sortOrder = 5
                ),
                RoutineItem(
                    titleEn = "Nourishing Gujarati Lunch",
                    titleHi = "पौष्टिक दोपहर का भोजन",
                    titleGu = "પૌષ્ટિક બપોરનું જમવાનું",
                    time = "01:00 PM",
                    period = "AFTERNOON",
                    iconEmoji = "🍛",
                    caregiverVoiceTextEn = "Ba, hot rotli, dal, and khichdi lunch is served.",
                    caregiverVoiceTextHi = "बा, गरम रोटी, दाल और खिचड़ी तैयार है।",
                    caregiverVoiceTextGu = "બા, ગરમ રોટલી, દાળ અને ખીચડી તૈયાર છે.",
                    sortOrder = 6
                ),
                RoutineItem(
                    titleEn = "Afternoon Rest / Nap",
                    titleHi = "दोपहर का आराम",
                    titleGu = "બપોરનો આરામ",
                    time = "02:00 PM",
                    period = "AFTERNOON",
                    iconEmoji = "😴",
                    caregiverVoiceTextEn = "Rest your eyes for a quiet hour.",
                    caregiverVoiceTextHi = "एक घंटे आराम से सो जाइए।",
                    caregiverVoiceTextGu = "એક કલાક શાંતિથી આરામ કરો.",
                    sortOrder = 7
                ),
                RoutineItem(
                    titleEn = "Evening Herbal Tea",
                    titleHi = "शाम की चाय",
                    titleGu = "સાંજની ચા",
                    time = "04:30 PM",
                    period = "EVENING",
                    iconEmoji = "☕",
                    caregiverVoiceTextEn = "A warm cup of tea with light snacks.",
                    caregiverVoiceTextHi = "शाम की हल्की चाय और खाखरा।",
                    caregiverVoiceTextGu = "સાંજની ગરમ ચા અને ખાખરા.",
                    sortOrder = 8
                ),
                RoutineItem(
                    titleEn = "Family Video Call with Raj",
                    titleHi = "बेटे राज से फोन पर बात",
                    titleGu = "દીકરા રાજ સાથે ફોન પર વાત",
                    time = "07:00 PM",
                    period = "EVENING",
                    iconEmoji = "📞",
                    caregiverVoiceTextEn = "Raj is calling from Mumbai to say hello.",
                    caregiverVoiceTextHi = "राज मुंबई से आपको फोन कर रहा है।",
                    caregiverVoiceTextGu = "રાજ મુંબઈથી તમને ફોન કરી રહ્યો છે.",
                    sortOrder = 9
                ),
                RoutineItem(
                    titleEn = "Light Dinner",
                    titleHi = "हल्का रात का भोजन",
                    titleGu = "હળવું સાંજનું ભોજન",
                    time = "08:30 PM",
                    period = "NIGHT",
                    iconEmoji = "🍽️",
                    caregiverVoiceTextEn = "Warm dinner ready for you, Ba.",
                    caregiverVoiceTextHi = "बा, रात का खाना तैयार है।",
                    caregiverVoiceTextGu = "બા, સાંજનું હળવું જમવાનું તૈયાર છે.",
                    sortOrder = 10
                ),
                RoutineItem(
                    titleEn = "Night Medicine & Peaceful Sleep",
                    titleHi = "रात की दवाई और शुभ रात्रि",
                    titleGu = "રાતની દવા અને શાંતિપૂર્ણ ઊંઘ",
                    time = "09:30 PM",
                    period = "NIGHT",
                    iconEmoji = "🛏️",
                    caregiverVoiceTextEn = "Night medicine taken. Sleep peacefully with sweet dreams.",
                    caregiverVoiceTextHi = "रात की गोली लें और अच्छी नींद सोएं।",
                    caregiverVoiceTextGu = "રાતની દવા લઈને સુખરૂપ ઊંઘી જાઓ. શુભ રાત્રી.",
                    sortOrder = 11
                )
            )
        )

        // Seed Medications (Fictional demonstration data)
        db.medicationDao().insertMedications(
            listOf(
                Medication(
                    name = "Memory Vital (BP/Routine)",
                    dosage = "1 Tablet (5mg)",
                    time = "08:00 AM",
                    period = "MORNING",
                    instructionsEn = "Take 1 tablet after breakfast with plain water.",
                    instructionsHi = "नाश्ते के बाद 1 गोली पानी के साथ लें।",
                    instructionsGu = "નાસ્તા પછી 1 ગોળી સાદા પાણી સાથે લો.",
                    status = MedicationStatus.TAKEN,
                    takenTime = "08:15 AM",
                    caregiverVoicePromptEn = "Ba, please take the small white BP tablet after your breakfast.",
                    caregiverVoicePromptHi = "बा, नाश्ते के बाद अपनी सफेद गोली ले लीजिए।",
                    caregiverVoicePromptGu = "બા, નાસ્તા પછી તમારી સફેદ નાની ગોળી લઈ લો."
                ),
                Medication(
                    name = "Calcium & Vitamin D3",
                    dosage = "1 Tablet",
                    time = "01:30 PM",
                    period = "AFTERNOON",
                    instructionsEn = "Take 1 tablet after lunch.",
                    instructionsHi = "दोपहर के खाने के बाद 1 गोली लें।",
                    instructionsGu = "બપોરે જમ્યા પછી 1 ગોળી લો.",
                    status = MedicationStatus.PENDING,
                    caregiverVoicePromptEn = "Ba, your calcium tablet is on the table next to the water jug.",
                    caregiverVoicePromptHi = "बा, पानी के जग के पास आपकी कैल्शियम की गोली रखी है।",
                    caregiverVoicePromptGu = "બા, પાણીના જગ પાસે તમારી કેલ્શિયમની ગોળી મૂકી છે."
                ),
                Medication(
                    name = "Evening Neuro Support",
                    dosage = "1 Capsule",
                    time = "09:00 PM",
                    period = "NIGHT",
                    instructionsEn = "Take 1 capsule 30 minutes before bedtime with warm milk or water.",
                    instructionsHi = "सोने से 30 मिनट पहले 1 कैप्सूल गरम दूध या पानी के साथ लें।",
                    instructionsGu = "સૂતા પહેલા 30 મિનિટ અગાઉ ગરમ દૂધ કે પાણી સાથે 1 કેપ્સ્યુલ લો.",
                    status = MedicationStatus.PENDING,
                    caregiverVoicePromptEn = "Ba, here is your evening capsule before going to bed.",
                    caregiverVoicePromptHi = "बा, सोने से पहले की आपकी शाम की दवाई।",
                    caregiverVoicePromptGu = "બા, સૂતા પહેલા તમારી સાંજની કેપ્સ્યુલ લઈ લો."
                )
            )
        )

        // Seed Appointments
        db.appointmentDao().insertAppointments(
            listOf(
                Appointment(
                    doctorName = "Dr. Rajesh Patel",
                    specialty = "Senior Neurologist",
                    hospitalClinic = "Apollo Hospital / Ahmedabad Neuro Care",
                    date = "Today, 22 Aug",
                    time = "05:00 PM",
                    purposeEn = "Routine monthly memory & wellness check-up",
                    purposeHi = "नियमित मासिक स्मृति और स्वास्थ्य जांच",
                    purposeGu = "નિયમિત માસિક સ્મૃતિ અને સામાન્ય સ્વાસ્થ્ય તપાસ",
                    location = "Bodakdev, Ahmedabad",
                    iconEmoji = "👩‍⚕️"
                ),
                Appointment(
                    doctorName = "Dr. Ananya Shah",
                    specialty = "Eye Specialist",
                    hospitalClinic = "Vision Care Clinic",
                    date = "28 August 2026",
                    time = "11:00 AM",
                    purposeEn = "Reading glasses vision test",
                    purposeHi = "चश्मे की दृष्टि जांच",
                    purposeGu = "વાંચવાના ચશ્માની તપાસ",
                    location = "Navrangpura, Ahmedabad",
                    iconEmoji = "👓"
                )
            )
        )

        // Seed Family Members with rich voice intros
        db.familyDao().insertFamilyMembers(
            listOf(
                FamilyMember(
                    name = "Meena",
                    relationshipEn = "Daughter & Primary Caregiver",
                    relationshipHi = "बेटी और मुख्य देखभालकर्ता",
                    relationshipGu = "દિકરી અને મુખ્ય સંભાળ રાખનાર",
                    phone = "+91 98765 43210",
                    iconEmoji = "👩",
                    photoColorHex = 0xFF00695C,
                    introEn = "This is Meena. She is your loving daughter who stays with you in Ahmedabad and takes care of you every day.",
                    introHi = "यह मीना है। यह आपकी प्यारी बेटी है जो आपके साथ अहमदाबाद में रहती है और आपकी हर ज़रूरत का ध्यान रखती है।",
                    introGu = "આ મીના છે. આ તમારી વહાલી દિકરી છે જે તમારી સાથે અમદાવાદમાં રહે છે અને દરરોજ તમારી સંભાળ રાખે છે.",
                    sortPriority = 1
                ),
                FamilyMember(
                    name = "Raj",
                    relationshipEn = "Son",
                    relationshipHi = "बेटा",
                    relationshipGu = "દીકરો",
                    phone = "+91 98222 33445",
                    iconEmoji = "👨",
                    photoColorHex = 0xFF9A5B00,
                    introEn = "This is Raj. He is your son who works in Mumbai and calls you every evening at 7 PM.",
                    introHi = "यह राज है। यह आपका बेटा है जो मुंबई में काम करता है और रोज़ शाम 7 बजे आपसे बात करता है।",
                    introGu = "આ રાજ છે. આ તમારો દીકરો છે જે મુંબઈમાં કામ કરે છે અને દરરોજ સાંજે 7 વાગે તમને ફોન કરે છે.",
                    sortPriority = 2
                ),
                FamilyMember(
                    name = "Pooja",
                    relationshipEn = "Granddaughter",
                    relationshipHi = "पोती",
                    relationshipGu = "પૌત્રી",
                    phone = "+91 98333 44556",
                    iconEmoji = "👧",
                    photoColorHex = 0xFF3F6359,
                    introEn = "This is Pooja. She is your granddaughter who loves listening to your stories and eating your homemade laddoos.",
                    introHi = "यह पूजा है। यह आपकी पोती है जो आपसे कहानियां सुनना बहुत पसंद करती है।",
                    introGu = "આ પૂજા છે. આ તમારી પૌત્રી છે જેને તમારી વાર્તાઓ સાંભળવી અને તમારા હાથના લાડુ ખાવા બહુ ગમે છે.",
                    sortPriority = 3
                ),
                FamilyMember(
                    name = "Dr. Rajesh Patel",
                    relationshipEn = "Family Doctor",
                    relationshipHi = "पारिवारिक डॉक्टर",
                    relationshipGu = "ફેમિલી ડૉક્ટર",
                    phone = "+91 98123 45678",
                    iconEmoji = "👨‍⚕️",
                    photoColorHex = 0xFF0D47A1,
                    introEn = "This is Dr. Rajesh Patel. He is your trusted doctor who helps keep you healthy and strong.",
                    introHi = "यह डॉक्टर राजेश पटेल हैं। यह आपके डॉक्टर हैं जो आपको स्वस्थ रखने में मदद करते हैं।",
                    introGu = "આ ડૉક્ટર રાજેશ પટેલ છે. આ તમારા વિશ્વાસુ ડૉક્ટર છે જે તમારા સ્વાસ્થ્યનું ધ્યાન રાખે છે.",
                    sortPriority = 4
                )
            )
        )

        // Seed Memories
        db.memoryDao().insertMemories(
            listOf(
                Memory(
                    titleEn = "Meena's Joyful Wedding",
                    titleHi = "मीना की शादी का सुंदर दिन",
                    titleGu = "મીનાના લગ્નનો આનંદમય દિવસ",
                    descriptionEn = "Everyone gathered in Ahmedabad. You wore a gorgeous red and gold bandhani saree and blessed Meena with so much happiness.",
                    descriptionHi = "अहमदाबाद में सभी रिश्तेदार आए थे। आपने लाल और सुनहरी बांधनी साड़ी पहनी थी और मीना को भरपूर आशीर्वाद दिया था।",
                    descriptionGu = "અમદાવાદમાં બધા સ્નેહીજનો ભેગા થયા હતા. તમે સુંદર લાલ-સોનેરી બાંધણી સાડી પહેરી હતી અને મીનાને ખૂબ આશીર્વાદ આપ્યા હતા.",
                    yearOrEra = "December 2012",
                    location = "Ahmedabad",
                    iconEmoji = "🌸",
                    photoColorHex = 0xFF9A5B00,
                    promptEn = "Do you remember the beautiful red bandhani saree you wore on this day?",
                    promptHi = "क्या आपको याद है कि इस दिन आपने लाल बांधनी साड़ी पहनी थी?",
                    promptGu = "શું તમને યાદ છે કે આ દિવસે તમે લાલ બાંધણી સાડી પહેરી હતી?"
                ),
                Memory(
                    titleEn = "Family Pilgrimage to Somnath Temple",
                    titleHi = "सोमनाथ मंदिर की पारिवारिक यात्रा",
                    titleGu = "સોમનાથ મહાદેવની પરિવાર સાથે યાત્રા",
                    descriptionEn = "A peaceful morning by the sea listening to the temple bells and evening Aarti with the whole family.",
                    descriptionHi = "समुद्र किनारे मंदिर की घंटियों की आवाज़ और पूरे परिवार के साथ संध्या आरती का सुखद अनुभव।",
                    descriptionGu = "દરિયા કિનારે મંદિરમાં ઘંટનાદ અને આખા પરિવાર સાથે સાંજની આરતીનું પવિત્ર દર્શન.",
                    yearOrEra = "Winter 2018",
                    location = "Somnath, Gujarat",
                    iconEmoji = "🛕",
                    photoColorHex = 0xFF00695C,
                    promptEn = "Do you remember the sound of the ocean waves near Somnath temple?",
                    promptHi = "क्या आपको सोमनाथ मंदिर के पास समुद्र की लहरों की आवाज़ याद है?",
                    promptGu = "શું તમને સોમનાથ મંદિર પાસે દરિયાના મોજાંનો અવાજ યાદ છે?"
                ),
                Memory(
                    titleEn = "Old Courtyard House in Siddhpur",
                    titleHi = "सिद्धपुर का पुराना आंगन वाला घर",
                    titleGu = "સિદ્ધપુરનું જૂનું આંગણાવાળું ઘર",
                    descriptionEn = "The mango tree in the courtyard where all the children played in summer, and you prepared fresh mango pickle.",
                    descriptionHi = "आंगन में आम का पेड़ जहाँ बच्चे गर्मियों में खेलते थे और आप ताज़ा आम का अचार बनाती थीं।",
                    descriptionGu = "આંગણામાં આંબો જ્યાં ઉનાળામાં બાળકો રમતા અને તમે તાજું કેરીનું અથાણું બનાવતા હતા.",
                    yearOrEra = "Childhood & Family Home",
                    location = "Siddhpur, Gujarat",
                    iconEmoji = "🏡",
                    photoColorHex = 0xFF3F6359,
                    promptEn = "Do you remember the sweet smell of the mango blossoms in the courtyard?",
                    promptHi = "क्या आपको आंगन में आम के बौर की मीठी खुशबू याद है?",
                    promptGu = "શું તમને આંગણામાં આંબાના મહોરની મીઠી સુગંધ યાદ છે?"
                ),
                Memory(
                    titleEn = "Granddaughter Pooja's Graduation Day",
                    titleHi = "पोती पूजा का दीक्षांत समारोह",
                    titleGu = "પૌત્રી પૂજાનો ગ્રેજ્યુએશન દીક્ષાંત દિવસ",
                    descriptionEn = "Pooja received her university degree with honors and touched your feet with pride.",
                    descriptionHi = "पूजा ने विश्वविद्यालय की डिग्री हासिल की और गर्व के साथ आपके चरण स्पर्श किए।",
                    descriptionGu = "પૂજાએ ડિગ્રી મેળવીને ગૌરવ સાથે તમારા ચરણ સ્પર્શ કર્યા હતા.",
                    yearOrEra = "May 2024",
                    location = "Gujarat University",
                    iconEmoji = "🎓",
                    photoColorHex = 0xFF0D47A1,
                    promptEn = "Look how tall and happy Pooja looks next to you!",
                    promptHi = "देखिए पूजा आपके पास कितनी खुश दिख रही है!",
                    promptGu = "જુઓ પૂજા તમારી પાસે કેટલી ખુશ દેખાય છે!"
                )
            )
        )

        // Seed Mood entries
        db.moodDao().insertMoodEntry(
            MoodEntry(
                timestamp = System.currentTimeMillis() - 86400000L,
                dateFormatted = "Yesterday",
                timeFormatted = "04:30 PM",
                moodType = MoodType.HAPPY,
                note = "Enjoyed looking at old photo albums with Meena.",
                caregiverNotified = true
            )
        )
        db.moodDao().insertMoodEntry(
            MoodEntry(
                timestamp = System.currentTimeMillis() - 14400000L,
                dateFormatted = "Today",
                timeFormatted = "09:00 AM",
                moodType = MoodType.OKAY,
                note = "Ate breakfast peacefully and completed morning prayer.",
                caregiverNotified = true
            )
        )

        // Seed Emergency Contacts
        db.emergencyContactDao().insertContacts(
            listOf(
                EmergencyContact(
                    name = "Meena (Daughter & Caregiver)",
                    relationshipEn = "Daughter (Immediate Home Contact)",
                    relationshipHi = "बेटी (घर पर मुख्य संपर्क)",
                    relationshipGu = "દિકરી (ઘરે મુખ્ય સંપર્ક)",
                    phone = "+91 98765 43210",
                    priority = 1,
                    iconEmoji = "👩"
                ),
                EmergencyContact(
                    name = "Raj (Son)",
                    relationshipEn = "Son (Mumbai)",
                    relationshipHi = "बेटा (मुंबई)",
                    relationshipGu = "દીકરો (મુંબઈ)",
                    phone = "+91 98222 33445",
                    priority = 2,
                    iconEmoji = "👨"
                ),
                EmergencyContact(
                    name = "Dr. Rajesh Patel (Neurologist)",
                    relationshipEn = "Family Doctor & Specialist",
                    relationshipHi = "पारिवारिक डॉक्टर",
                    relationshipGu = "ફેમિલી સ્પેશિયાલિસ્ટ ડૉક્ટર",
                    phone = "+91 98123 45678",
                    priority = 3,
                    iconEmoji = "👨‍⚕️"
                ),
                EmergencyContact(
                    name = "Apollo Hospital Emergency",
                    relationshipEn = "Emergency Medical Helpline",
                    relationshipHi = "आपातकालीन चिकित्सा सहायता",
                    relationshipGu = "ઇમરજન્સી મેડિકલ હેલ્પલાઇન",
                    phone = "108",
                    priority = 4,
                    iconEmoji = "🏥"
                )
            )
        )

        // Seed Caregiver Notes
        db.caregiverNoteDao().insertNote(
            CaregiverNote(
                dateFormatted = "21 Aug 2026",
                title = "Positive response to Somnath photo album",
                content = "Ba smiled warmly when seeing the Somnath temple photo and remembered the sea Aarti. Her orientation was sharp during evening chai.",
                category = "Observation"
            )
        )
        db.caregiverNoteDao().insertNote(
            CaregiverNote(
                dateFormatted = "22 Aug 2026",
                title = "Morning routine adherence 100%",
                content = "Morning breakfast and BP medication taken on time with water. Mood was peaceful.",
                category = "Medication"
            )
        )
    }
}
