package eu.acolombo.imagepicker

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore


object ImagePickerIntents {

    private fun getGalleryIntents(packageManager: PackageManager, action: String): List<Intent> {
        val intents = mutableListOf<Intent>()

        val galleryIntent = if (action === Intent.ACTION_GET_CONTENT) {
            Intent(action)
        } else {
            Intent(action, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        }

        galleryIntent.type = "image/*"
        val listGallery = packageManager.queryIntentActivities(galleryIntent, 0)

        for (res in listGallery) {
            val intent = Intent(galleryIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            intents.add(intent)
        }

        return intents
    }

    private fun getCameraIntent(outputFileUri: Uri): Intent {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
        return intent
    }

    fun getPickerIntents(context: Context, pickerTitle: String, cameraPicturePath: Uri? = null): Intent {
        val allIntents = mutableListOf<Intent>()
        val packageManager = context.packageManager

        var galleryIntents = getGalleryIntents(packageManager, Intent.ACTION_GET_CONTENT)
        if (galleryIntents.isEmpty()) galleryIntents = getGalleryIntents(packageManager, Intent.ACTION_PICK)
        cameraPicturePath?.let { allIntents.add(getCameraIntent(it)) }

        allIntents.addAll(galleryIntents)

        val target = if (allIntents.isEmpty()) Intent() else allIntents[allIntents.size - 1].run { allIntents.removeAt(allIntents.size - 1) }
        val chooserIntent = Intent.createChooser(target, pickerTitle)

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray())
        return chooserIntent
    }


}