package eu.acolombo.imagepicker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import androidx.activity.result.contract.ActivityResultContract

object ActivityResultContracts {

    class ChooseContracts : ActivityResultContract<List<ActivityResultContract<*,*>>, Intent>(){

        override fun parseResult(resultCode: Int, intent: Intent?) = intent

        override fun createIntent(
            context: Context,
            input: List<ActivityResultContract<*,*>>
        ) = Intent(Intent.ACTION_CHOOSER).apply {
            putExtra(Intent.EXTRA_INTENT, input.first().createIntent(context, null))
            putExtra(Intent.EXTRA_INITIAL_INTENTS, input.drop(1).map { it.createIntent(context, null) }.toTypedArray())
        }

    }

    class PickImage : ActivityResultContract<Unit, Uri>(){

        override fun createIntent(
            context: Context,
            input: Unit?
        ) = Intent(Intent.ACTION_PICK, EXTERNAL_CONTENT_URI)

        override fun parseResult(resultCode: Int, intent: Intent?) = intent?.data

    }

}

