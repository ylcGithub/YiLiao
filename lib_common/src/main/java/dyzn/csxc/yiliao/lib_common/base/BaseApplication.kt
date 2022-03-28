package dyzn.csxc.yiliao.lib_common.base

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.alibaba.android.arouter.launcher.ARouter
import com.jeremyliao.liveeventbus.LiveEventBus
import dyzn.csxc.yiliao.lib_common.BuildConfig
import com.orhanobut.hawk.Hawk

/**
 *@author YLC-D
 *说明:
 */
open class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Hawk.init(this).build()
        mAppContext = this
        if (BuildConfig.DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog()     // 打印日志
            ARouter.openDebug()   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this) // 尽可能早，推荐在Application中初始化
        MultiDex.install(this)
        LiveEventBus.config().enableLogger(true).setContext(this)
    }

    companion object {
        private var mAppContext: Context? = null

        /**
         * 全局获取系统上下文
         */
        fun getAppContext(): Context {
            return mAppContext!!
        }
    }

}