package com.androidprojek.unifind.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.androidprojek.unifind.R
import com.google.android.material.appbar.MaterialToolbar

class KontakActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kontak)
        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener {
            finish() // kembali ke sebelumnya
        }

    }
}
