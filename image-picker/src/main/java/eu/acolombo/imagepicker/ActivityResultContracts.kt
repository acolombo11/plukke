package eu.acolombo.imagepicker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract

object ActivityResultContracts {

    class Chooser : ActivityResultContract<List<Intent>, Intent>(){

        override fun parseResult(resultCode: Int, intent: Intent?) = intent

        override fun createIntent(
            context: Context,
            input: List<Intent>
        ) = Intent(Intent.ACTION_CHOOSER).apply {
            putExtra(Intent.EXTRA_INTENT, input.first())
            putExtra(Intent.EXTRA_INITIAL_INTENTS, input.drop(1).toTypedArray())
        }

    }

    class Picker : ActivityResultContract<Unit, Uri>(){

        override fun createIntent(
            context: Context,
            input: Unit
        ) = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        override fun parseResult(resultCode: Int, intent: Intent?) = intent?.data

    }

}

