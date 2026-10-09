// Hayner Spin Local Storage Manager

const STORAGE_KEYS = {
  ONBOARDING: 'hayner_onboarding_completed',
  WHEELS: 'hayner_custom_wheels',
  ACTIVE_WHEEL_ID: 'hayner_active_wheel_id',
  HISTORY: 'hayner_spin_history',
  PROFILE: 'hayner_user_profile',
  CHALLENGES: 'hayner_daily_challenges',
  SETTINGS: 'hayner_app_settings'
};

export const DEFAULT_WHEEL = {
  id: 'wheel_hayner_default',
  name: 'Hayner Spin Classic',
  description: 'Signature demonstration prize wheel with 8 lucky segments.',
  isCustom: false,
  segments: [
    { id: '1', label: 'Try Again', colorHex: '#E11D48', category: 'TRY_AGAIN' },
    { id: '2', label: 'Bonus Spin', colorHex: '#7C3AED', category: 'BONUS' },
    { id: '3', label: 'Small Reward', colorHex: '#0284C7', category: 'SMALL' },
    { id: '4', label: 'Mystery Reward', colorHex: '#9333EA', category: 'MYSTERY' },
    { id: '5', label: 'Lucky Spin', colorHex: '#059669', category: 'LUCKY' },
    { id: '6', label: 'Special Prize', colorHex: '#D97706', category: 'SPECIAL' },
    { id: '7', label: 'No Prize', colorHex: '#64748B', category: 'NO_PRIZE' },
    { id: '8', label: 'Surprise Reward', colorHex: '#DB2777', category: 'SURPRISE' }
  ]
};

export const StorageService = {
  getOnboarding() {
    return localStorage.getItem(STORAGE_KEYS.ONBOARDING) === 'true';
  },
  setOnboarding(completed) {
    localStorage.setItem(STORAGE_KEYS.ONBOARDING, String(completed));
  },
  getWheels() {
    const raw = localStorage.getItem(STORAGE_KEYS.WHEELS);
    return raw ? JSON.parse(raw) : [DEFAULT_WHEEL];
  },
  saveWheels(wheels) {
    localStorage.setItem(STORAGE_KEYS.WHEELS, JSON.stringify(wheels));
  },
  getActiveWheelId() {
    return localStorage.getItem(STORAGE_KEYS.ACTIVE_WHEEL_ID) || DEFAULT_WHEEL.id;
  },
  setActiveWheelId(id) {
    localStorage.setItem(STORAGE_KEYS.ACTIVE_WHEEL_ID, id);
  },
  getHistory() {
    const raw = localStorage.getItem(STORAGE_KEYS.HISTORY);
    return raw ? JSON.parse(raw) : [];
  },
  addHistoryItem(item) {
    const history = this.getHistory();
    const updated = [item, ...history].slice(0, 100);
    localStorage.setItem(STORAGE_KEYS.HISTORY, JSON.stringify(updated));
    return updated;
  },
  clearHistory() {
    localStorage.removeItem(STORAGE_KEYS.HISTORY);
  },
  getSettings() {
    const raw = localStorage.getItem(STORAGE_KEYS.SETTINGS);
    return raw ? JSON.parse(raw) : {
      theme: 'PURPLE_GALAXY',
      language: 'en',
      sound: true,
      vibration: true,
      animations: true
    };
  },
  saveSettings(settings) {
    localStorage.setItem(STORAGE_KEYS.SETTINGS, JSON.stringify(settings));
  },
  resetAll() {
    localStorage.clear();
  }
};
