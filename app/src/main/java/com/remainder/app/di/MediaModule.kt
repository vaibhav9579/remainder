package com.remainder.app.di

import com.remainder.app.domain.media.VoiceNotePlayer
import com.remainder.app.domain.media.VoiceNoteRecorder
import com.remainder.app.media.VoiceNotePlayerImpl
import com.remainder.app.media.VoiceNoteRecorderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaModule {

    @Binds
    @Singleton
    abstract fun bindVoiceNoteRecorder(impl: VoiceNoteRecorderImpl): VoiceNoteRecorder

    @Binds
    @Singleton
    abstract fun bindVoiceNotePlayer(impl: VoiceNotePlayerImpl): VoiceNotePlayer
}
