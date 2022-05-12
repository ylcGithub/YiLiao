package dyzn.csxc.yiliao.lib_common.expand

import okhttp3.internal.and
import java.io.ByteArrayOutputStream


private val hexArray = "0123456789abcdef".toCharArray()

//将string字符串转换成16进制字符串
fun String.ylEncodeToHexString():String{
    //根据默认编码获取字节数组
    val bytes: ByteArray = toByteArray()
    //存储16进制字符串
    val sb = StringBuilder(bytes.size * 2)
    //将字节数组中每个字节拆解成2位16进制整数
    for (i in bytes.indices) {
        sb.append(hexArray[bytes[i] and 0xf0 shr 4])
        sb.append(hexArray[bytes[i] and 0x0f])
    }
    return sb.toString()
}

//将字节数组转换成16进制的字符串
fun ByteArray.ylDecodeHexString():String{
    if(isEmpty()) return ""
    val sf = StringBuffer()
    for (j in indices) {
        val c = Integer.toHexString(get(j) and 0xFF)
        sf.append(c)
    }
    return sf.toString()
}

//将字节数组转换成一个对齐的（不够两位补0）16进制字符串
fun ByteArray.ylDecodeAlignHexString():String{
    if(isEmpty()) return ""
    val sf = StringBuffer()
    for (j in indices) {
        val c = Integer.toHexString(get(j) and 0xFF)
        if(c.length < 2){
            sf.append(0)
        }
        sf.append(c)
    }
    return sf.toString()
}


//将16进制的字符串转换成普通字符串
fun String.ylDecodeHexStringToString(): String {
    val charArray = toCharArray()
    val bas = ByteArrayOutputStream(length/2)
    //将每2位16进制整数组装成一个字节
    var i = 0
    while (i < length) {
        bas.write(hexArray.indexOf(charArray[i]) shl 4 or hexArray.indexOf(charArray[i + 1]))
        i += 2
    }
    return String(bas.toByteArray())
}

//将字节数组转换成16进制字符串，再将16进制字符串转换成普通字符串
fun ByteArray.ylDecodeBteToString():String{
    val hexString = ylDecodeHexString()
    return hexString.ylDecodeHexStringToString()
}