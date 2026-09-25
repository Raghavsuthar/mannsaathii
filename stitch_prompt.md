# MannSaathi — Google Stitch UI Generation Prompt (copy-paste into Stitch)

Generate a realistic, production-quality Android mobile UI for **MannSaathi (मनसाथી)** — a warm, dignified dementia & elder-care companion app for elderly patients (60–85y, mild-to-moderate cognitive impairment) and their family caregivers in India. Trilingual: English + Hindi (Devanagari) + Gujarati. Every screen must show BOTH English and regional script together (e.g. "PEOPLE / પરિવાર / लोग").

## Design system (strict — Warm Tactile Bento)
- Canvas: #FBFDF7 warm off-white. Text: #1A1C18. Surfaces: #FFFFFF with explicit 2px solid borders (never translucency, never thin hairlines).
- Header bar: bg #DDE6C7, border #C6CFB1, 28px bottom radius. Left: white rounded icon tile with red heart + "MannSaathi" bold + "Patient Mode" in #386B1D. Right: white pill buttons (picture toggle, "🇮🇳 ગુજરાતી" language, lock, info).
- Brand green: #386B1D, light container #E2F1CD. Emergency red: bg #BA1A1A, pressed #93000A, border #800006, text white.
- 8 Bento module colors (bg / border / text, all opaque):
  1. People: #FCE1D1 / #E5B69E / #5C280D
  2. My Day: #E0E2ED / #BFC3D4 / #242D42
  3. Memories: #F7F2D7 / #DBD3AA / #4A3F14
  4. Play: #D1E8FF / #A5C7E6 / #0D3A63
  5. Medicine: #FFE0D6 / #F8BBA8 / #6E1C06
  6. Mood: #D7F3DF / #AEE0BE / #144D25
  7. Today: #E2F1CD / #C6DF9D / #2B5216
  8. Ask Saathi: #EDE3FF / #D2BEFF / #3B1A7A
- Shapes: Bento tiles 36px radius, min-height 148dp, 20dp padding; buttons pill 9999px, min-height 64dp (elder tremor-friendly); chips 44dp height pill; inputs 64dp height, 20px radius, white with 2px #C6CFB1 border, 3px #386B1D on focus.
- Depth: NO blurred shadows. Tactile 2px borders + crisp 0x4px bottom-edge press effect. Decorative 64dp halo circle at top-right of each tile at 30% border color.
- Typography: Plus Jakarta Sans (Latin) + Noto Sans Devanagari/Gujarati at same scale. Weights ONLY 500–800. Patient text never below 16px; titles ExtraBold 17–30px uppercase with wide tracking; subtitles 11px bold at 85% opacity. Gujarati/Hindi line-height +4px to avoid matra clipping. Left-aligned only.
- Imagery: photorealistic, warm, Indian: smiling 72-year-old Gujarati woman (Kamla Ben, silver bun, cotton saree), family photos, Ahmedabad home terrace at golden hour, marigold, steel thali, temple courtyard. Soft morning light, shallow depth of field, dignified — never clinical, never stock-cold, never cartoonish beyond small emoji glyphs (44px) on tiles.

## Screens to generate (360x800 Android, light mode)
1. **Home (Patient Mode):** greeting banner (top): white date pill "● FRIDAY, 25 SEP", big green "નમસ્તે," + black "Kamla Ben", "શુભ સવાર • Good Morning", right circular white speaker button + "10:42 AM" + "📍 Ahmedabad, GJ", bottom white next-up pill (💊 icon, "NEXT UP • હવે પછી / Morning BP Medicine", green time chip "11:00 AM"). 2-col grid of 8 tiles (PEOPLE 👨‍👩‍👧, MY DAY 🕐, MEMORIES 🎞️, PLAY 🧩, MEDICINE 💊 with red "1" badge, MOOD 😊, TODAY ☀️, ASK SAATHI 🎤) each with English + Gujarati/Hindi sub. Full-width red HELP bar ("🚨 HELP / મદદ / तुरंत सहायता" + glowing white 🆘). Bottom nav: Home (active green pill) / Caregiver / GUJ + disclaimer microcopy.
2. **Today (Orientation):** back pill "← Home ઘર" + speaker circle; title "☀️ Today આજ" + "Live Clock • સક્રિય" chip; hint "👉 Tap any card to hear it aloud • સાંભળવા માટે સ્પર્શ કરો". Cards: CURRENT TIME • સમય (10:42 AM, Friday Morning), TODAY'S DATE • આજની તારીખ (25 September 2024), WHERE YOU ARE • તમે ક્યાં છો (Ahmedabad, Gujarat — At home, surrounded by love), YOUR IDENTITY • તમારી ઓળખ (Kamla Ben Patel (Ba), Age 73), PRIMARY CAREGIVER • સંભાળ રાખનાર (Radha Patel, +91 98250 12345, તમારી સાથે છે), UPCOMING STEP • હવે પછીનું કામ (Morning BP Medicine 11 AM, warm water after tea). Each card: left white icon tile, bold value, speaker icon top-right. Bottom reassurance card: "🛡️ Everything is okay • બધું બરાબર છે — You are safe, loved, and Radha is nearby. આરામથી બેસો."
3. **People:** family photo grid with big circular portraits (daughter Radha, son Mehul, grandchildren), name + relation trilingual + green Call button per card.
4. **My Day:** vertical timeline 7:30 AM–9 PM with period filters (Morning/Afternoon/Evening/Night), DONE/LATER big buttons, delayed-task amber state.
5. **Memories:** warm album cards with realistic photo, year + place ("1985 • Somnath Temple"), "Listen Story" audio button, gentle prompt line.
6. **Play:** 2-col cognitive game cards (Picture Match, Find the Object, 3-Item Recall) — big icons, "No score, no pressure" note.
7. **Medicine:** dose cards with time chip, "I Took It" green pill + "Later" outline, taken-state checkmark, caregiver note.
8. **Mood:** 6 big emoji buttons (😀🙂😐😢😴😡) + calming soundscape cards (ocean, temple bells) with play buttons.
9. **Ask Saathi:** voice pill (#EDE3FF) with pulsing waveform, sample Q&A bubbles, mic button.
10. **Help (Emergency):** "Emergency Assistance" header, light-green "✳ Immediate Help • મદદ — Help is right here…" intro, solid-red "Call Radha 📞 →" button, "Emergency Contacts • પરિવાર / Tap to call" list (Radha Main green, doctor white, Ambulance 108 pink), green Voice Guide card, gray "YOU ARE SAFE AT HOME — 14 Shanti Kutir, Navrangpura" card.

## Constraints
- Minimum 64dp touch targets, 12px gaps, 16px screen margins, single-column mobile flow.
- Every interactive card: visible speaker/volume affordance (tap-to-hear).
- No medical diagnosis/treatment language; footer disclaimer: "MannSaathi is a daily cognitive support companion. It does not provide medical diagnosis, treatment, or clinical prescriptions."
- Output: high-fidelity realistic renders + matching clean HTML/Tailwind structure per screen, reusing the exact hex tokens above.
