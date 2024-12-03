package com.project.secureauthentication.data.datasource

import android.widget.Toast
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.project.secureauthentication.data.model.UserDetailResponse
import kotlinx.coroutines.tasks.await

class AuthRemoteDatasource(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) {
    
    suspend fun registerAccount(
        userName: String,
        password: String,
        email: String,
        phoneNumber: String
    ): Result<String> {
        return try {

            val authResult = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()
            val user = authResult.user
            
            if (user != null) {
                val userInfo = UserDetailResponse(
                    userId = user.uid,
                    email = email,
                    phoneNumber = phoneNumber,
                    name = userName,
                    password = password
                )
                firestore.collection("users")
                    .document(user.uid)
                    .set(userInfo).await()

                Result.success(user.uid)
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun reAuthenticateAccount(password: String): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val credential: AuthCredential = EmailAuthProvider.getCredential(user.email!!, password)
                user.reauthenticate(credential).await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePassword(password: String): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                user.updatePassword(password).await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Password Reset Failed: ${e.message}"))
        }
    }



    suspend fun loginAccount(email: String, password: String): Result<String> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                Result.success(user.uid)
            } else {
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserInfo(): Result<UserDetailResponse> {
        return try {
            val uid = firebaseAuth.currentUser!!.uid
            val documentSnapshot = firestore.collection("users").document(uid).get().await()
            val userResponse = documentSnapshot.toObject<UserDetailResponse>()

            if (userResponse != null) {
                Result.success(userResponse)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPasswordWithLink(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkEmailVerification(): Result<Boolean> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val verify = user.isEmailVerified
                user.reload().await()
                if (verify) {
                    Result.success(true)
                } else {
                    Result.failure(Exception("Email is not verified"))
                }
            } else {
                Result.failure(Exception("User is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendEmailVerification(): Result<Unit> {
        return try {
            firebaseAuth.currentUser?.let { user ->
                user.sendEmailVerification().await()
                Result.success(Unit)
            } ?: Result.failure(Exception("No user is currently signed in"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logoutAccount():Result<Unit> {
        return try {
            val fAuth = FirebaseAuth.getInstance()
            fAuth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}