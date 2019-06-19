package com.benbaba.dadpat.host.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer

/**
 * 音频播放封装
 */
class AudioPlayerManager private constructor() {
    private var mMediaPlayer: MediaPlayer = MediaPlayer()

    companion object {
        val instance = SingletonHolder.holder
    }

    init {
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mMediaPlayer.setOnPreparedListener {
        }
        mMediaPlayer.setOnCompletionListener {

        }
    }



    /**
     * 播放音频
     */
    public fun prepare(context: Context, assetPath: String) {
        mMediaPlayer.reset()
        val fd = context.assets.openFd(assetPath)
        mMediaPlayer.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
        mMediaPlayer.prepare()
    }

    /**
     * 设置是否循环播放
     */
    public fun setLoop(isLoop: Boolean) {
        mMediaPlayer.isLooping = isLoop
    }

    public fun start() {
        mMediaPlayer.start()
    }

    public fun stop() {
        mMediaPlayer.stop()
    }

    /**
     * 设置是否自动播放
     */
    public fun setPrepareCompletPlay(isAutoPlay: Boolean) {

    }

    public fun setVolume(left: Float, right: Float) {
        mMediaPlayer.setVolume(left, right)
    }

    private fun reset() {
        mMediaPlayer.reset()
    }


    private object SingletonHolder {
        val holder = AudioPlayerManager()
    }
}