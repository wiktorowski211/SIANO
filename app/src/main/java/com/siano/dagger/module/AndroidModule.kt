package com.siano.dagger.module

import android.accounts.AccountManager
import android.app.ActivityManager
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.LocationManager
import android.net.ConnectivityManager
import com.siano.dagger.annotations.DaggerAnnotation
import dagger.Module
import dagger.Provides
import java.io.File
import javax.inject.Named
import javax.inject.Singleton

@Module
class AndroidModule {

    @Provides
    @Singleton
    @Named("CacheDir")
    fun httpCacheDir(@DaggerAnnotation.ForApplication context: Context): File {
        return context.cacheDir
    }

    @Provides
    @Singleton
    fun provideContentResolver(@DaggerAnnotation.ForApplication context: Context): ContentResolver {
        return context.contentResolver
    }

    @Provides
    @Singleton
    fun provideLocationManager(@DaggerAnnotation.ForApplication context: Context): LocationManager {
        return context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    @Provides
    @Singleton
    fun provideAccountManager(@DaggerAnnotation.ForApplication context: Context): AccountManager {
        return AccountManager.get(context)
    }

    @Provides
    @Singleton
    fun provideNotificationManager(@DaggerAnnotation.ForApplication context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Provides
    @Singleton
    @DaggerAnnotation.ForApplication
    fun provideResource(@DaggerAnnotation.ForApplication context: Context): Resources {
        return context.resources
    }

    @Provides
    @Singleton
    fun provideConnectivityManager(@DaggerAnnotation.ForApplication context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    fun providePackageManager(@DaggerAnnotation.ForApplication context: Context): PackageManager {
        return context.packageManager
    }

    @Provides
    @Singleton
    fun provideActivityManager(@DaggerAnnotation.ForApplication context: Context): ActivityManager {
        return context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    }
}

