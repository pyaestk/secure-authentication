package com.project.secureauthentication.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.secureauthentication.data.datasource.AuthRemoteDatasource
import com.project.secureauthentication.data.repository.AuthRepository
import com.project.secureauthentication.ui.viewmodel.AuthOptionViewModel
import com.project.secureauthentication.ui.viewmodel.ForgotPasswordViewModel
import com.project.secureauthentication.ui.viewmodel.HomeViewModel
import com.project.secureauthentication.ui.viewmodel.OTPViewModel
import com.project.secureauthentication.util.HCaptchaService
import com.project.secureauthentication.util.EmailService
import com.project.suitcase.ui.viewmodel.LoginViewModel
import com.project.suitcase.ui.viewmodel.RegisterViewModel
import com.project.suitcase.ui.viewmodel.util.ValidatorImpl
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authFeatureModule = module {
    viewModel {
        AuthOptionViewModel(get())
    }
    viewModel {
        LoginViewModel(
            authRepository = get(),
            validator = ValidatorImpl(
                get()
            ),
            hCaptchaService = HCaptchaService(),
            emailService = EmailService()
        )
    }
    viewModel {
        RegisterViewModel(
            authRepository = get(),
            validator = ValidatorImpl(
                get()
            ),
            hCaptchaService = HCaptchaService()
        )
    }
    viewModel{
        HomeViewModel(
            authRepository = get(),
            validator = ValidatorImpl(get())
        )
    }
    viewModel {
        ForgotPasswordViewModel(
            authRepository = get(),
            validator = ValidatorImpl(
                get()
            ),
            hCaptchaService = HCaptchaService()
        )
    }
    viewModel {
        OTPViewModel(
            EmailService()
        )
    }

    single {
        AuthRemoteDatasource(
            firebaseAuth = FirebaseAuth.getInstance(),
            firestore = FirebaseFirestore.getInstance(),
        )
    }
    single {
        AuthRepository(get())
    }
}