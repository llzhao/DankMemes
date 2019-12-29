package com.gelostech.dankmemes

import androidx.multidex.MultiDexApplication
import com.google.firebase.database.FirebaseDatabase
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.gelostech.dankmemes.di.*
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber


class DankMemes : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        // Initialize app
        if (FirebaseApp.getApps(this).isNullOrEmpty())
            FirebaseApp.initializeApp(this)

        // Set Timber
        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())

        // Initialize crash activity
        CaocConfig.Builder.create()
                .enabled(true)
                .showErrorDetails(false)
                .errorDrawable(R.mipmap.ic_launcher)
                .apply()

        // Enable Firebase persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        FirebaseFirestore.getInstance().firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()

        // Initialize Koin
        startKoin {
            androidLogger()
            androidContext(this@DankMemes)
            modules(listOf(
                    firebaseModule,
                    repositoriesModule,
                    viewModelsModule,
                    sessionManagerModule,
                    googleSignClientModule)
            )
        }
    }
}