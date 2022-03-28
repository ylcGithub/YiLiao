package dyzn.csxc.yiliao.lib_common.base

import androidx.room.*

@Dao
interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: MutableList<T>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: T)

    @Delete
    fun delete(element: T)

    @Delete
    fun deleteList(elements:MutableList<T>)

    @Delete
    fun deleteSome(vararg elements:T)

    @Update
    fun update(element: T)

}
