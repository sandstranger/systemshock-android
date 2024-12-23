package com.shockolate.ui.controls

import android.graphics.Color
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.preference.PreferenceManager
import com.shockolate.databinding.ScreenControlsBinding
import com.shockolate.ui.controls.views.SDLImageButton
import org.libsdl.app.SDL


const val VIRTUAL_SCREEN_WIDTH = 1024
const val VIRTUAL_SCREEN_HEIGHT = 768
const val CONTROL_DEFAULT_SIZE = 70

private const val MIDDLE_MOUSE_BUTTON_ID = 2
private const val RIGHT_MOUSE_BUTTON_ID = 3

class ScreenControlsManager(
    private val screenControlsBinding: ScreenControlsBinding
) {

    private var callback: ConfigureCallback? = null
    private val controlsItems = arrayListOf<ControlsItem>()
    private val joystickHolder: SDLJoystick = SDLJoystick(screenControlsBinding.joystick)

    init {
        joystickHolder.joystick.enable = false

        controlsItems += ControlsItem(
            "joystick", joystickHolder.joystick,
            30, 330, 280
        )
        controlsItems += ControlsItem(
            "jump_button", screenControlsBinding.jumpButton.setKeycode(KeyEvent.KEYCODE_SPACE),
            830, 570, 70
        )

        controlsItems.forEach {
            it.loadPrefs()
        }
    }

    fun hideScreenControls (){
        for (button in controlsItems){
            button.view.visibility = View.GONE
        }
    }

    fun showScreenControls (){
        for (button in controlsItems){
            button.view.visibility = View.VISIBLE
        }
    }

    fun editScreenControls() {
        callback = ConfigureCallback(screenControlsBinding.screenControlsRoot)
        controlsItems.forEach {
            it.view.setOnTouchListener(callback)

            if (it.view is SDLImageButton) {
                it.view.interactable = false
            }
        }
        screenControlsBinding.buttonsHolder.visibility = View.VISIBLE
        screenControlsBinding.screenControlsRoot.setBackgroundColor(Color.GRAY)
    }

    fun enableScreenControls() {
        joystickHolder.joystick.enable = true
        screenControlsBinding.buttonsHolder.visibility = View.GONE
        screenControlsBinding.touchCamera.visibility = View.VISIBLE

        controlsItems.forEach {
            if (it.view is SDLImageButton){
                it.view.interactable = true
            }
        }
    }

    fun changeOpacity(delta: Float) {
        val view = callback?.currentView ?: return
        val el = view.tag as ControlsItem
        el.changeOpacity(delta)
        el.updateView()
    }

    fun changeSize(delta: Int) {
        val view = callback?.currentView ?: return
        val el = view.tag as ControlsItem
        el.changeSize(delta)
        el.updateView()
    }

    fun resetItems() {
        controlsItems.forEach {
            it.resetPrefs()
        }
    }

    private inner class ControlsItem(
        val uniqueId: String,
        val view: View,
        val defaultX: Int,
        val defaultY: Int,
        val defaultSize: Int = CONTROL_DEFAULT_SIZE,
        val defaultOpacity: Float = 0.5f,
        val visible: Boolean = true
    ) {

        private var opacity = defaultOpacity
        var size = defaultSize
        var x = defaultX
        var y = defaultY

        init {
            view.tag = this
        }

        fun changeOpacity(delta: Float) {
            opacity = Math.max(0f, Math.min(opacity + delta, 1.0f))
            savePrefs()
        }

        fun changeSize(delta: Int) {
            size = Math.max(0, size + delta)
            savePrefs()
        }

        fun changePosition(virtualX: Int, virtualY: Int) {
            x = virtualX
            y = virtualY
            savePrefs()
        }

        fun updateView() {
            val v = view
            val realScreenWidth = (v.parent as View).width
            val realScreenHeight = (v.parent as View).height
            val realX = x * realScreenWidth / VIRTUAL_SCREEN_WIDTH
            val realY = y * realScreenHeight / VIRTUAL_SCREEN_HEIGHT

            val screenSize = (1.0 * size * realScreenWidth / VIRTUAL_SCREEN_WIDTH).toInt()
            val params = FrameLayout.LayoutParams(screenSize, screenSize)

            params.leftMargin = realX
            params.topMargin = realY

            v.layoutParams = params

            v.alpha = opacity
        }

        private fun savePrefs() {
            val v = view
            val prefs = PreferenceManager.getDefaultSharedPreferences(v.context)
            with(prefs.edit()) {
                putFloat("osc:$uniqueId:opacity", opacity)
                putInt("osc:$uniqueId:size", size)
                putInt("osc:$uniqueId:x", x)
                putInt("osc:$uniqueId:y", y)

                commit()
            }
        }

        fun loadPrefs() {
            val prefs = PreferenceManager.getDefaultSharedPreferences(view.context)

            opacity = prefs.getFloat("osc:$uniqueId:opacity", defaultOpacity)
            size = prefs.getInt("osc:$uniqueId:size", defaultSize)
            x = prefs.getInt("osc:$uniqueId:x", defaultX)
            y = prefs.getInt("osc:$uniqueId:y", defaultY)

            updateView()
        }

        fun resetPrefs() {
            val prefs = PreferenceManager.getDefaultSharedPreferences(view.context)

            with(prefs.edit()) {
                remove("osc:$uniqueId:opacity")
                remove("osc:$uniqueId:size")
                remove("osc:$uniqueId:x")
                remove("osc:$uniqueId:y")

                commit()
            }

            loadPrefs()
        }
    }

    private class ConfigureCallback(private val rootView : View) : View.OnTouchListener {

        var currentView: View? = null
        private var origX: Float = 0.0f
        private var origY: Float = 0.0f
        private var startX: Float = 0.0f
        private var startY: Float = 0.0f

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    currentView?.setBackgroundColor(Color.TRANSPARENT)
                    currentView = v
                    v.setBackgroundColor(Color.RED)
                    origX = v.x
                    origY = v.y
                    startX = event.rawX
                    startY = event.rawY
                }

                MotionEvent.ACTION_MOVE -> if (currentView != null) {
                    val view = currentView!!
                    val x = ((event.rawX - startX) + origX).toInt()
                    val y = ((event.rawY - startY) + origY).toInt()

                    val el = view.tag as ControlsItem
                    el.changePosition(
                        x * VIRTUAL_SCREEN_WIDTH / rootView.width,
                        y * VIRTUAL_SCREEN_HEIGHT /rootView.height
                    )
                    el.updateView()
                }
            }

            return true
        }
    }

}