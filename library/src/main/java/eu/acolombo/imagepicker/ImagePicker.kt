package eu.acolombo.imagepicker

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.widget.Toast
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCrop.REQUEST_CROP
import com.yalantis.ucrop.UCrop.RESULT_ERROR
import com.yalantis.ucrop.UCropFragment
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
        private const val REQUEST_PERMISSION = 100
        private const val REQUEST_CAPTURE = 101
        private const val REQUEST_GALLERY = 102
        private const val REQUEST_FILE = 103

        private const val RESULT_CROP = 200

        const val EXTRA_IMAGE_CROP_URI = "extra_image"
        const val EXTRA_ERROR = "extra_error"
    }

    private var imageUri: Uri? = null
    private var skipCrop: Boolean = true
    private var uCropOptions: UCrop.Options = UCrop.Options()

    override fun showGalleryPicker(): Unit = activity.run {
        val pickPicture = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickPicture.resolveActivity(packageManager)?.run {
            startActivityForResult(pickPicture, REQUEST_GALLERY)
        }
    }

    override fun showCameraPicker(): Unit = activity.run {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION)
        } else {
            startCamera()
        }
    }

    override fun showFilePicker(fileTypes: String) = activity.run {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = fileTypes
        startActivityForResult(intent, REQUEST_FILE)
    }

    private fun startCamera() = activity.run {

        val pictureFileName = System.currentTimeMillis().toString() + ".jpg"
        val pictureFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), pictureFileName)
        imageUri = FileProvider.getUriForFile(this, getString(R.string.authority_file_provider), pictureFile)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)?.let {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(intent, REQUEST_CAPTURE)
        }
    }

    override fun handlePermission(requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION -> if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED)
                startCamera() else showCameraPicker()
        }
    }

    override fun handleActivityResult(resultCode: Int, requestCode: Int, data: Intent?) {
        when (resultCode) {

            RESULT_OK -> when (requestCode) {
                REQUEST_CAPTURE -> startCrop(imageUri ?: data?.data)
                REQUEST_GALLERY -> startCrop(data?.data)
                REQUEST_CROP -> listener.onImagePicked(UCrop.getOutput(data!!)!!) //TODO Manage null
            }

            RESULT_CROP -> {
                val imageCrop: String? = data?.extras?.getString(EXTRA_IMAGE_CROP_URI)
                imageCrop?.let { listener.onImagePicked(Uri.parse(it)) } ?: showError()
                if (data?.extras?.getBoolean(EXTRA_ERROR) == true) showError()
            }

            RESULT_ERROR -> showError()
        }
    }

    private fun startCrop(resultImageUri: Uri?) {
        resultImageUri?.let {
            when {
                skipCrop -> listener.onImagePicked(resultImageUri)
                else -> UCrop.of(
                    resultImageUri,
                    Uri.fromFile(File(activity.cacheDir, System.currentTimeMillis().toString() + ".jpg"))
                ).withOptions(uCropOptions).start(activity)
            }
        } ?: showError()
    }

    private fun showError() {
        Toast.makeText(activity, activity.getString(R.string.error_image_picker), Toast.LENGTH_SHORT).show()
    }

    override fun setDefaultCropConfiguration(cropOptions: UCrop.Options): ImagePicker {
        skipCrop = false
        uCropOptions = cropOptions
        return this
    }

    override fun getCustomCropFragment(square: Boolean, color: Int): UCropFragment {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun showGenericPicker(pickerTitle: String, includeCamera: Boolean, includeDocuments: Boolean) {
        activity.startActivityForResult(getPickerIntents(activity, pickerTitle, includeCamera, includeDocuments), REQUEST_GALLERY)
    }


}
