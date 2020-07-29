package eu.acolombo.imagepicker

import android.content.ContentValues
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.Intent.ACTION_PICK
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build.VERSION_CODES.JELLY_BEAN_MR2
import android.provider.MediaStore
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.afollestad.inlineactivityresult.startActivityForResult

sealed class ImagePicker<T>(val intent: Intent, private val requestCode: Int) {

    // High Quality //https://stackoverrun.com/it/q/8987386 //TODO Not working, trying to not request permissions, in the end we might have to
    class Photo(
        private val activity: FragmentActivity,
        private var uri: Uri? = activity.contentResolver.insert(
            EXTERNAL_CONTENT_URI, ContentValues(1).apply {
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
            })
    ) : ImagePicker<Bitmap>(
        Intent(ACTION_IMAGE_CAPTURE).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
            putExtra(MediaStore.EXTRA_OUTPUT, uri)
        },
        68
    ) {
        override fun getData(data: Intent?): Bitmap? =
            data?.extras?.get("data") as? Bitmap
    }

    object Content : ImagePicker<String>(
        Intent(ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }, 67
    ) {
        override fun getData(data: Intent?): String? = getStringData(data)?.first()
    }

    @RequiresApi(JELLY_BEAN_MR2)
    object MultipleContent : ImagePicker<List<String>>(
        (Content.intent.clone() as Intent).apply {
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }, 66
    ) {
        override fun getData(data: Intent?): List<String>? = getStringData(data)
    }

    // Low quality //TODO Deprecate to use High Quality when working
    object Capture : ImagePicker<Bitmap>(
        Intent(ACTION_IMAGE_CAPTURE),
        65
    ) {
        override fun getData(data: Intent?): Bitmap? =
            data?.extras?.get("data") as? Bitmap
    }

    object Pick : ImagePicker<Uri>(
        Intent(ACTION_PICK, EXTERNAL_CONTENT_URI),
        64
    ) {
        override fun getData(data: Intent?): Uri? =
            data?.data
    }

    class Picker(
        others: List<ImagePicker<*>> = listOf(Capture, Pick),
        title: String? = null
    ) : ImagePicker<List<Any>>(Intent(Intent.ACTION_CHOOSER).apply {
        putExtra(Intent.EXTRA_INTENT, others.first().intent)
        putExtra(Intent.EXTRA_INITIAL_INTENTS, others.drop(1).map { it.intent }.toTypedArray())
        title?.let { putExtra(Intent.EXTRA_TITLE, title) }
    }, 69) {
        override fun getData(data: Intent?): List<Any>? =
            getStringData(data) ?: Capture.getData(data)?.let { listOf(it) }
    }

    abstract fun getData(data: Intent?): T?

    protected fun getStringData(data: Intent?): List<String>? = data?.clipData?.let { clip ->
        return clip.items.map { it.uri.toString() }.toList()
    } ?: data?.dataString?.let { string ->
        return listOf(string)
    }

    fun pick(activity: FragmentActivity, select: (T) -> Unit) {
        activity.startActivityForResult(intent, requestCode) { success, data ->
            if (success) getData(data)?.let { select(it) }
        }
    }

}
