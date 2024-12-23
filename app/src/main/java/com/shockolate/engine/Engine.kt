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
    updatePrefsConfig(prefsFile,prefs)
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

private fun updatePrefsConfig(prefsFile: File,prefs: SharedPreferences) {
    val prefsConfigBuffer : StringBuilder = StringBuilder(1024 * 5)

    prefsConfigBuffer.appendLine("language = ${getValue(prefs,"text_localization")}")
    prefsConfigBuffer.appendLine("capture-mouse = ${getBoolValue(prefs,"capture_mouse",true)}")
    prefsConfigBuffer.appendLine("invert-mousey = ${getBoolValue(prefs,"invert_mousey",false)}")
    prefsConfigBuffer.appendLine("half-resolution = ${getBoolValue(prefs,"half_resolution",false)}")
    prefsConfigBuffer.appendLine("music-volume = ${Integer.parseInt(prefs.getString("music_volume","127"))}")
    prefsConfigBuffer.appendLine("sfx-volume = ${Integer.parseInt(prefs.getString("sfx_volume","100"))}")
    prefsConfigBuffer.appendLine("alog-volume = ${Integer.parseInt(prefs.getString("alog_volume","100"))}")
    prefsConfigBuffer.appendLine("video-mode = ${getValue(prefs,"video_mode")}")
    prefsConfigBuffer.appendLine("detail = ${getValue(prefs,"detail")}")
    prefsConfigBuffer.appendLine("use-opengl = ${getBoolValue(prefs,"use-opengl",true)}")
    prefsConfigBuffer.appendLine("texture-filter = ${getValue(prefs,"texture-filter")}")
    prefsConfigBuffer.appendLine("onscreen-help = ${getBoolValue(prefs,"onscreen_help",true)}")
    prefsConfigBuffer.appendLine("gamma = ${Integer.parseInt(prefs.getString("gamma","78"))}")
    prefsFile.writeText(prefsConfigBuffer.toString())
}

private fun getBoolValue (prefs: SharedPreferences, prefsValue : String, defaultValue : Boolean = false) =
    if (prefs.getBoolean(prefsValue,defaultValue))  "yes" else "no"


private fun getValue (prefs: SharedPreferences, prefsValue : String) : Int{
    val language = prefs.getString(prefsValue, "0")!!

    if (language.startsWith("0")){
        return 0
    }
    else if (language.startsWith("1")){
        return 1
    }
    else if (language.startsWith("2")){
        return 2
    }

    return 3
}