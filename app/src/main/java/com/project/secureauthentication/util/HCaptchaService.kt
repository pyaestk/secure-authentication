package com.project.secureauthentication.util

import android.app.Activity
import android.content.Context
import com.hcaptcha.sdk.HCaptcha

class HCaptchaService {

    private var hCaptchaClient: HCaptcha? = null

    fun verifyHCaptcha(
        activity: Activity,
        addOnSuccessListener: (String) -> Unit,
        addOnFailureListener: (String) -> Unit
    ) {
        hCaptchaClient = HCaptcha.getClient(activity)

        hCaptchaClient?.verifyWithHCaptcha()?.apply {
            addOnSuccessListener { response ->
                addOnSuccessListener(response.tokenResult)
            }
            addOnFailureListener {
                addOnFailureListener(it.message.toString())
            }
        }
    }
}