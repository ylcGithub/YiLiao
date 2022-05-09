package dyzn.csxc.yiliao.bluetooth.view.scan

import dyzn.csxc.yiliao.lib_common.base.BaseRepository
import kotlinx.coroutines.CoroutineScope

/**
 *@author YLC-D
 *@create on 2022/3/14 10
 *说明:
 */
class BluetoothScanRepository(private val viewModel: BluetoothScanViewModel, scope: CoroutineScope) :
    BaseRepository(viewModel, scope) {
}