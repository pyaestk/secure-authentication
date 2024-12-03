package com.project.secureauthentication.data.repository

import com.project.secureauthentication.data.datasource.AuthRemoteDatasource
import com.project.secureauthentication.data.util.toModels
import com.project.secureauthentication.domain.model.UserDetailModel

class AuthRepository(
    private val authRemoteDatasource: AuthRemoteDatasource
) {

    suspend fun registerAccount(
        username: String,
        password: String,
        email: String,
        phoneNumber: String
    ): Result<Unit>{

        val result = authRemoteDatasource.registerAccount(
            userName = username,
            email = email,
            password = password,
            phoneNumber = phoneNumber
        ).map {

        }
        return result
    }

    suspend fun loginAccount(
        email: String,
        password: String
    ): Result<Unit> {
        val result = authRemoteDatasource.loginAccount(
            email = email,
            password = password
        ).map {

        }

        return result
    }

    suspend fun getUserInfo(): Result<UserDetailModel>{
        return authRemoteDatasource.getUserInfo().map {
            it.toModels()
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return authRemoteDatasource.resetPasswordWithLink(email)
    }

    suspend fun checkEmailVerification(): Result<Boolean> {
        return authRemoteDatasource.checkEmailVerification()
    }

    suspend fun logoutAccount():Result<Unit>{
        return authRemoteDatasource.logoutAccount()
    }

    suspend fun sendEmailVerification(): Result<Unit> {
        return authRemoteDatasource.sendEmailVerification()
    }

    suspend fun reAuthenticateAccount(password: String): Result<Unit> {
        return authRemoteDatasource.reAuthenticateAccount(password)
    }

    suspend fun updatePassword(password: String): Result<Unit> {
        return authRemoteDatasource.updatePassword(password)
    }

}