package dyzn.csxc.yiliao.lib_common.util

import okhttp3.internal.and
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * @author YLC-D
 * 说明:MD5 工具类
 */
object Md5Util {
    /**
     * 32位MD5加密
     * @param content -- 待加密内容
     * @return 加密字符串
     */
    fun decodeToBit32(content: String): String {
        val hash: ByteArray = try {
            MessageDigest.getInstance("MD5").digest(content.toByteArray(StandardCharsets.UTF_8))
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("NoSuchAlgorithmException", e)
        }
        //对生成的16字节数组进行补零操作
        val hex = StringBuilder(hash.size * 2)
        for (b in hash) {
            if (b and 0xFF < 0x10) {
                hex.append("0")
            }
            hex.append(Integer.toHexString(b and 0xFF))
        }
        return hex.toString()
    }
}