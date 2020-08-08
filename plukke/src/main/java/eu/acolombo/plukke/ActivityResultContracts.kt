package eu.acolombo.plukke

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract

object ActivityResultContracts {

    class Choose : ActivityResultContract<List<ActivityResultContract<*, *>>, ActivityResult>() {

        override fun createIntent(
            context: Context,
            input: List<ActivityResultContract<*, *>>
        ) = Intent(Intent.ACTION_CHOOSER).apply {
            putExtra(Intent.EXTRA_INTENT, input.first().createIntent(context, null))
            putExtra(Intent.EXTRA_INITIAL_INTENTS, input.drop(1).map {
                it.createIntent(context, null)
            }.toTypedArray())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): ActivityResult =
            ActivityResult(resultCode, intent)

    }

    // Same as ActivityResultContracts.TakePicture but input and output are fields
    class TakePhoto(private val input: Uri) : ActivityResultContract<Unit, Uri?>() {

        var output: Uri? = null

        override fun createIntent(
            context: Context,
            input: Unit?
        ) = Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, this.input)

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? =
            input.takeIf { resultCode == RESULT_OK }.also { output = it }

    }

    class PickImage : ActivityResultContract<Unit, Uri>() {

        override fun createIntent(
            context: Context,
            input: Unit?
        ) = Intent(Intent.ACTION_PICK, EXTERNAL_CONTENT_URI)

        override fun parseResult(resultCode: Int, intent: Intent?) = intent?.data

    }

}

