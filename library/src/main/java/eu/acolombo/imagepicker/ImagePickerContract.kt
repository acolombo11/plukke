package eu.acolombo.imagepicker

import android.content.Intent
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropFragment

interface ImagePickerContract {

    fun showGalleryPicker()

    fun showCameraPicker()

    fun showFilePicker(fileTypes: String = "text/*|application/*|audio/*")

    fun showGenericPicker(pickerTitle: String, includeCamera: Boolean = true, includeDocuments: Boolean = false)

    fun getCustomCropFragment(square: Boolean = false, color: Int = -1): UCropFragment

    fun setDefaultCropConfiguration(cropOptions: UCrop.Options = UCrop.Options()): ImagePicker

    fun handlePermission(requestCode: Int, grantResults: IntArray)

    fun handleActivityResult(resultCode: Int, requestCode: Int, data: Intent?)

}
