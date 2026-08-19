package com.remainder.app.domain.media

/**
 * Abstraction the domain/presentation layers record and manage voice notes
 * through. The Android-specific MediaRecorder implementation lives in the
 * top-level `media` package.
 */
interface VoiceNoteRecorder {
    /** Starts recording to a new file and returns its path. */
    fun start(): String

    /** Stops recording and returns the file path, or null if nothing usable was recorded. */
    fun stop(): String?

    /** Aborts an in-progress recording and discards its file. */
    fun cancel()

    fun deleteFile(path: String)
}
