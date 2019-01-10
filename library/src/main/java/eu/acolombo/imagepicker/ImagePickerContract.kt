package eu.acolombo.imagepicker

import android.content.Intent
import com.yalantis.ucrop.UCrop
import eu.acolombo.imagepicker.ImagePicker

interface ImagePickerContract {

    fun showGenericPicker(pickerTitle: String, includeCamera: Boolean = true)

    fun showGalleryPicker()

    fun showCameraPicker()

    fun setupCrop(cropOptions: UCrop.Options = UCrop.Options()): ImagePicker

    fun handlePermission(requestCode: Int, grantResults: IntArray)

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    
}
