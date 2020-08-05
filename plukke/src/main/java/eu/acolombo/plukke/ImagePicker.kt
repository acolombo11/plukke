package eu.acolombo.plukke

import android.app.Activity
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import eu.acolombo.plukke.ActivityResultContracts.Choose
import eu.acolombo.plukke.ActivityResultContracts.PickImage
import eu.acolombo.plukke.ActivityResultContracts.TakePhoto


fun ComponentActivity.pickImage(onResult: (Uri) -> Unit) {
    val photo = TakePhoto(this)
    val image = PickImage()

    registerForActivityResult(Choose()) { result ->
        onResult(
            if (result.resultCode == Activity.RESULT_OK) result.data.let { intent ->
                intent?.data ?: photo.uri ?: return@registerForActivityResult
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