package com.project.secureauthentication.ui.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.project.secureauthentication.R
import com.project.secureauthentication.databinding.ActivityHomeBinding
import com.project.secureauthentication.ui.viewmodel.HomeUIEvent
import com.project.secureauthentication.ui.viewmodel.HomeUiState
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.project.secureauthentication.ui.viewmodel.HomeViewModel
import com.project.secureauthentication.ui.viewmodel.NewPasswordFormEvent

class HomeActivity : AppCompatActivity() {
    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    private val viewModel: HomeViewModel by viewModel()

    private lateinit var dialog: AlertDialog

    lateinit var currentPw: EditText
    lateinit var inputLayoutPw: TextInputLayout
    lateinit var newPw: EditText
    lateinit var inputLayoutNewPw: TextInputLayout
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        sharedPreferences = getSharedPreferences("UserRole", MODE_PRIVATE)

        binding.btnSignout.setOnClickListener {
            viewModel.logout()
        }

        observerViewModel()

        binding.btnResetPw.setOnClickListener {
            val inflater = LayoutInflater.from(this)
            val dialogView = inflater.inflate(R.layout.dialog_password_reset, null)
            inputLayoutPw = dialogView.findViewById(R.id.input_password)
            currentPw = dialogView.findViewById<EditText>(R.id.edt_current_password)
            inputLayoutNewPw = dialogView.findViewById(R.id.input_new_password)
            newPw = dialogView.findViewById(R.id.edt_new_password)


            dialog = MaterialAlertDialogBuilder(this,
                R.style.ThemeOverlay_App_MaterialAlertDialog)
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("RESET", null)
                .setView(dialogView)
                .show()

            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                viewModel.onEvent(NewPasswordFormEvent.Submit)
            }

            observeTextChangeListener()

            newPw.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    if (newPw.text.toString().isEmpty()){
                        inputLayoutNewPw.helperText =
                            getString(R.string.nine_characters_minimum_and_remaining)
                    }
                } else {
                    inputLayoutNewPw.helperText = null
                }
            }
            viewModel.formState.observe(this){
                inputLayoutNewPw.error = it.newPasswordError
            }
        }
    }

    private fun observeTextChangeListener() {
        currentPw.addTextChangedListener {
            viewModel.onEvent(NewPasswordFormEvent.passwordChange(it.toString()))
        }
        newPw.addTextChangedListener {
            if (newPw.text.toString().isEmpty()){
                inputLayoutNewPw.helperText =
                    getString(R.string.nine_characters_minimum_and_remaining)
            }
            viewModel.onEvent(NewPasswordFormEvent.newPasswordChange(it.toString()))
            viewModel.clearPasswordError()
            inputLayoutNewPw.isErrorEnabled = false
        }
    }

    private fun observerViewModel() {

        viewModel.uiEvent.observe(this){
            when(it){
                is HomeUIEvent.Error -> {

                }
                HomeUIEvent.NavigateToAuthOptionScreen -> {
                    MaterialAlertDialogBuilder(this,
                        R.style.ThemeOverlay_App_MaterialAlertDialog)
                        .setTitle("Do you want to Sign out?")
                        .setNegativeButton("NO") { dialog, which ->
                            dialog.dismiss()
                        }
                        .setPositiveButton("YES") { dialog, which ->
                            val intent = Intent(this, AuthOptionActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            sharedPreferences.edit().clear().apply()
                            startActivity(intent)
                        }
                        .show()

                }
            }
        }

        viewModel.uiState.observe(this){
            when(it){
                HomeUiState.LOADING -> {
                    binding.apply {
                        progressBar.visibility = View.VISIBLE
                        layoutCardView.visibility = View.INVISIBLE
                    }
                }
                is HomeUiState.SUCCESS -> {
                    binding.apply {
                        progressBar.visibility = View.GONE
                        layoutCardView.visibility = View.VISIBLE
                        tvEnduserName.text = it.userDetailModel.name
                        tvEnduserEmail.text = it.userDetailModel.email
                        tvPhoneNumber.text = it.userDetailModel.phoneNumber
                    }
                }

                HomeUiState.PwResetSuccess -> {
                    Toast.makeText(this,
                        "Password has been reset",
                        Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }

                HomeUiState.ReAuthenticateError -> {
                    inputLayoutPw.error = "Wrong Password"
                }

                HomeUiState.ReAuthenticateSuccess -> {
                    viewModel.updatePassword(password = newPw.text.toString())
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}