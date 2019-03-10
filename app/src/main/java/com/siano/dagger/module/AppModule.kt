package com.siano.dagger.module

import android.content.Context
import com.siano.App
import com.siano.TokenPreferences
import com.siano.dagger.annotations.DaggerAnnotation
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    @DaggerAnnotation.ForApplication
    fun provideContext(application: App): Context = application.applicationContext

    @Provides
    @Singleton
    fun userPreferences(@DaggerAnnotation.ForApplication context: Context): TokenPreferences = TokenPreferences(context)
}
