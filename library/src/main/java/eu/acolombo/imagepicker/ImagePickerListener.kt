package eu.acolombo.imagepicker

import android.net.Uri

interface ImagePickerListener {

    fun onImagePicked(imageUri: Uri)

    fun onImagePickerError()

}
