package com.shockolate.engine

import android.content.Context
import android.os.Environment
import android.os.Process
import android.system.Os
import android.view.View
import androidx.preference.PreferenceManager
import com.shockolate.engine.activity.EngineActivity
import com.shockolate.utils.GAME_FILES_SHARED_PREFS_KEY
import com.shockolate.utils.startActivity


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
    Os.setenv("GAME_PATH","${gamePath}/",true)
    Os.setenv("LIBGL_ES", "2", true)
    context.startActivity<EngineActivity>()
}