package com.androidprojek.unifind

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.RelativeLayout
import com.androidprojek.unifind.databinding.ActivitySplashScreenBinding
import android.content.Intent
import android.os.Handler
import android.os.Looper


class SplashScreenActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        animateLogoBg()
    }

    private fun animateLogoBg() {
        val logoBg = binding.logoBg

        // Set tinggi awal jadi 0
        val initialParams = logoBg.layoutParams
        initialParams.height = 0
        logoBg.layoutParams = initialParams

        // Hitung tinggi layar
        val screenHeight = resources.displayMetrics.heightPixels

        // Animasi perubahan height dari 0 ke full
        val animator = ValueAnimator.ofInt(0, screenHeight)
        animator.duration = 1500 // dalam ms
        animator.interpolator = AccelerateDecelerateInterpolator()

        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val params = logoBg.layoutParams
            params.height = value
            logoBg.layoutParams = params
        }

        animator.start()

        // Setelah animasi selesai, tunggu 2 detik lalu pindah ke LoginActivity
        animator.addListener(object : android.animation.Animator.AnimatorListener {
            override fun onAnimationStart(animation: android.animation.Animator) {}

            override fun onAnimationEnd(animation: android.animation.Animator) {
                animateLogoCover()
            }

            override fun onAnimationCancel(animation: android.animation.Animator) {}
            override fun onAnimationRepeat(animation: android.animation.Animator) {}
        })
    }

    private fun animateLogoCover() {
        val logoBg2 = binding.logoBg2
        val splashLogo = binding.splashLogo
        val logoBg = binding.logoBg

        splashLogo.post {
            val targetHeight = splashLogo.height+2

            val animator = ValueAnimator.ofInt(0, targetHeight)
            animator.duration = 1000
            animator.interpolator = AccelerateDecelerateInterpolator()

            animator.addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                val params = logoBg2.layoutParams
                params.height = value
                logoBg2.layoutParams = params
            }

            animator.start()

            // Setelah animasi selesai, delay lalu masuk ke Login
            animator.addListener(object : android.animation.Animator.AnimatorListener {
                override fun onAnimationStart(animation: android.animation.Animator) {}
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 0)
                }

                override fun onAnimationCancel(animation: android.animation.Animator) {}
                override fun onAnimationRepeat(animation: android.animation.Animator) {}
            })
        }
    }

}
