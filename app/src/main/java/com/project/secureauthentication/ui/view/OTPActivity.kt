package com.project.secureauthentication.ui.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.secureauthentication.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.project.secureauthentication.databinding.ActivityOtpactivityBinding
import com.project.secureauthentication.ui.viewmodel.OTPUiState
import com.project.secureauthentication.ui.viewmodel.OTPViewModel
import com.project.secureauthentication.ui.viewmodel.OTPViewModelEvent

class OTPActivity : AppCompatActivity() {
    private var _binding: ActivityOtpactivityBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OTPViewModel by viewModel()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        sharedPreferences = getSharedPreferences("UserRole", MODE_PRIVATE)

        val email = intent.getStringExtra("email")
        email?.let { viewModel.otpSend(it) }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.apply {

            btnVerifyOTP.setOnClickListener {
                val typedOTP =
                    (otpEditText1.text.toString() + otpEditText2.text.toString() + otpEditText3.text.toString() + otpEditText4.text.toString() + otpEditText5.text.toString() + otpEditText6.text.toString())

                if (typedOTP.isNotEmpty()) {
                    if (typedOTP.length == 6) {
                        viewModel.verifyOTP(typedOTP)
                    } else {
                        Toast.makeText(
                            this@OTPActivity, "Please Enter Correct OTP", Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@OTPActivity, "Please Enter OTP", Toast.LENGTH_SHORT
                    ).show()
                }
            }

            resendTextView.setOnClickListener {
                email?.let { viewModel.otpSend(it) }
                Toast.makeText(this@OTPActivity, "A new OTP has been sent to your email address", Toast.LENGTH_SHORT).show()
            }
        }
        addTextChangeListener()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) {
            when (it) {
                OTPUiState.Loading -> {
                    binding.btnVerifyOTP.isEnabled = false
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnVerifyOTP.text = null
                }

                OTPUiState.OTPSendingSuccess -> {
                    Toast.makeText(
                        this@OTPActivity,
                        "OTP has been sent to your email address",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                OTPUiState.OTPVerifySuccess -> {
                    binding.btnVerifyOTP.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                    binding.btnVerifyOTP.text = "VERIFY"
                    Log.i("SendGrid", "OTPVerifySuccess")
                    saveUser()

                    val intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }
        viewModel.uiEvent.observe(this) {
            when (it) {
                is OTPViewModelEvent.Error -> {
                    Log.e("SendGrid", it.error)
                    Toast.makeText(
                        this@OTPActivity,
                        it.error,
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.btnVerifyOTP.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                    binding.btnVerifyOTP.text = "VERIFY"
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun saveUser(){
        sharedPreferences.edit().putBoolean("verified", true).apply()
    }

    private fun addTextChangeListener() {
        binding.apply {
            otpEditText1.addTextChangedListener(EditTextWatcher(otpEditText1))
            otpEditText2.addTextChangedListener(EditTextWatcher(otpEditText2))
            otpEditText3.addTextChangedListener(EditTextWatcher(otpEditText3))
            otpEditText4.addTextChangedListener(EditTextWatcher(otpEditText4))
            otpEditText5.addTextChangedListener(EditTextWatcher(otpEditText5))
            otpEditText6.addTextChangedListener(EditTextWatcher(otpEditText6))
        }
    }

    inner class EditTextWatcher(
        private val currentView: View,
    ) : TextWatcher {

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {
            val text = p0.toString()
            // Ensure the user can only enter one digit
            if (text.length > 1) {
                (currentView as EditText).setText(text.substring(0, 1)) // Keep only the first character
                (currentView as EditText).setSelection(1) // Move the cursor after the first character
                return
            }
            when (currentView.id) {
                R.id.otpEditText1 -> if (text.length == 1) binding.otpEditText2.requestFocus()
                R.id.otpEditText2 -> if (text.length == 1) binding.otpEditText3.requestFocus() else if (text.isEmpty()) binding.otpEditText1.requestFocus()
                R.id.otpEditText3 -> if (text.length == 1) binding.otpEditText4.requestFocus() else if (text.isEmpty()) binding.otpEditText2.requestFocus()
                R.id.otpEditText4 -> if (text.length == 1) binding.otpEditText5.requestFocus() else if (text.isEmpty()) binding.otpEditText3.requestFocus()
                R.id.otpEditText5 -> if (text.length == 1) binding.otpEditText6.requestFocus() else if (text.isEmpty()) binding.otpEditText4.requestFocus()
                R.id.otpEditText6 -> if (text.isEmpty()) binding.otpEditText5.requestFocus()
            }
        }
    }
}