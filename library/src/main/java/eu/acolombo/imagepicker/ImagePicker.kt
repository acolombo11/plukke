package eu.acolombo.imagepicker

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCrop.REQUEST_CROP
import com.yalantis.ucrop.UCrop.RESULT_ERROR
import eu.acolombo.imagepicker.ImagePickerIntents.getPickerIntents
import java.io.File

/**
 * Usage: Create new instance, call [.showGalleryPicker] or [.showCameraPicker]
 * override [Activity.onActivityResult], call [.handleActivityResult] in it
 * override [Activity.onRequestPermissionsResult], call [.handlePermission] in it
 * get picked file with [.getImageFile]
 */
class ImagePicker(private val activity: Activity, private val listener: ImagePickerListener) : ImagePickerContract {

    companion object {
        private const val REQUEST_PERMISSION_START_CAMERA = 99
        private const val REQUEST_PERMISSION_START_PICKER = 100
        private const val REQUEST_CAPTURE = 101
    }

    private var imageUri: Uri? = null
    private var skipCrop: Boolean = true
    private var uCropOptions: UCrop.Options = UCrop.Options()
    private var mPickerTitle: String = ""

    override fun showGenericPicker(pickerTitle: String, includeCamera: Boolean) {
        mPickerTitle = pickerTitle

        fun showPicker() = activity.startActivityForResult(getPickerIntents(activity, pickerTitle, getNewImageUri()), REQUEST_CAPTURE)

        if (includeCamera) {
            if (hasCameraPermission()) showPicker() else requestCameraPermission(REQUEST_PERMISSION_START_PICKER)
        } else {
            showPicker()
        }
    }

    override fun showGalleryPicker(): Unit = activity.run {
        val pickPicture = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickPicture.resolveActivity(packageManager)?.run {
            startActivityForResult(pickPicture, REQUEST_CAPTURE)
        }
    }

    override fun showCameraPicker() {
        if (hasCameraPermission()) startCamera() else requestCameraPermission(REQUEST_PERMISSION_START_CAMERA)
    }

    private fun hasCameraPermission() = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    private fun requestCameraPermission(requestCode: Int = 0) = ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), requestCode)

    private fun startCamera() = activity.run {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)?.let {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getNewImageUri())
            startActivityForResult(intent, REQUEST_CAPTURE)
        } ?: listener.onImagePickerError()
    }

    private fun startCrop(resultImageUri: Uri?) {
        resultImageUri?.let {
            when {
                skipCrop -> listener.onImagePicked(it)
                else -> getUCrop(it).start(activity)
            }
        } ?: listener.onImagePickerError()
    }

    private fun getUCrop(resultImageUri: Uri): UCrop {
        return UCrop.of(
                resultImageUri,
                Uri.fromFile(File(activity.cacheDir, System.currentTimeMillis().toString() + ".jpg"))
        ).withOptions(uCropOptions)
    }

    override fun setupCrop(cropOptions: UCrop.Options): ImagePicker {
        skipCrop = false
        uCropOptions = cropOptions
        return this
    }

    private fun getNewImageUri(): Uri {
        val pictureFileName = System.currentTimeMillis().toString() + ".jpg"
        val pictureFile = File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), pictureFileName)
        return FileProvider.getUriForFile(activity, activity.getString(R.string.authority_file_provider), pictureFile).apply {
            imageUri = this
        }
    }

    override fun handlePermission(requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_START_CAMERA -> if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) startCamera()
            REQUEST_PERMISSION_START_PICKER -> showGenericPicker(mPickerTitle, grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED)
        }
    }

    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            RESULT_OK -> when (requestCode) {
                REQUEST_CAPTURE -> startCrop(data?.data ?: imageUri)
                REQUEST_CROP -> data?.let { listener.onImagePicked(UCrop.getOutput(it)!!) }
            }

            RESULT_ERROR -> {
                listener.onImagePickerError()
            }
        }
        imageUri = null
    }

}
