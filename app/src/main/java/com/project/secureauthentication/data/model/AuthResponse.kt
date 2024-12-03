package com.project.secureauthentication.data.model


data class UserDetailResponse(
    val userId: String? = null,
    val name: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val password: String? = null
) {
    constructor() : this(
        null,
        null,
        null,
        null,
        null
    )
}