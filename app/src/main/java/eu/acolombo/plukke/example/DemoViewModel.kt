package eu.acolombo.plukke.example

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DemoViewModel : ViewModel() {

    private val _pickerResult = MutableLiveData<Uri?>()
    val pickerResult: LiveData<Uri?> = _pickerResult
    private val _cameraResult = MutableLiveData<Uri?>()
    val cameraResult: LiveData<Uri?> = _cameraResult

    fun savePicker(uri: Uri) {
        _pickerResult.value = uri
    }

    fun saveCamera(uri: Uri) {
        _cameraResult.value = uri
    }

    fun clearPicker() {
        _pickerResult.value = null
    }

    fun clearCamera() {
        _cameraResult.value = null
    }

}