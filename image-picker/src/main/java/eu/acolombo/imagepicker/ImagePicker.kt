package eu.acolombo.imagepicker

import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts.TakePicturePreview
import eu.acolombo.imagepicker.ActivityResultContracts.ChooseContracts
import eu.acolombo.imagepicker.ActivityResultContracts.PickImage

fun ComponentActivity.pickImage(
    intents: List<ActivityResultContract<*, *>> = listOf(TakePicturePreview(), PickImage()),
    onPick: (Any?) -> Unit
) {

    registerForActivityResult(ChooseContracts()) { intent ->
        onPick(
            intent?.clipData?.items?.map { it.uri.toString() }?.toList()?.first()
                ?: intent?.dataString
                ?: intent?.extras?.get("data") as? Bitmap
        )
    }.launch(intents)

}