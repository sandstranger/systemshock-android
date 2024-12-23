package com.shockolate.engine.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import com.shockolate.databinding.ScreenControlsBinding
import com.shockolate.engine.jniLibsArray
import com.shockolate.engine.killEngine
import com.shockolate.engine.setFullscreen
import com.shockolate.ui.controls.ScreenControlsManager
import com.shockolate.utils.HIDE_SCREEN_CONTROLS_KEY
import com.shockolate.utils.MAIN_ENGINE_NATIVE_LIB
import com.shockolate.utils.displayInCutoutArea
import org.libsdl.app.SDLActivity


class EngineActivity : SDLActivity() {
    private lateinit var prefsManager : SharedPreferences
    private lateinit var loggerProcess: Process
    private lateinit var screenControlsManager: ScreenControlsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        setFullscreen(window.decorView)
        super.onCreate(savedInstanceState)
        prefsManager = PreferenceManager.getDefaultSharedPreferences(this)
        displayInCutoutArea(prefsManager)
        loggerProcess = createProcess("${Environment.getExternalStorageDirectory().absolutePath}/sshock.log")
        initScreenControls()
    }

    override fun getArguments(): Array<String> {
        val commandLineArgs = prefsManager.getString("command_line", "")

        if (commandLineArgs.isNullOrEmpty() || !commandLineArgs.contains("-")){
            return super.getArguments()
        }

        try {
            val args = arrayListOf<String>()

            commandLineArgs.split(" ".toRegex()).forEach {
                if (it.isNotEmpty()){
                    args +=it
                }
            }

            return args.toTypedArray()
        }
        catch (e: Exception) {
            return super.getArguments()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loggerProcess.destroy()
        killEngine()
    }

    override fun getMainSharedObject() = MAIN_ENGINE_NATIVE_LIB

    override fun getLibraries() = jniLibsArray

    private fun initScreenControls (){
        val hideScreenControls = prefsManager.getBoolean(HIDE_SCREEN_CONTROLS_KEY,false)

        if (!hideScreenControls) {
            val binding = ScreenControlsBinding.inflate(layoutInflater)

            window.addContentView(
                binding.root,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )

            binding.screenControlsRoot.post {
                screenControlsManager = ScreenControlsManager(binding)
                screenControlsManager.enableScreenControls()
            }
        }
    }

    private fun createProcess(pathToLog: String): Process {
        val processBuilder = ProcessBuilder()
        processBuilder.command("/system/bin/sh", "-c", "logcat *:W -d -f $pathToLog")
        processBuilder.redirectErrorStream(true)
        return processBuilder.start()
    }
}