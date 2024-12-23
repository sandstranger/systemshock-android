package com.shockolate.engine

import android.content.Context
import android.content.SharedPreferences
import android.os.Process
import android.system.Os
import android.view.View
import androidx.preference.PreferenceManager
import com.afollestad.materialdialogs.MaterialDialog
import com.shockolate.R
import com.shockolate.engine.activity.EngineActivity
import com.shockolate.utils.CUSTOM_RESOLUTION_PREFS_KEY
import com.shockolate.utils.GAME_FILES_SHARED_PREFS_KEY
import com.shockolate.utils.startActivity
import org.libsdl.app.SDLSurface
import java.io.File


internal val jniLibsArray= arrayOf("c++_shared","omp","FLAC","gio-2.0","glib-2.0","gmodule-2.0","gobject-2.0",
    "gthread-2.0","instpatch-1.0","oboe","ogg","opus","pcre","pcreposix","sndfile","vorbis","vorbisenc",
    "vorbisfile","fluidsynth-assetloader","fluidsynth","GL","SDL2","SDL2_mixer","systemshock")
private const val RESOLUTION_DELIMITER = "x"

@Suppress("DEPRECATION")
internal fun setFullscreen(decorView: View) {
    val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    decorView.systemUiVisibility = uiOptions
}

fun killEngine() = Process.killProcess(Process.myPid())

fun startEngine(context: Context) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val gamePath  = prefs.getString(GAME_FILES_SHARED_PREFS_KEY, "")

    val prefsFile = File("$gamePath/prefs.txt")

    if (!prefsFile.exists()){
        MaterialDialog(context).show {
            message(R.string.can_not_start_game)
            positiveButton(R.string.ok_text)
        }
        return
    }

    Os.setenv("GAME_PATH","${gamePath}/",true)
    Os.setenv("LIBGL_ES", "2", true)
    setCustomScreenResolution(prefs)
    context.startActivity<EngineActivity>()
}

private fun setCustomScreenResolution(prefs : SharedPreferences) {
    val customResolution = prefs.getString(CUSTOM_RESOLUTION_PREFS_KEY, "")
    if (!customResolution.isNullOrEmpty() && customResolution.contains(RESOLUTION_DELIMITER)) {
        try {
            val resolutionsArray = customResolution.split(RESOLUTION_DELIMITER)
            SDLSurface.fixedWidth = Integer.parseInt(resolutionsArray[0])
            SDLSurface.fixedHeight = Integer.parseInt(resolutionsArray[1])
        } catch (e: Exception) {

        }
    }
}