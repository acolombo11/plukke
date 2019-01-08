package eu.acolombo.imagepicker

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import java.io.File


object ImagePickerIntents {

    /**
     * Get all Gallery intents for getting image from one of the apps of the device that handle images.
     */
    fun getGalleryIntents(packageManager: PackageManager, action: String, includeDocuments: Boolean): List<Intent> {
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

        // remove documents intent
        if (!includeDocuments) {
            for (intent in intents) {
                if (intent.component!!.className == "com.android.documentsui.DocumentsActivity") {
                    intents.remove(intent)
                    break
                }
            }
        }
        return intents
    }

    /**
     * Get the main Camera intent for capturing image using device camera app. If the outputFileUri is
     * null, a default Uri will be created with [.getCaptureImageOutputUri], so then
     * you will be able to get the pictureUri using [.getPickImageResultUri].
     * Otherwise, it is just you use the Uri passed to this method.
     *
     * @param context used to access Android APIs, like content resolve, it is your
     * activity/fragment/widget.
     * @param outputFileUri the Uri where the picture will be placed.
     */
    fun getCameraIntent(context: Context, outputFileUri: Uri? = null): Intent {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri ?: getNewImagePath(context))
        return intent
    }

    fun getPickerIntents(context: Context, pickerTitle: String, includeCamera: Boolean, includeDocuments: Boolean): Intent {
        val allIntents = mutableListOf<Intent>()
        val packageManager = context.packageManager

        var galleryIntents = getGalleryIntents(packageManager, Intent.ACTION_GET_CONTENT, includeDocuments)
        if (galleryIntents.isEmpty()) galleryIntents = getGalleryIntents(packageManager, Intent.ACTION_PICK, includeDocuments)
        if (includeCamera) allIntents.add(getCameraIntent(context))

        allIntents.addAll(galleryIntents)

        val target =
            if (allIntents.isEmpty()) Intent() else allIntents[allIntents.size - 1].run { allIntents.removeAt(allIntents.size - 1) }

        val chooserIntent = Intent.createChooser(target, pickerTitle)

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray())

        return chooserIntent

    }

    /**
     * Get URI to image received from capture by camera.
     *
     * @param context used to access Android APIs, like content resolve, it is your
     * activity/fragment/widget.
     */
    private fun getNewImagePath(context: Context) = context.externalCacheDir?.let {
        Uri.fromFile(File(it.path, "pickImageResult.jpeg"))
    }


}