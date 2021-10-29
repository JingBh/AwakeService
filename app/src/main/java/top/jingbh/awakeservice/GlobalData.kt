package top.jingbh.awakeservice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

private val serviceStarted: MutableLiveData<Boolean> by lazy {
    MutableLiveData<Boolean>().apply {
        value = false
    }
}

fun getServiceStarted(): LiveData<Boolean> {
    return serviceStarted
}

fun setServiceStarted(value: Boolean) {
    serviceStarted.value = value
}
