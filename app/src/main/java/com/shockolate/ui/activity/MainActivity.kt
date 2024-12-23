package com.shockolate.ui.activity

import android.os.Bundle
import com.shockolate.R
import com.shockolate.presenter.MainActivityPresenter
import com.shockolate.databinding.MainActivityBinding
import com.shockolate.ui.fragment.SettingsFragment
import moxy.MvpAppCompatActivity
import moxy.MvpView
import moxy.presenter.InjectPresenter

internal class MainActivity : MvpAppCompatActivity(), MvpView {
    @InjectPresenter
    lateinit var presenter: MainActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startGameButton.setOnClickListener {
            presenter.onStartGameBtnClicked(this@MainActivity)
        }

        presenter.requestExternalStorage(this)
        changeFragment()
    }

    private fun changeFragment() {
        val fragmentContainterResId = R.id.fragments_container
        var fragment = supportFragmentManager.findFragmentById(fragmentContainterResId)

        if (fragment == null) {
            fragment = SettingsFragment()
            supportFragmentManager.beginTransaction().apply {
                replace(fragmentContainterResId, fragment)
                commit()
            }
        }
    }
}
