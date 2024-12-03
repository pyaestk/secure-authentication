package com.project.secureauthentication.domain.model

data class UserDetailModel(
    val name: String,
    val email: String,
    val phoneNumber: String,
    val password: String
)