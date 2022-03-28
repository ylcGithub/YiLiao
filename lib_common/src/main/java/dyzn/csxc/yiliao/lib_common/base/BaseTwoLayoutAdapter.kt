package dyzn.csxc.yiliao.lib_common.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import dyzn.csxc.yiliao.lib_common.type.ItemType

/**
 *@author YLC-D
 *说明: 带有两个布局的 RecyclerView 的通用适配器
 */
abstract class BaseTwoLayoutAdapter<E, VB_ONE : ViewDataBinding, VB_TWO : ViewDataBinding>(
    @LayoutRes val layoutIdOne: Int,
    @LayoutRes val layoutIdTwo: Int
) : BaseOneLayoutAdapter<E,VB_ONE>(layoutIdOne){

    override fun getItemViewType(position: Int): Int {
        return when (getItemType(position)) {
            ItemType.ONE_TYPE -> {
                1
            }
            ItemType.TWO_TYPE -> {
                2
            }
        }
    }

    /**
     * 自定义实现item的类型分配
     *
     * @param pos item 下标
     * @return item 的类型
     */
    protected abstract fun getItemType(pos: Int): ItemType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == 2){
            val vbTwo = DataBindingUtil.inflate<VB_TWO>(
                LayoutInflater.from(parent.context),
                layoutIdTwo,
                parent,
                false
            )
            BaseTwoBindingViewHolder(vbTwo.root)
        }else{
            super.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is BaseTwoBindingViewHolder){
            val binding = DataBindingUtil.getBinding<VB_TWO>(holder.itemView)
            binding?.let {
                onBindTwoTypeItem(it,dataList[position],holder)
                it.executePendingBindings()
            }
        }else{
            super.onBindViewHolder(holder, position)
        }
    }

    protected abstract fun onBindTwoTypeItem(binding: VB_TWO, item: E, holder: RecyclerView.ViewHolder)
}