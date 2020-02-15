package com.yurivlad.multiweather

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.yurivlad.multiweather.weeklyForecastModel.Presentation
import org.koin.android.ext.android.inject

const val ROOT_FRAGMENT_TAG = "root_fragment_tag"

class MainActivity : AppCompatActivity() {
    private val presentation: Presentation by inject()
    private var lastKeyEvent: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = supportFragmentManager
            .findFragmentByTag(ROOT_FRAGMENT_TAG)
            ?: presentation.getRootFragment(this)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.root_frame, fragment, ROOT_FRAGMENT_TAG)
            .commit()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && lastKeyEvent == KeyEvent.KEYCODE_VOLUME_DOWN) {
                startActivity(Intent(this, DebugActivity::class.java))
            }

            lastKeyEvent = keyCode
            true
        } else super.onKeyDown(keyCode, event)
    }
}
