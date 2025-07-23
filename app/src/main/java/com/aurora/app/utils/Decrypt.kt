package com.aurora.app.utils

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Decrypt {

    fun decryptBookText(textBase64: String): String {
        if (textBase64.length < 20) return ""

        val encryptedBytes = Base64.decode(textBase64, Base64.DEFAULT)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val blockSize = 16 // AES block size

        val ivBytes = encryptedBytes.sliceArray(0 until blockSize)
        val cipherText = encryptedBytes.sliceArray(blockSize until encryptedBytes.size)

        // Key derivation as in your class!
        val keyString = "SANATAN_BOOK_RAM_JI"
        val keyBytes = MessageDigest.getInstance("SHA-256").digest(keyString.toByteArray(Charsets.UTF_8))

        val keySpec = SecretKeySpec(keyBytes, "AES")
        val ivSpec = IvParameterSpec(ivBytes)

        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        val plainBytes = cipher.doFinal(cipherText)
        return plainBytes.toString(Charsets.UTF_8)
    }

}