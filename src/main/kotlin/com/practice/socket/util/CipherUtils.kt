package com.practice.socket.util

import com.practice.socket.config.properties.CipherProperties
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest

object CipherUtils {
    fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(aes128Key(), "AES"))

        val encrypted = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))
        return encrypted.joinToString("") { "%02X".format(it) }
    }

    fun decrypt(encryptedHex: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(aes128Key(), "AES"))

        val decrypted = cipher.doFinal(hexToBytes(encryptedHex))
        return String(decrypted, StandardCharsets.UTF_8)
    }

    private fun aes128Key(): ByteArray {
        val sha512 = MessageDigest.getInstance("SHA-512")
            .digest(CipherProperties.seed().toByteArray(StandardCharsets.UTF_8))

        val foldedKey = ByteArray(16)
        sha512.forEachIndexed{ i, b -> foldedKey[i % 16] = (foldedKey[i % 16].toInt() xor b.toInt()).toByte()}
        return foldedKey
    }

    private fun hexToBytes(hex: String): ByteArray =
        hex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}