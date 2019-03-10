package com.siano.dagger.module

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.siano.dagger.annotations.DaggerAnnotation
import com.siano.dagger.annotations.Scope
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class BaseActivityModule {

    @Binds
    @DaggerAnnotation.ForActivity
    abstract fun activityContext(@DaggerAnnotation.ForActivity activity: Activity): Context

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Scope.Activity
        fun activityResources(@DaggerAnnotation.ForActivity activity: Activity): Resources =
                activity.resources

        @JvmStatic
        @Provides
        @Scope.Activity
        fun activityLayoutInflater(@DaggerAnnotation.ForActivity activity: Activity): LayoutInflater =
                LayoutInflater.from(activity)

        @JvmStatic
        @Provides
        @Scope.Activity
        fun activityInputMethodManager(@DaggerAnnotation.ForActivity activity: Activity): InputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        @JvmStatic
        @Provides
        @Scope.Activity
        fun fragmentManager(@DaggerAnnotation.ForActivity activity: Activity): FragmentManager =
                (activity as AppCompatActivity).supportFragmentManager
    }
}