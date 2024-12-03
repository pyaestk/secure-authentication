package com.project.secureauthentication.ui.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.hcaptcha.sdk.HCaptcha
import com.hcaptcha.sdk.HCaptchaTokenResponse
import com.hcaptcha.sdk.tasks.OnFailureListener
import com.hcaptcha.sdk.tasks.OnSuccessListener
import com.project.secureauthentication.R
import com.project.secureauthentication.databinding.ActivityLoginBinding
import com.project.suitcase.ui.viewmodel.LoginFormEvent
import com.project.suitcase.ui.viewmodel.LoginUiState
import com.project.suitcase.ui.viewmodel.LoginViewModel
import com.project.suitcase.ui.viewmodel.LoginViewModelEvent
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    private var timeLeftInMillis: Long = 30000
    private val handler = Handler(Looper.getMainLooper())

    private val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            viewModel.onEvent(this@LoginActivity, LoginFormEvent.Submit)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnForgotPw.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        observeViewModel()
        setUpTextChangeEventListener()
    }

    private fun observeViewModel() {
        viewModel.loginFormState.observe(this) {
            binding.textInputLayoutEmail.error = it.emailError
            binding.textInputLayoutPw.error = it.passwordError
        }

        viewModel.uiState.observe(this) {
            when(it) {
                LoginUiState.Loading -> {
                    binding.btnLogin.isEnabled = false
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.text = null
                }
            }
        }

        viewModel.uiEvent.observe(this) { event ->
            when(event) {
                is LoginViewModelEvent.Error -> {
                    //Toast.makeText(this@LoginActivity, event.error, Toast.LENGTH_SHORT).show()
                    binding.textInputLayoutEmail.error = "Login or password is invalid"
                    binding.textInputLayoutPw.error = "Login or password is invalid"

                    if (event.loginAttempt > 5) {
                        startTimer()
                    } else {
                        binding.btnLogin.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                        binding.btnLogin.text = "LOGIN"
                    }
                }
                LoginViewModelEvent.LoginSuccess -> {
                    binding.btnLogin.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.text = "LOGIN"
                    val intent = Intent(this@LoginActivity, OTPActivity::class.java)
                    intent.putExtra("email", binding.edtEmail.text.toString())
                    startActivity(intent)
                }
            }
        }
    }

    private fun setUpTextChangeEventListener() {
        binding.apply {
            edtEmail.addTextChangedListener {
                viewModel.onEvent(this@LoginActivity, LoginFormEvent.EmailChanged(it.toString()))
                viewModel.clearEmailError()
            }
            edtPassword.addTextChangedListener {
                viewModel.onEvent(this@LoginActivity, LoginFormEvent.PasswordChange(it.toString()))
                viewModel.clearPasswordError()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        handler.removeCallbacksAndMessages(null)
    }

    // For timer
    private fun startTimer() {
        // Reset the time
        timeLeftInMillis = 30000
        updateText()

        // Use handler to update the button text every second
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (timeLeftInMillis > 0) {
                    timeLeftInMillis -= 1000
                    updateText()
                    handler.postDelayed(this, 1000)
                } else {
                    resetTimer()
                }
            }
        }, 1000)
    }

    private fun resetTimer() {
        timeLeftInMillis = 30000
        updateText()
        binding.btnLogin.text = "LOGIN"
        binding.btnLogin.isEnabled = true
        binding.progressBar.visibility = View.GONE
        binding.btnLogin.setTextColor(getColor(R.color.white))
    }

    private fun updateText() {
        // Ensure binding is not null before using it
        if (_binding == null) return

        val remainingTime: Int = (timeLeftInMillis / 1000).toInt()
        binding.btnLogin.setTextColor(getColor(R.color.black))
        binding.progressBar.visibility = View.GONE
        binding.btnLogin.text = "Please login after ${String.format(Locale.getDefault(), "%02d", remainingTime)}s .."
    }
}
