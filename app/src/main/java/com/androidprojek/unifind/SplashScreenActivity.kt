package com.androidprojek.unifind

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        playerView = findViewById(R.id.playerView)

        // Inisialisasi ExoPlayer
        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        // Ambil video dari folder raw
        val videoUri = Uri.parse("android.resource://$packageName/${R.raw.splash_screen_animations}")
        val mediaItem = MediaItem.fromUri(videoUri)
        player.setMediaItem(mediaItem)

        // Set tidak mengulang video
        player.repeatMode = Player.REPEAT_MODE_OFF

        // Mulai video saat sudah siap
        player.prepare()
        player.play()

        // Saat video selesai, pindah ke LoginActivity
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                    startActivity(intent)
                    player.release()
                    finish()
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        player.release()
    }
}
