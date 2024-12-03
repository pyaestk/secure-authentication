package com.project.secureauthentication.ui.view

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.project.secureauthentication.R
import com.project.secureauthentication.databinding.ActivityRegisterBinding
import com.project.secureauthentication.util.StrengthLevel
import com.project.suitcase.ui.viewmodel.RegisterUiState
import com.project.suitcase.ui.viewmodel.RegisterViewModel
import com.project.suitcase.ui.viewmodel.RegisterViewModelEvent
import com.project.suitcase.ui.viewmodel.RegistrationFormEvent
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : AppCompatActivity() {

    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.isEnabled = false

        binding.btnRegister.setOnClickListener{
            viewModel.onEvent(this@RegisterActivity, RegistrationFormEvent.Submit)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
        observeViewModel()
        setUpTextListenerEvent()
        
    }

    private fun observeViewModel() {
        //for form validation
        viewModel.formState.observe(this) {
            binding.apply {
                registerBtnVisibility(
                    it.name.isNotEmpty() and it.email.isNotEmpty()
                )
                textInputLayoutEmail.error = it.emailError
                textInputLayoutPassword.error = it.passwordError
                textInputLayoutPasswordRepeat.error = it.repeatedPasswordError
                updatePasswordStrength(it.passwordStrength)
            }
        }
        //for registration
        viewModel.uiState.observe(this){ state ->
            when(state){
                RegisterUiState.Loading -> {
                    Toast.makeText(this, "LOADING", Toast.LENGTH_SHORT).show()
                    binding.btnRegister.isEnabled = false
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnRegister.text = null
                }
            }
        }

        viewModel.uiEvent.observe(this) { event ->
            when(event){
                is RegisterViewModelEvent.Error -> {
                    Toast.makeText(this, event.error, Toast.LENGTH_LONG).show()
                    binding.btnRegister.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.text = "REGISTER"
                }
                RegisterViewModelEvent.RegisterSuccess -> {
                    binding.btnRegister.isEnabled = false
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.text = "REGISTER"
                    Toast.makeText(this, "Verify your email address", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }


    private fun setUpTextListenerEvent() {
        binding.apply {
            edtUserName.addTextChangedListener {
                viewModel.onEvent(this@RegisterActivity,
                    RegistrationFormEvent.NameChanged(it.toString()))
            }
            edtEmail.addTextChangedListener {
                viewModel.onEvent(this@RegisterActivity,
                    RegistrationFormEvent.EmailChanged(it.toString()))
                viewModel.clearEmailError()
                textInputLayoutEmail.isErrorEnabled = false
            }
            edtPassword.addTextChangedListener {
                if (edtPassword.text.toString().isEmpty()){
                    textInputLayoutPassword.helperText =
                        getString(R.string.nine_characters_minimum_and_remaining)
                }
                viewModel.onEvent(this@RegisterActivity,
                    RegistrationFormEvent.PasswordChanged(it.toString()))
                viewModel.clearPasswordError()
                textInputLayoutPassword.isErrorEnabled = false

            }
            edtPasswordRepeat.addTextChangedListener {
                viewModel.onEvent(this@RegisterActivity,
                    RegistrationFormEvent.RepeatedPasswordChanged(it.toString()))
                viewModel.clearRepeatedPasswordError()
                textInputLayoutPasswordRepeat.isErrorEnabled = false
            }
            edtPhoneNum.addTextChangedListener{
                viewModel.onEvent(this@RegisterActivity,
                    RegistrationFormEvent.PhoneNumberChanged(it.toString()))
            }

            edtPassword.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    if (edtPassword.text.toString().isEmpty()){
                        textInputLayoutPassword.helperText =
                            getString(R.string.nine_characters_minimum_and_remaining)
                    }
                    pwStrengthLayout.visibility = View.VISIBLE
                } else {
                    textInputLayoutPassword.helperText = null
                    pwStrengthLayout.visibility = View.GONE
                }
            }
        }
    }

    private fun registerBtnVisibility(shouldShow: Boolean) {
        if (shouldShow) {
            binding.btnRegister.isEnabled = true
        } else {
            binding.btnRegister.isEnabled = false
        }
    }

    private fun updatePasswordStrength(strengthLevel: StrengthLevel) {
        binding.apply {
            binding.pbPwStrength.max = 80
            when (strengthLevel) {
                StrengthLevel.WEAK -> {
                    binding.pbPwStrength.setIndicatorColor(getColor(R.color.red))
                    binding.tvPwStrength.text = "Weak!"
                    ObjectAnimator.ofInt(binding.pbPwStrength, "progress", 20).start()
                }
                StrengthLevel.MEDIUM -> {
                    binding.pbPwStrength.setIndicatorColor(getColor(R.color.orange))
                    binding.tvPwStrength.text = "Medium!"
                    ObjectAnimator.ofInt(binding.pbPwStrength, "progress", 40).start()
                }
                StrengthLevel.STRONG -> {
                    binding.pbPwStrength.setIndicatorColor(getColor(R.color.green))
                    binding.tvPwStrength.text = "Strong!"
                    ObjectAnimator.ofInt(binding.pbPwStrength, "progress", 60).start()
                }
                StrengthLevel.PERFECT -> {
                    binding.pbPwStrength.setIndicatorColor(getColor(R.color.blue))
                    binding.tvPwStrength.text = "Perfect!"
                    ObjectAnimator.ofInt(binding.pbPwStrength, "progress", 80).start()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    
}