package com.project.secureauthentication.ui.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.secureauthentication.R
import com.project.secureauthentication.databinding.ActivityAuthOptionBinding
import com.project.secureauthentication.ui.viewmodel.AuthOptionUIEvent
import com.project.secureauthentication.ui.viewmodel.AuthOptionUiState
import com.project.secureauthentication.ui.viewmodel.AuthOptionViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthOptionActivity : AppCompatActivity() {

    private var _binding: ActivityAuthOptionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthOptionViewModel by viewModel()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onResume() {
        super.onResume()
        viewModel.checkUserRegistration()
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityAuthOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        sharedPreferences = getSharedPreferences("UserRole", MODE_PRIVATE)

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        viewModel.uiState.observe(this) {
            when(it){
                AuthOptionUiState.Loading -> {
                    Toast.makeText(this, "LOADING..", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.uiEvent.observe(this) {
            when(it){
                is AuthOptionUIEvent.Error -> {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
                AuthOptionUIEvent.NavigateToMainScreen -> {
                    val cachedRole = sharedPreferences.getBoolean("verified", false)
                    if (cachedRole) {
                        startActivity(Intent(this@AuthOptionActivity, HomeActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}