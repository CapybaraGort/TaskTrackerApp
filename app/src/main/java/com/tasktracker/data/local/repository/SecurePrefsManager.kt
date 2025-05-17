package com.tasktracker.data.local.repository

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import androidx.core.content.edit
import com.tasktracker.data.remote.auth.KeyToken
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

class SecurePrefsManager @Inject constructor(
    private val context: Context
) {

    companion object {
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "secure_key"
        private const val PREFS_NAME = "secure_prefs"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val TAG_IV = "_iv"
        private const val TAG_DATA = "_data"
    }

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    init {
        generateKeyIfNecessary()
    }

    private fun generateKeyIfNecessary() {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
            val keyGenSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
            keyGenerator.init(keyGenSpec)
            keyGenerator.generateKey()
        }
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }
        return (keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
    }

    private fun encrypt(value: String): Pair<String, String> {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(iv, Base64.DEFAULT) to Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    private fun decrypt(iv: String, encrypted: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(128, Base64.decode(iv, Base64.DEFAULT))
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)
        val decryptedBytes = cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT))
        return String(decryptedBytes, Charsets.UTF_8)
    }

    fun putString(keyToken: KeyToken, value: String) {
        val (iv, encrypted) = encrypt(value)
        prefs.edit {
                putString(keyToken.value + TAG_IV, iv)
                    .putString(keyToken.value + TAG_DATA, encrypted)
        }
    }

    fun getString(keyToken: KeyToken): String? {
        val iv = prefs.getString(keyToken.value + TAG_IV, null)
        val encrypted = prefs.getString(keyToken.value + TAG_DATA, null)
        return if (iv != null && encrypted != null) {
            try {
                decrypt(iv, encrypted)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    fun isTokenValid(keyToken: KeyToken): Boolean {
        val token = getString(keyToken) ?: return false

        return token.isNotEmpty()
    }

    fun remove(keyToken: KeyToken) {
        prefs.edit {
            remove(keyToken.value + TAG_IV)
                .remove(keyToken.value + TAG_DATA)
        }
    }

    fun clearAll() {
        prefs.edit { clear() }
    }
}
