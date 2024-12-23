package com.shockolate.engine.activity

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.shockolate.engine.jniLibsArray
import com.shockolate.engine.killEngine
import com.shockolate.engine.setFullscreen
import com.shockolate.utils.MAIN_ENGINE_NATIVE_LIB
import org.libsdl.app.SDLActivity

class EngineActivity : SDLActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setFullscreen(window.decorView)
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        killEngine()
    }

    override fun getMainSharedObject() = MAIN_ENGINE_NATIVE_LIB

    override fun getLibraries() = jniLibsArray
}