package com.example.localization

import com.example.model.AppLanguage

object Localization {
    private val en = mapOf(
        "app_name" to "Hayner Spin",
        "tagline" to "Premium Spin Experience",
        "spin_now" to "SPIN NOW",
        "spinning" to "SPINNING...",
        "spin_again" to "SPIN AGAIN",
        "nav_home" to "Home",
        "nav_wheels" to "My Wheels",
        "nav_spin" to "Spin",
        "nav_history" to "History",
        "nav_profile" to "Profile",
        
        // Onboarding
        "onboarding_1_title" to "Welcome to Hayner Spin",
        "onboarding_1_desc" to "Discover a fun spinning-wheel experience with customizable rewards and exciting animations.",
        "onboarding_2_title" to "Every Spin Is an Adventure",
        "onboarding_2_desc" to "Customize your wheel, explore different rewards, and enjoy a smooth spinning experience.",
        "onboarding_3_title" to "Your Wheel Is Ready",
        "onboarding_3_desc" to "Create your own spinning experience and start exploring Hayner Spin.",
        "btn_skip" to "Skip",
        "btn_next" to "Next",
        "btn_get_started" to "Get Started",

        // Home
        "dashboard_title" to "Hayner Dashboard",
        "total_spins" to "Total Spins",
        "last_result" to "Last Result",
        "streak_days" to "Spin Streak",
        "available_themes" to "Themes",
        "daily_challenges" to "Daily Challenges",
        "recent_activity" to "Recent Activity",
        "no_recent_activity" to "No spins yet. Tap SPIN NOW to begin!",
        "practice_mode_badge" to "PRACTICE (NO MONETARY VALUE)",

        // Spin screen
        "mode_standard" to "Standard",
        "mode_practice" to "Practice",
        "mode_quick" to "Quick Spin",
        "active_wheel" to "Active Wheel",
        "tap_or_flick" to "Tap SPIN NOW or flick the wheel!",
        "wheel_ready" to "Ready to Spin",

        // Result Dialog
        "result_congratulations" to "Congratulations!",
        "result_try_again_title" to "Keep Trying!",
        "result_reward_badge" to "Reward Won",
        "result_spin_num" to "Spin #",
        "result_disclaimer" to "Demonstration entertainment reward. No real cash value.",
        "btn_back_home" to "Back to Home",
        "btn_share" to "Share Result",

        // My Wheels & Editor
        "wheels_title" to "Custom Wheels",
        "create_new_wheel" to "+ Create New Wheel",
        "edit_wheel" to "Edit Wheel",
        "wheel_name" to "Wheel Name",
        "wheel_description" to "Description",
        "segments" to "Segments",
        "add_segment" to "Add Segment",
        "segment_label" to "Segment Label",
        "choose_color" to "Segment Color",
        "save_wheel" to "Save Wheel",
        "duplicate_wheel" to "Duplicate",
        "delete_wheel" to "Delete Wheel",
        "reset_defaults" to "Reset to Defaults",
        "select_active" to "Use Wheel",
        "active_badge" to "ACTIVE",
        "min_segments_notice" to "Wheel must have between 2 and 16 segments.",

        // History
        "history_title" to "Spin History",
        "search_history" to "Search results...",
        "filter_all" to "All",
        "filter_wins" to "Wins Only",
        "filter_practice" to "Practice",
        "clear_history" to "Clear History",
        "empty_history_title" to "No Spin History",
        "empty_history_desc" to "Every spin you make will appear here with date and outcome.",

        // Profile
        "profile_title" to "Profile & Stats",
        "edit_name" to "Edit Name",
        "choose_avatar" to "Choose Avatar",
        "stats_overview" to "Statistics Overview",
        "best_streak" to "Best Streak",
        "wheels_created" to "Custom Wheels",
        "reset_stats" to "Reset Statistics",

        // Challenges
        "challenge_spin_3" to "Complete 3 spins today",
        "challenge_themes" to "Try 2 different themes",
        "challenge_custom" to "Create or customize a wheel",
        "challenge_practice" to "Complete 5 practice spins",
        "claimed" to "Claimed",
        "claim" to "Claim",

        // Settings
        "settings_title" to "Settings",
        "appearance" to "Appearance & Themes",
        "sound_effects" to "Sound Effects",
        "sound_desc" to "Play mechanical tick and victory fanfare",
        "vibration" to "Haptic Feedback",
        "vibration_desc" to "Vibrate on needle tick and prize stop",
        "animations" to "Smooth Animations",
        "animations_desc" to "Particle celebrations and smooth motion",
        "language" to "Language (Luqadda)",
        "privacy_policy" to "Privacy Information",
        "terms" to "Terms and Conditions",
        "about" to "About Hayner Spin",
        "version" to "Version 1.0.0",
        "offline_notice" to "Offline-ready. All data stored strictly on your device.",
        "reset_all_data" to "Reset All App Data",
        "confirm_clear_history" to "Are you sure you want to clear your spin history?",
        "confirm_reset_stats" to "Reset all spin counts, streaks and records?",
        "confirm_reset_all" to "Erase all custom wheels and reset everything to factory default?",
        "btn_confirm" to "Confirm",
        "btn_cancel" to "Cancel"
    )

