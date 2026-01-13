package com.practice.socket.service.user

import com.practice.socket.domain.entity.CustomOAuth2User
import com.practice.socket.domain.entity.type.OAuth2UserType
import com.practice.socket.repository.user.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository,
): DefaultOAuth2UserService() {
    private var log: Logger = LoggerFactory.getLogger(CustomOAuth2UserService::class.java)

    override fun loadUser(userRequest: OAuth2UserRequest?): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)
        log.info("Attributes: {}", oAuth2User.attributes.toString())
        val oAuth2UserType = userRequest?.clientRegistration?.let { OAuth2UserType.findByRegistrationId(it.registrationId) }

        val attribute = oAuth2UserType?.extractAttributes(oAuth2User.attributes)
        val email = attribute?.get("email") as String

        val user = userRepository.findByEmail(email)
            .orElseGet { oAuth2UserType?.let { userRepository.save(it.generateUser(attribute)) } }

        return CustomOAuth2User.of(user, attribute)
    }
}
//kakao
//Attributes: {id=4693631336, connected_at=2026-01-12T14:28:57Z, properties={nickname=지현, profile_image=http://k.kakaocdn.net/dn/bxU8CH/dJMcah35etn/PhpOTHNXS4DmQ8SnDtGKIK/img_640x640.jpg, thumbnail_image=http://k.kakaocdn.net/dn/bxU8CH/dJMcah35etn/PhpOTHNXS4DmQ8SnDtGKIK/img_110x110.jpg}, kakao_account={profile_nickname_needs_agreement=false, profile_image_needs_agreement=false, profile={nickname=지현, thumbnail_image_url=http://k.kakaocdn.net/dn/bxU8CH/dJMcah35etn/PhpOTHNXS4DmQ8SnDtGKIK/img_110x110.jpg, profile_image_url=http://k.kakaocdn.net/dn/bxU8CH/dJMcah35etn/PhpOTHNXS4DmQ8SnDtGKIK/img_640x640.jpg, is_default_image=false, is_default_nickname=false}, name_needs_agreement=false, name=김지현, has_email=true, email_needs_agreement=false, is_email_valid=true, is_email_verified=true, email=kenj_h@naver.com, has_birthyear=true, birthyear_needs_agreement=false, birthyear=1992, has_birthday=true, birthday_needs_agreement=false, birthday=0316, birthday_type=SOLAR, is_leap_month=false, has_gender=true, gender_needs_agreement=false, gender=male}}
//naver
//Attributes: {resultcode=00, message=success, response={id=tA-Up7TWzXN1feLZwPz8eKidK8h3HyX1G6mDVPO7K9M, nickname=김지현, profile_image=https://ssl.pstatic.net/static/pwe/address/img_profile.png, age=30-39, gender=M, email=kenj_h@naver.com, name=김지현, birthday=03-16, birthyear=1992}}
//google
//Attributes: {sub=111745856976255844324, name=jihyeon, given_name=jihyeon, picture=https://lh3.googleusercontent.com/a/ACg8ocL7Itbfm9T7qGWyEdBMQ3oj8PHX5muLd2aPZYPeDpPlFXKZ2Q=s96-c, email=gus120332@gmail.com, email_verified=true}
