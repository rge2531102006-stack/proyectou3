package com.example.controldegastos.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.controldegastos.R
import com.example.controldegastos.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAuthenticate.setOnClickListener {
            startBiometricCheck()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        startBiometricCheck()
    }

    private fun startBiometricCheck() {
        val biometricManager = BiometricManager.from(this)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK

        when (biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> showBiometricPrompt(authenticators)
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                binding.tvStatusMessage.text = getString(R.string.err_no_sensor)
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                binding.tvStatusMessage.text = getString(R.string.err_hw_unavailable)
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                binding.tvStatusMessage.text = getString(R.string.err_no_enrolled)
            else ->
                binding.tvStatusMessage.text = getString(R.string.err_hw_unavailable)
        }
    }

    private fun showBiometricPrompt(authenticators: Int) {
        val executor = ContextCompat.getMainExecutor(this)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                binding.tvStatusMessage.text = when (errorCode) {
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> getString(R.string.err_canceled)
                    BiometricPrompt.ERROR_LOCKOUT,
                    BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> getString(R.string.err_lockout)
                    BiometricPrompt.ERROR_NO_BIOMETRICS -> getString(R.string.err_no_enrolled)
                    BiometricPrompt.ERROR_HW_NOT_PRESENT -> getString(R.string.err_no_sensor)
                    BiometricPrompt.ERROR_HW_UNAVAILABLE -> getString(R.string.err_hw_unavailable)
                    else -> errString.toString()
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                binding.tvStatusMessage.text = getString(R.string.err_not_recognized)
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_title))
            .setSubtitle(getString(R.string.biometric_subtitle))
            .setNegativeButtonText(getString(R.string.biometric_negative))
            .setAllowedAuthenticators(authenticators)
            .build()

        BiometricPrompt(this, executor, callback).authenticate(promptInfo)
    }
}