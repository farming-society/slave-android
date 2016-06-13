package messages

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object CryptoUtil {

    fun encrypt(key: String, text: String): String {
        val cipher = getCipher(Cipher.ENCRYPT_MODE, key)
        val bytes = cipher.doFinal(text.toByteArray(charset("UTF-8")))
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    fun decrypt(key: String, base64Text: String): String {
        val cipher = getCipher(Cipher.DECRYPT_MODE, key)
        val bytes = Base64.decode(base64Text, Base64.DEFAULT)
        return String(cipher.doFinal(bytes), charset("UTF-8"))
    }

    private fun getCipher(cipherMode: Int, key: String): Cipher {
        val algorithm = "AES"
        val keySpec = SecretKeySpec(key.toByteArray(charset("UTF-8")), algorithm)
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(cipherMode, keySpec)
        return cipher
    }

}