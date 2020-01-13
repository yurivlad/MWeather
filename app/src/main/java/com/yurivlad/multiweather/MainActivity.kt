package com.yurivlad.multiweather

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yurivlad.multiweather.weeklyForecastModel.Presentation
import org.koin.android.ext.android.inject

const val ROOT_FRAGMENT_TAG = "root_fragment_tag"

class MainActivity : AppCompatActivity() {
    private val presentation: Presentation by inject()

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
}
