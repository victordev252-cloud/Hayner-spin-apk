package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class SoundEffects(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)

    var isSoundEnabled: Boolean = true
    var isVibrationEnabled: Boolean = true

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    /**
     * Plays a short mechanical wheel tick sound.
     */
    fun playTick() {
        if (!isSoundEnabled) return
        scope.launch {
            generateTone(frequency = 750.0, durationMs = 28, volume = 0.55f, isClick = true)
        }
    }

    /**
     * Plays a pleasant victory arpeggio when winning a reward.
     */
    fun playWinFanfare() {
        if (!isSoundEnabled) return
        scope.launch {
            val notes = listOf(523.25, 659.25, 783.99, 1046.50) // C5, E5, G5, C6
            for (note in notes) {
                generateTone(frequency = note, durationMs = 110, volume = 0.7f, isClick = false)
            }
        }
    }

    /**
     * Plays a gentle Try Again chime.
     */
    fun playTryAgainChime() {
        if (!isSoundEnabled) return
        scope.launch {
            generateTone(frequency = 440.0, durationMs = 90, volume = 0.5f, isClick = false)
            generateTone(frequency = 392.0, durationMs = 150, volume = 0.5f, isClick = false)
        }
    }

    /**
     * Plays a soft button tap sound.
     */
    fun playButtonTap() {
        if (!isSoundEnabled) return
        scope.launch {
            generateTone(frequency = 900.0, durationMs = 20, volume = 0.35f, isClick = true)
        }
    }

    /**
     * Vibration tick as pegs pass pointer.
     */
    fun vibrateTick() {
        if (!isVibrationEnabled) return
        try {
            vibrator?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(12)
                }
            }
        } catch (_: Exception) {
            // Ignore if permissions or device lacks vibrator
        }
    }

    /**
     * Vibration pulse for wheel stop and victory.
     */
    fun vibrateWin() {
        if (!isVibrationEnabled) return
        try {
            vibrator?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timings = longArrayOf(0, 70, 50, 110, 50, 150)
                    val amplitudes = intArrayOf(0, 180, 0, 220, 0, 255)
                    v.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(longArrayOf(0, 80, 60, 120), -1)
                }
            }
        } catch (_: Exception) {
            // Ignore safely
        }
    }

    private fun generateTone(frequency: Double, durationMs: Int, volume: Float, isClick: Boolean) {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val time = i.toDouble() / sampleRate
            // Apply envelope to prevent audio popping
            val envelope = if (isClick) {
                (1.0 - (i.toDouble() / numSamples))
            } else {
                when {
                    i < numSamples * 0.1 -> i / (numSamples * 0.1)
                    else -> 1.0 - ((i - numSamples * 0.1) / (numSamples * 0.9))
                }
            }
            val angle = 2.0 * Math.PI * frequency * time
            val sample = (sin(angle) * envelope * Short.MAX_VALUE * volume).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            Thread.sleep(durationMs.toLong() + 20)
            audioTrack.stop()
            audioTrack.release()
        } catch (_: Exception) {
            // Audio generation fallback without crashing
        }
    }
}
