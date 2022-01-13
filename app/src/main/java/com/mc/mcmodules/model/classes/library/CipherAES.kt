package com.mc.mcmodules.model.classes.library

import com.mc.mcmodules.model.classes.data.ObjectAES
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class CipherAES {


    private var key: SecretKey


    init {


        val keygen = KeyGenerator.getInstance("AES")
        keygen.init(256)
        key = keygen.generateKey()


    }


    fun encrypt(textToEncrypt: String): ObjectAES {
        val plaintext: ByteArray = textToEncrypt.encodeToByteArray()
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val ciphertext: ByteArray = cipher.doFinal(plaintext)
        val iv: ByteArray = cipher.iv

        return ObjectAES(ciphertext, iv)
    }

    fun decrypt(textToDecrypt: ByteArray, iv: ByteArray): String {

        val ivP = IvParameterSpec(iv)
        val keySpec = SecretKeySpec(key.encoded, "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivP)
        val output = cipher.doFinal(textToDecrypt)
        return String(output)

    }


}