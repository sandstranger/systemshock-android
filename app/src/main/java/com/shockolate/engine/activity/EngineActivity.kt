package com.shockolate.engine.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import androidx.preference.PreferenceManager
import com.shockolate.engine.jniLibsArray
import com.shockolate.engine.killEngine
import com.shockolate.engine.setFullscreen
import com.shockolate.utils.MAIN_ENGINE_NATIVE_LIB
import com.shockolate.utils.displayInCutoutArea
import org.libsdl.app.SDLActivity
import java.io.IOException
import java.util.Arrays


class EngineActivity : SDLActivity() {
    private lateinit var prefsManager : SharedPreferences
    private lateinit var loggerProcess: Process

    override fun onCreate(savedInstanceState: Bundle?) {
        setFullscreen(window.decorView)
        super.onCreate(savedInstanceState)
        prefsManager = PreferenceManager.getDefaultSharedPreferences(this)
        displayInCutoutArea(prefsManager)
        loggerProcess = createProcess("${Environment.getExternalStorageDirectory().absolutePath}/sshock.log")
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

    private fun createProcess(pathToLog: String): Process {
        val processBuilder = ProcessBuilder()
        val commandToExecute = listOf("/system/bin/sh", "-c", "logcat *:W -d -f $pathToLog")
        processBuilder.command(commandToExecute)
        processBuilder.redirectErrorStream(true)
        return processBuilder.start()
    }
}