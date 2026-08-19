package com.remainder.app.media

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import com.remainder.app.domain.media.VoiceNoteRecorder
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

// Singleton: only one recording can happen at a time app-wide, and both the
// interface binding and any direct injection of this class should share the
// same underlying MediaRecorder state.
@Singleton
class VoiceNoteRecorderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : VoiceNoteRecorder {

    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    override fun start(): String {
        val dir = File(context.filesDir, "voice_notes").apply { mkdirs() }
        val file = File(dir, "voice_${System.currentTimeMillis()}.m4a")
        outputFile = file

        val mediaRecorder = newMediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
        recorder = mediaRecorder
        return file.absolutePath
    }

    override fun stop(): String? {
        val path = try {
            recorder?.apply {
                stop()
                release()
            }
            outputFile?.absolutePath
        } catch (e: RuntimeException) {
            // stop() throws if called too soon after start() with no audio captured.
            outputFile?.delete()
            recorder?.release()
            null
        }
        recorder = null
        outputFile = null
        return path
    }

    override fun cancel() {
        try {
            recorder?.stop()
        } catch (e: RuntimeException) {
            // No audio was captured; nothing to clean up beyond the file itself.
        }
        recorder?.release()
        recorder = null
        outputFile?.delete()
        outputFile = null
    }

    override fun deleteFile(path: String) {
        File(path).delete()
    }

    private fun newMediaRecorder(): MediaRecorder =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
}
