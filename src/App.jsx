import React, { useState, useEffect } from 'react';
import { StorageService, DEFAULT_WHEEL } from './services/storage.js';
import enTranslations from './locales/en.json';
import soTranslations from './locales/so.json';

export default function App() {
  const [activeWheel, setActiveWheel] = useState(DEFAULT_WHEEL);
  const [language, setLanguage] = useState('en');
  const [isSpinning, setIsSpinning] = useState(false);
  const [rotation, setRotation] = useState(0);
  const [lastResult, setLastResult] = useState(null);

  const t = (key) => {
    const dict = language === 'so' ? soTranslations : enTranslations;
    return dict[key] || enTranslations[key] || key;
  };

  const handleSpin = () => {
    if (isSpinning) return;
    setIsSpinning(true);
    const targetIdx = Math.floor(Math.random() * activeWheel.segments.length);
    const fullRotations = 5 * 360;
    const arcAngle = 360 / activeWheel.segments.length;
    const targetRemainder = ((270 - (targetIdx + 0.5) * arcAngle) % 360 + 360) % 360;
    const currentRem = (rotation % 360 + 360) % 360;
    let delta = targetRemainder - currentRem;
    if (delta < 0) delta += 360;
    const finalRot = rotation + fullRotations + delta;
    setRotation(finalRot);

    setTimeout(() => {
      setIsSpinning(false);
      setLastResult(activeWheel.segments[targetIdx]);
    }, 3500);
  };

  return (
    <div style={{
      minHeight: '100vh',
      backgroundColor: '#090614',
      color: '#FFFFFF',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      padding: '24px',
      boxSizing: 'border-box'
    }}>
      <header style={{ width: '100%', maxWidth: '480px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1 style={{ margin: 0, fontSize: '24px', fontWeight: 'bold', background: 'linear-gradient(90deg, #A855F7, #00DFD8)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
          HAYNER SPIN
        </h1>
        <button
          onClick={() => setLanguage(language === 'en' ? 'so' : 'en')}
          style={{ background: '#1E153E', border: '1px solid #38BDF8', color: '#FFF', borderRadius: '12px', padding: '6px 12px', cursor: 'pointer' }}
        >
          {language === 'en' ? 'Soomaali' : 'English'}
        </button>
      </header>

      <main style={{ marginTop: '36px', textAlign: 'center', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <div style={{
          width: '280px',
          height: '280px',
          borderRadius: '50%',
          border: '4px solid #FFBE0B',
          boxShadow: '0 0 30px rgba(168, 85, 247, 0.4)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          position: 'relative',
          transition: isSpinning ? 'transform 3.5s cubic-bezier(0.15, 0.9, 0.25, 1)' : 'none',
          transform: `rotate(${rotation}deg)`
        }}>
          <div style={{
            width: '70px',
            height: '70px',
            borderRadius: '50%',
            background: '#160D2E',
            border: '3px solid #00DFD8',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontSize: '32px',
            fontWeight: 'bold',
            color: '#00DFD8'
          }}>
            H
          </div>
        </div>

        <button
          onClick={handleSpin}
          disabled={isSpinning}
          style={{
            marginTop: '36px',
            padding: '16px 48px',
            fontSize: '18px',
            fontWeight: 'bold',
            background: 'linear-gradient(90deg, #A855F7, #0284C7)',
            color: '#FFF',
            border: 'none',
            borderRadius: '16px',
            cursor: isSpinning ? 'not-allowed' : 'pointer',
            boxShadow: '0 8px 24px rgba(168, 85, 247, 0.5)'
          }}
        >
          {isSpinning ? t('spinning') : t('spin_now')}
        </button>

        {lastResult && (
          <div style={{ marginTop: '24px', padding: '16px', background: '#1E153E', borderRadius: '16px', border: '1px solid #FFBE0B' }}>
            <h3 style={{ margin: 0, color: '#FFBE0B' }}>{t('result_congratulations')}</h3>
            <p style={{ margin: '8px 0 0 0', fontSize: '20px', fontWeight: 'bold' }}>{lastResult.label}</p>
          </div>
        )}
      </main>
    </div>
  );
}
