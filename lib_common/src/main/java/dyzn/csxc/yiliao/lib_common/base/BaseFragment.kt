package dyzn.csxc.yiliao.lib_common.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.alibaba.android.arouter.launcher.ARouter
import dyzn.csxc.yiliao.lib_common.R
import dyzn.csxc.yiliao.lib_common.expand.toast
import dyzn.csxc.yiliao.lib_common.widget.LoadingDialog

/**
 *@author YLC-D
 *说明:
 */
abstract class BaseFragment<VM : BaseViewModel, B : ViewDataBinding> : Fragment(), ILoad, IView {
    //上下文
    protected lateinit var mContext: AppCompatActivity
    protected lateinit var mBinding: B
    lateinit var mViewModel: VM
    private var noInitData: Boolean = true

    private var mNavOptions: NavOptions? = null

    private val dialog: LoadingDialog by lazy {
        LoadingDialog.create(mContext)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = activity as AppCompatActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        mBinding.lifecycleOwner = this
        mViewModel = getViewModel()
        lifecycle.addObserver(mViewModel)
        noInitData = true
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        if (noInitData) {
            init()
            initData()
            noInitData = false
        }
    }

    /**
     * 预置的一些初始化操作
     */
    private fun init() {
        mViewModel.getLoadingState().observe(this, {
            if (it) dialog.show(mViewModel.loadingText.value)
            else dialog.dismiss()
        })
        mViewModel.loadingText.observe(this,{
             dialog.textChange(it)
        })
        mViewModel.errorMsg.observe(this,{
            it.toast()
        })
        mViewModel.pageBack.observe(this,{
            pageBack()
        })
    }

    abstract fun getViewModel(): VM

    private lateinit var mFragmentProvider: ViewModelProvider
    private lateinit var mActivityProvider: ViewModelProvider

    protected open fun getFragmentViewModelProvider(fragment: Fragment): ViewModelProvider {
        if (!this::mFragmentProvider.isInitialized) mFragmentProvider = ViewModelProvider(fragment)
        return mFragmentProvider
    }

    protected open fun getActivityViewModelProvider(activity: AppCompatActivity): ViewModelProvider {
        if (!this::mActivityProvider.isInitialized) mActivityProvider = ViewModelProvider(activity)
        return mActivityProvider
    }


    override fun showLoading(message: String) {
        mViewModel.loadingText.value = message
        mViewModel.setLoadingState(true)
    }

    override fun dismissLoading(){
        mViewModel.setLoadingState(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog.dismiss()
        lifecycle.removeObserver(mViewModel)
    }

    /**
     * 跳转到下一个页面
     * @param resId id
     * @param args args 要传递的参数
     * @param navOptions options 页面的跳转动画效果
     */
    protected open fun navigate(
        @IdRes resId: Int,
        args: Bundle? = null,
        navOptions: NavOptions? = getNavOptions()
    ) {
        NavHostFragment.findNavController(this).navigate(resId, args, navOptions)
    }

    private fun getNavOptions(): NavOptions? {
        if (mNavOptions == null) {
            mNavOptions = NavOptions.Builder()
                .setEnterAnim(R.anim.base_page_right_in)
                .setExitAnim(R.anim.base_page_left_out)
                .setPopEnterAnim(R.anim.base_page_left_in)
                .setPopExitAnim(R.anim.base_page_right_out)
                .build()
        }
        return mNavOptions
    }

    fun pageBack() {
        NavHostFragment.findNavController(this).navigateUp()
    }

    protected open fun toNextActivity(path:String,bundle: Bundle? = null){
        ARouter.getInstance().build(path).with(bundle).navigation()
    }
}