    private val so = mapOf(
        "app_name" to "Hayner Spin",
        "tagline" to "Khibrad Sare Oo Wareeg ah",
        "spin_now" to "WAREEJI HADDA",
        "spinning" to "WUU WAREEGAYAA...",
        "spin_again" to "KALE WAREEJI",
        "nav_home" to "Guriga",
        "nav_wheels" to "Gawaadhida",
        "nav_spin" to "Wareeji",
        "nav_history" to "Taariikhda",
        "nav_profile" to "Muuqaalka",

        // Onboarding
        "onboarding_1_title" to "Kusoo Dhawoow Hayner Spin",
        "onboarding_1_desc" to "Baadh khibrad xiiso leh oo shaag wareeg ah oo leh abaalmarino la hagaajin karo iyo sawir-gacmeedyo cajiib ah.",
        "onboarding_2_title" to "Wareeg Kasta Waa Fursad",
        "onboarding_2_desc" to "Hagaaji shaagaaga, baro abaalmarino kala duwan, oo ku raaxayso wareejin habsami leh.",
        "onboarding_3_title" to "Shaagaagu Waa Diyaar",
        "onboarding_3_desc" to "Abuur khibradaada gaarka ah ee wareegga oo bilow sahaminta Hayner Spin.",
        "btn_skip" to "Ka bood",
        "btn_next" to "Xiga",
        "btn_get_started" to "Bilow Hadda",

        // Home
        "dashboard_title" to "Bogga Hayner",
        "total_spins" to "Wadarta Wareegyada",
        "last_result" to "Natiijadii Ugu Dambaysay",
        "streak_days" to "Xidhiidhka Maalmaha",
        "available_themes" to "Muuqaallada",
        "daily_challenges" to "Tartamada Maalinlaha",
        "recent_activity" to "Dhaqdhaqaaqii Dhowaa",
        "no_recent_activity" to "Weli wareeg ma jiro. Riix WAREEJI HADDA si aad u bilowdo!",
        "practice_mode_badge" to "LAYLIS (LACAG MA LAHA)",

        // Spin screen
        "mode_standard" to "Caadi",
        "mode_practice" to "Laylis",
        "mode_quick" to "Dhaqso",
        "active_wheel" to "Shaaga Shaqaynaya",
        "tap_or_flick" to "Riix WAREEJI HADDA ama farta ku riix shaaga!",
        "wheel_ready" to "Waa Diyaar",

        // Result Dialog
        "result_congratulations" to "Hambalyo!",
        "result_try_again_title" to "Mar Kale Isku Day!",
        "result_reward_badge" to "Abaalmarin Guul",
        "result_spin_num" to "Wareegga #",
        "result_disclaimer" to "Abaalmarintani waa madadaalo oo keliya. Qiimo lacageed oo dhab ah ma laha.",
        "btn_back_home" to "Ku Noqo Guriga",
        "btn_share" to "La Wadaag",

        // My Wheels & Editor
        "wheels_title" to "Gawaadhidaada Gaarka ah",
        "create_new_wheel" to "+ Abuur Shaag Cusub",
        "edit_wheel" to "Wax Ka Beddel Shaaga",
        "wheel_name" to "Magaca Shaaga",
        "wheel_description" to "Faahfaahin",
        "segments" to "Qaybaha",
        "add_segment" to "Kudar Qayb",
        "segment_label" to "Qoraalka Qaybta",
        "choose_color" to "Midabka Qaybta",
        "save_wheel" to "Keydi Shaaga",
        "duplicate_wheel" to "Nuqul Ka Samee",
        "delete_wheel" to "Tirtir Shaaga",
        "reset_defaults" to "Dib Ugu Celi Asalkii",
        "select_active" to "Isticmaal Shaagan",
        "active_badge" to "SHAQAYNAYA",
        "min_segments_notice" to "Shaagu waa inuu lahaadaa inta u dhaxaysa 2 iyo 16 qaybood.",

        // History
        "history_title" to "Taariikhda Wareegyada",
        "search_history" to "Raadi natiijooyinka...",
        "filter_all" to "Dhammaan",
        "filter_wins" to "Guulaha Kaliya",
        "filter_practice" to "Laylis",
        "clear_history" to "Tirtir Taariikhda",
        "empty_history_title" to "Taariikh Ma Jirto",
        "empty_history_desc" to "Wareeg kasta oo aad samayso halkan ayuu ku soo muuqan doonaa.",

        // Profile
        "profile_title" to "Muuqaalka & Tirakoobka",
        "edit_name" to "Beddel Magaca",
        "choose_avatar" to "Dooro Sawirka",
        "stats_overview" to "Dulmarka Tirakoobka",
        "best_streak" to "Xidhiidhkii Ugu Wacnaa",
        "wheels_created" to "Gawaadhida La Sameeyay",
        "reset_stats" to "Dib U Deji Tirakoobka",

        // Challenges
        "challenge_spin_3" to "Samee 3 wareeg maanta",
        "challenge_themes" to "Isku day 2 muuqaal oo kala duwan",
        "challenge_custom" to "Abuur ama wax ka beddel shaag",
        "challenge_practice" to "Samee 5 wareeg oo laylis ah",
        "claimed" to "La Qaatay",
        "claim" to "Qaado",

        // Settings
        "settings_title" to "Dejinta",
        "appearance" to "Muuqaalka & Midabada",
        "sound_effects" to "Dhawaaqyada",
        "sound_desc" to "Daar dhawaaqa wareegga iyo guusha",
        "vibration" to "Gariirka (Haptic)",
        "vibration_desc" to "Gariir marka uu cirbadda maro iyo marka uu joogsado",
        "animations" to "Dhaqdhaqaaqa Quruxda Badan",
        "animations_desc" to "Xafladda dhalanteedka iyo dhaqdhaqaaqa habsami leh",
        "language" to "Luqadda (Language)",
        "privacy_policy" to "Xogta Gaarka ah",
        "terms" to "Shuruudaha & Xeerarka",
        "about" to "Ku Saabsan Hayner Spin",
        "version" to "Nooca 1.0.0",
        "offline_notice" to "Waxay ku shaqaysaa bilaa internet. Dhammaan xogtu waxay ku kaydsan tahay taleefankaaga.",
        "reset_all_data" to "Dib U Deji Dhammaan Xogta",
        "confirm_clear_history" to "Ma hubtaa inaad doonayso inaad tirtirto taariikhda wareegyada?",
        "confirm_reset_stats" to "Dib ma u dejisaa dhammaan tirakoobka iyo xidhiidhka?",
        "confirm_reset_all" to "Dhammaan shaagyada aad samaysay miyaa la tirtiraa dibna loogu celiyaa asalkii?",
        "btn_confirm" to "Xaqiiji",
        "btn_cancel" to "Jooji"
    )

    fun get(key: String, lang: AppLanguage): String {
        val dict = if (lang == AppLanguage.SOMALI) so else en
        return dict[key] ?: en[key] ?: key
    }
}
