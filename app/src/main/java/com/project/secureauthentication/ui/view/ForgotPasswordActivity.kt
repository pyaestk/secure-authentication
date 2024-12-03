package com.project.secureauthentication.ui.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.hcaptcha.sdk.HCaptcha
import com.project.secureauthentication.R
import com.project.secureauthentication.databinding.ActivityAuthOptionBinding
import com.project.secureauthentication.databinding.ActivityForgotPasswordBinding
import com.project.secureauthentication.ui.viewmodel.EmailFormEvent
import com.project.secureauthentication.ui.viewmodel.ForgotPasswordViewModel
import com.project.secureauthentication.ui.viewmodel.ForgotPwUiState
import com.project.secureauthentication.ui.viewmodel.ForgotPwViewModelEvent
import com.project.suitcase.ui.viewmodel.LoginFormEvent
import com.project.suitcase.ui.viewmodel.LoginUiState
import com.project.suitcase.ui.viewmodel.LoginViewModel
import com.project.suitcase.ui.viewmodel.LoginViewModelEvent
import org.koin.androidx.viewmodel.ext.android.viewModel

class ForgotPasswordActivity : AppCompatActivity() {
    private var _binding: ActivityForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ForgotPasswordViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnSendEmail.setOnClickListener {
            viewModel.onEvent(this, emailFormEvent = EmailFormEvent.Submit)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        observeViewModel()
        setUpTextChangeEventListener()
    }

    private fun observeViewModel() {
        viewModel.emailFormState.observe(this) {
            binding.textInputLayoutEmail.error = it.emailError
            binding.btnSendEmail.isEnabled = it.email.isNotEmpty()
        }

        viewModel.uiState.observe(this) {
            when(it) {
                ForgotPwUiState.Loading -> {
                    Toast.makeText(this, "LOADING", Toast.LENGTH_SHORT).show()
                    binding.btnSendEmail.isEnabled = false
                }
            }
        }

        viewModel.uiEvent.observe(this) { event ->
            when(event){
                is ForgotPwViewModelEvent.Success -> {
                    Toast.makeText(this,
                        "Password reset link has been sent to your email address",
                        Toast.LENGTH_LONG).show()
                    binding.btnSendEmail.isEnabled = false
                }
                is ForgotPwViewModelEvent.Error -> {
                    Toast.makeText(this,
                        event.error, Toast.LENGTH_LONG).show()
                    binding.btnSendEmail.isEnabled = true
                }
            }
        }
    }

    private fun setUpTextChangeEventListener() {
        binding.apply {
            edtEmail.addTextChangedListener {
                viewModel.onEvent(this@ForgotPasswordActivity,
                    EmailFormEvent.EmailChanged(it.toString()))
                viewModel.clearEmailError()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}