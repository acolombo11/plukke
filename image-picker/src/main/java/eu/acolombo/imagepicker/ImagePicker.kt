package eu.acolombo.imagepicker

import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.TakePicturePreview
import eu.acolombo.imagepicker.ActivityResultContracts.Chooser
import eu.acolombo.imagepicker.ActivityResultContracts.Picker

fun ComponentActivity.pickImage(
    intents: List<Intent> = imagePickers, onPick: (Any?) -> Unit
) {

    registerForActivityResult(Chooser()) { intent ->
        onPick(
            intent?.clipData?.items?.map { it.uri.toString() }?.toList()?.first()
                ?: intent?.dataString
                ?: intent?.extras?.get("data") as? Bitmap
        )
    }.launch(intents)

}


private val ComponentActivity.imagePickers
    get() = listOf(
        TakePicturePreview().createIntent(this, null),
        Picker().createIntent(this, Unit)
    )