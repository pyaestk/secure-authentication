package com.project.secureauthentication.data.util

import com.project.secureauthentication.data.model.UserDetailResponse
import com.project.secureauthentication.domain.model.UserDetailModel


fun UserDetailResponse.toModels(): UserDetailModel = UserDetailModel(
    phoneNumber = this.phoneNumber.orEmpty(),
    name = this.name.orEmpty(),
    email = this.email.orEmpty(),
    password = this.password.orEmpty()
)