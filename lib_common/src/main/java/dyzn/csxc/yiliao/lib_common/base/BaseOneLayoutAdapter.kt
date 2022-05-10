package dyzn.csxc.yiliao.lib_common.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 *@author YLC-D
 *说明: 数据源E 要实现 hashCode 和 equals 方法 以便数据的比较 （kotlin 中的data class 已经默认实现了这两个方法不用特意实现）
 */
abstract class BaseOneLayoutAdapter<E, VB : ViewDataBinding>(@LayoutRes val layoutId: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TAG1 = "params_update_tag_1"
        private const val TAG2 = "params_update_tag_2"
        private const val TAG3 = "params_update_tag_3"
        private const val TAG4 = "params_update_tag_4"
    }

    //数据源列表
    protected val dataList: ArrayList<E> = ArrayList()

    override fun getItemCount(): Int = dataList.size

    open fun updateList(list: List<E>?, isRefresh: Boolean = false) {
        val oldList = ArrayList(dataList)
        if (isRefresh) dataList.clear()
        list?.let { dataList.addAll(it) }
        val myDiffCallBack = MyDiffCallBack(oldList, dataList)
        val calculateDiff = DiffUtil.calculateDiff(myDiffCallBack, true)
        calculateDiff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vb = DataBindingUtil.inflate<VB>(
            LayoutInflater.from(parent.context), layoutId, parent, false
        )
        return BaseOneBindingViewHolder(vb.root)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = DataBindingUtil.getBinding<VB>(holder.itemView)
        binding?.let {
            val item = dataList[position]
            it.root.setOnClickListener { mItemListener?.invoke(position,item) }
            this.onBindOneTypeItem(it, item, holder)
            it.executePendingBindings()
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        }
        if (position >= dataList.size) {
            return
        }
        val t: E = dataList[position] ?: return
        val bind = DataBindingUtil.getBinding<VB>(holder.itemView)
        bind?.let {
            for (tag in payloads) {
                when (tag) {
                    TAG1 -> {
                        partialUpdate1(bind, t, holder)
                    }
                    TAG2 -> {
                        partialUpdate2(bind, t, holder)
                    }
                    TAG3 -> {
                        partialUpdate3(bind, t, holder)
                    }
                    TAG4 -> {
                        partialUpdate4(bind, t, holder)
                    }
                }
                it.executePendingBindings()
            }
        }
    }

    open fun partialUpdate1(binding: VB, item: E, holder: RecyclerView.ViewHolder) {}
    open fun partialUpdate2(binding: VB, item: E, holder: RecyclerView.ViewHolder) {}
    open fun partialUpdate3(binding: VB, item: E, holder: RecyclerView.ViewHolder) {}
    open fun partialUpdate4(binding: VB, item: E, holder: RecyclerView.ViewHolder) {}

    fun partialUpdateTag1(index: Int) {
        notifyItemChanged(index, TAG1)
    }

    fun partialUpdateTag2(index: Int) {
        notifyItemChanged(index, TAG2)
    }

    fun partialUpdateTag3(index: Int) {
        notifyItemChanged(index, TAG3)
    }

    fun partialUpdateTag4(index: Int) {
        notifyItemChanged(index, TAG4)
    }

    inner class MyDiffCallBack(private val oldList: List<E>, private val newList: List<E>) :
        DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            itemIsSame(
                oldList[oldItemPosition], newList[newItemPosition]
            )

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.hashCode() == newItem.hashCode() && oldItem?.equals(newItem) ?: false
        }
    }

    /**
     * 列表更改前后 判断item是否是同一个
     *
     * @param oldItem 旧列表的item
     * @param newItem 新列表的item
     * @return 是否相同
     */
    protected abstract fun itemIsSame(oldItem: E, newItem: E): Boolean

    /**
     * 数据绑定动态交给需要使用的地方去实现
     *
     * @param binding itemLayout 的 数据绑定回调
     * @param item    数据item
     * @param holder  每条数据对应的holder 主要用来后去该条数据的下标 holder.getAdapterPosition();
     */
    protected abstract fun onBindOneTypeItem(binding: VB, item: E, holder: RecyclerView.ViewHolder)

    fun getItem(position: Int): E? {
        return if (position < 0 || position > itemCount) {
            null
        } else {
            dataList[position]
        }
    }

    fun deleteItem(position: Int) {
        dataList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun deleteAllItem() {
        val count = dataList.size
        dataList.clear()
        notifyItemRangeRemoved(0, count)
    }

    private var mItemListener: ((index:Int,item: E) -> Unit)? = null
    fun setOnItemClickListener(lis: ((index:Int,e: E) -> Unit)) {
        mItemListener = lis
    }
}