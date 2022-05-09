package dyzn.csxc.yiliao.lib_common.expand

import okhttp3.internal.and
private val hexArray = "0123456789ABCDEF".toCharArray()

fun String.decodeByteArray(): ByteArray {
    check(length % 2 == 0) { "Must have an even length" }
    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}

fun ByteArray.decodeHexString():String{
    if(isEmpty()) return ""
    val hexChars = CharArray(size * 2)
    for (j in indices) {
        val v:Int = get(j) and 0xFF
        hexChars[j * 2] = hexArray[v ushr 4]
        hexChars[j * 2 + 1] = hexArray[v and 0x0F]
    }
    return String(hexChars)
}