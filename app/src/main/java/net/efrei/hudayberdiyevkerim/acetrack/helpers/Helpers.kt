package net.efrei.hudayberdiyevkerim.acetrack.helpers

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.activity.result.ActivityResultLauncher
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Transformation
import net.efrei.hudayberdiyevkerim.acetrack.R
import timber.log.Timber
import java.io.*

fun write(context: Context, fileName: String, data: String) {
    try {
        val outputStreamWriter = OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE))
        outputStreamWriter.write(data)
        outputStreamWriter.close()
    } catch (e: Exception) {
        Timber.e("Cannot write into file $fileName: %s", e.toString())
    }
}

fun read(context: Context, fileName: String): String {
    val str = StringBuilder()

    try {
        val inputStream = context.openFileInput(fileName)

        if (inputStream != null) {
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            var done = false

            while (!done) {
                val line = bufferedReader.readLine()
                done = (line == null);
                if (line != null) str.append(line);
            }

            inputStream.close()
        }
    } catch (e: FileNotFoundException) {
        Timber.e("File $fileName not found: %s", e.toString());
    } catch (e: IOException) {
        Timber.e("Cannot read file $fileName: %s", e.toString());
    }

    return str.toString()
}

fun exists(context: Context, filename: String): Boolean {
    val file = context.getFileStreamPath(filename)
    return file.exists()
}

fun displayImagePicker(intentLauncher : ActivityResultLauncher<Intent>) {
    var chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT)
    chooseFile.type = "image/*"
    chooseFile = Intent.createChooser(chooseFile, R.string.select_player_image.toString())
    intentLauncher.launch(chooseFile)
}

fun customTransformation() : Transformation =
    RoundedTransformationBuilder()
        .borderColor(Color.WHITE)
        .borderWidthDp(2F)
        .cornerRadiusDp(35F)
        .oval(false)
        .build()
