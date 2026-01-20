package com.practice.api.domain.presentation.request.user

import org.hibernate.validator.constraints.Length

data class UserProfileUpdateRequestDto (
    @Length(max = 50, message = "닉네임은 50자 이하로 설정해주세요.")
    val nickname: String? = null,
    val profileImageUrl: String? = null,
)