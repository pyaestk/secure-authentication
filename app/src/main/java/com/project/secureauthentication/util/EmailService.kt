package com.project.secureauthentication.util

import android.util.Log
import com.project.secureauthentication.BuildConfig
import okhttp3.OkHttpClient
import uk.co.jakebreen.sendgridandroid.SendGrid
import uk.co.jakebreen.sendgridandroid.SendGridMail
import uk.co.jakebreen.sendgridandroid.SendTask
import okhttp3.Request
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


class EmailService {

    companion object {
        private const val FROM_EMAIL = BuildConfig.FROM_EMAIL
        private const val FROM_NAME = "SecureAuthentication"
        private const val API_KEY = BuildConfig.SENDGRID_API_KEY

    }

    suspend fun sendOTP(
        email: String,
        onSuccessListener: (String) -> Unit,
        onFailureListener: (Exception) -> Unit
    ){
        val otp = generateOTP()
        val sendGrid = SendGrid.create(API_KEY)
        val mail = SendGridMail().apply {
            addRecipient(email, email)
            setFrom(FROM_EMAIL, FROM_NAME)
            setSubject("Your OTP Code")
            setContent("""
                            Hello,
                            Here is your one-time password (OTP) for secure access to your account:
                            OTP: $otp

                            Best regards,
                            Secure Auth Admin Team
                        """.trimIndent()
            )
        }

        val task = SendTask(sendGrid)
        val response = task.send(mail)
        if (response.isSuccessful) {
            onSuccessListener(otp)
        } else {
            onFailureListener(Exception("Failed to sent OTP"))
        }
    }

    suspend fun sendAlert(
        email: String,
    ) {

        // Create SendGrid instance
        val sendGrid = SendGrid.create(API_KEY)

        // Create email content with details
        val emailContent = """
            |Hi there,
            |
            |We noticed multiple failed login attempts to your account on: ${getCurrentDateTime()}. 
            |If this wasn't you, please consider changing your password and ensure the security of your account.
            |
            |Best regards,
            |Secure Auth Admin Team
        """.trimMargin()

        // Construct the SendGrid email
        val mail = SendGridMail().apply {
            addRecipient(email, email)
            setFrom(FROM_EMAIL, FROM_NAME)
            setSubject("Failed Login Attempt")
            setContent(emailContent)
        }

        // Send the email
        val task = SendTask(sendGrid)
        val response = task.send(mail)
        if (response.isSuccessful) {
            Log.i("SendAlert", "Alert email has been sent")
        } else {
            Log.i("SendAlert", "Alert email failure")
        }
    }


    private fun generateOTP(): String {
        return (100000..999999).random().toString() 
    }

    private fun getCurrentDateTime(): String {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss a", Locale.getDefault())
        Log.i("Time", calendar.time.toString())
        return formatter.format(calendar.time)
    }
}