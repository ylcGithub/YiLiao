package dyzn.csxc.yiliao.lib_common.util

import android.os.Environment
import dyzn.csxc.yiliao.lib_common.base.BaseApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

/**
 *@author YLC-D
 *说明:
 */
object CacheFileUtil {
    private const val APP_FILE_ROOT_PATH_ = "A-genesis-factory-cache"
    private const val LOCAL_PRINT_LOG_PATH_ = "创世小厂本地打印日志文件.txt"

    /**
     * 获取手机的存储根目录
     */
    private fun getRootPath(): String {
        //这个目录不用存储权限
        ///storage/emulated/0/Android/data/yourPackageName/files
        val file = BaseApplication.getAppContext().getExternalFilesDir(null)
        var path = file?.path
        if (StringUtils.isEmpty(path)) {
            // 获得根目录/data
            path = Environment.getDataDirectory().path
        }
        return path + File.separator + APP_FILE_ROOT_PATH_
    }

    /**
     * 错误日志的存储目录
     */
    private fun getLogPath(): String {
        return getRootPath() + File.separator + "Log"
    }

    /**
     * 打印日志输出的文件路径
     */
    private fun getLocalLogPath(): String {
        return getLogPath() + File.separator + LOCAL_PRINT_LOG_PATH_
    }

    /**
     * 判断一个目录是否存在，不存在就创建该目录
     */
    private fun createFolder(path: String): Boolean {
        val folder = File(path)
        return if (folder.exists()) true else folder.mkdirs()
    }

    /**
     * 保存打印日志到本地
     */
    fun saveLogToLocal(tag: String, text: String) {
        GlobalScope.launch(Dispatchers.IO) {
            if (createFolder(getLogPath())) {
                try {
                    val fw = FileWriter(getLocalLogPath(), true)
                    val bw = BufferedWriter(fw)
                    bw.write("$tag:$text\n")
                    bw.close()
                    fw.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 删除本地保存的打印日志
     */
    fun deleteLocalLog() {
        deleteFile(getLocalLogPath())
    }

    /**
     * 删除文件
     */
    fun deleteFile(filePath: String): Boolean {
        val file = File(filePath)
        return if (file.isFile && file.exists()) {
            file.delete()
        } else false
    }

    fun getStlCachePath(): String {
        return getRootPath() + File.separator + "Stl"
    }

    fun stlIsInSdCard(stlId: String): Boolean {
        val file = File(getStlCachePath() + File.separator + stlId + ".stl")
        return file.exists()
    }


    fun getFileInSdCardOfPath(fileName: String): String {
        return getStlCachePath() + File.separator + fileName
    }

}