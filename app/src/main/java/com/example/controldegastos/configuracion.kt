package com.example.controldegastos.ui

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.controldegastos.databinding.ActivityConfiguracionBinding
import java.util.Locale
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

class SettingsActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityConfiguracionBinding
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfiguracionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        if (accelerometer == null) {
            binding.tvTiltStatus.text = "Estado: Acelerómetro no disponible"
        }

        if (gyroscope == null) {
            binding.tvGyroX.text = "Giroscopio no disponible en este dispositivo"
            binding.tvGyroY.text = ""
            binding.tvGyroZ.text = ""
        }

        binding.switchDarkMode.isChecked =
            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        binding.btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }

        binding.btnBackToMenu.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
        gyroscope?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                binding.tvAxisX.text = String.format(Locale.getDefault(), "Eje X: %.2f", x)
                binding.tvAxisY.text = String.format(Locale.getDefault(), "Eje Y: %.2f", y)
                binding.tvAxisZ.text = String.format(Locale.getDefault(), "Eje Z: %.2f", z)

                val horizontalNorm = sqrt((x * x + y * y).toDouble())
                val tiltDegrees = abs(Math.toDegrees(atan2(horizontalNorm, z.toDouble())))

                binding.tvTiltAngle.text = String.format(Locale.getDefault(), "%.1f°", tiltDegrees)
                binding.tvTiltStatus.text = when {
                    tiltDegrees < 10.0 -> "Estado: Nivelado"
                    tiltDegrees < 35.0 -> "Estado: Inclinación moderada"
                    else -> "Estado: Inclinación pronunciada"
                }
            }

            Sensor.TYPE_GYROSCOPE -> {
                binding.tvGyroX.text = String.format(Locale.getDefault(), "Giro X: %.2f", event.values[0])
                binding.tvGyroY.text = String.format(Locale.getDefault(), "Giro Y: %.2f", event.values[1])
                binding.tvGyroZ.text = String.format(Locale.getDefault(), "Giro Z: %.2f", event.values[2])
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Sin cambios necesarios
    }
}