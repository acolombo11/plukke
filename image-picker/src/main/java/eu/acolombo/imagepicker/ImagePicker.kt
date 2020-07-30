package eu.acolombo.imagepicker

import android.app.Activity
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import eu.acolombo.imagepicker.ActivityResultContracts.Choose
import eu.acolombo.imagepicker.ActivityResultContracts.PickImage
import eu.acolombo.imagepicker.ActivityResultContracts.TakePhoto

fun ComponentActivity.pickImage(onResult: (Any?) -> Unit) {
    val photo = TakePhoto(this)
    val image = PickImage()

    registerForActivityResult(Choose()) { result ->
        onResult(
            if (result.resultCode == Activity.RESULT_OK) result.data.let { intent ->
                intent?.clipData?.items?.map { it.uri.toString() }?.toList()?.first()
                    ?: intent?.dataString
                    ?: intent?.extras?.get("data") as? Bitmap
                    ?: photo.uri
                    ?: return@registerForActivityResult
            } else return@registerForActivityResult
        )
    }.launch(listOf(photo, image))
}

fun Fragment.pickImage(onPick: (Any?) -> Unit) = activity?.pickImage(onPick)

fun ComponentActivity.takePicture(onResult: (Uri) -> Unit) {
    contentResolver.insert(EXTERNAL_CONTENT_URI, ContentValues())?.let { uri ->
        registerForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
            if (ok) onResult(uri)
        }.launch(uri)
    }
}

fun Fragment.takePicture(onTake: (Uri) -> Unit) = activity?.takePicture(onTake)