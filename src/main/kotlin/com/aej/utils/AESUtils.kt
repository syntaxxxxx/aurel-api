package com.aej.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.apache.commons.codec.binary.Base64
import java.nio.ByteBuffer
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object AESUtils {
    private const val key = "utsman_gantenkkk" // 16 digit

    fun encrypt(value: String): String {
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(KotlinModule())
        val buf = ByteBuffer.wrap(objectMapper.writeValueAsString(value).toByteArray(Charsets.UTF_8))

        val keySpec = SecretKeySpec(key.toByteArray(charset("UTF-8")), "AES")

        val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)

        val encrypted = cipher.doFinal(buf.array())
        return Base64.encodeBase64String(encrypted)
    }

    fun decrypt(value: String): String {
        val keySpec = SecretKeySpec(key.toByteArray(charset("UTF-8")), "AES")

        val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
        cipher.init(Cipher.DECRYPT_MODE, keySpec)

        val decrypted = cipher.doFinal(Base64.decodeBase64(value))
        return String(decrypted)
    }
}