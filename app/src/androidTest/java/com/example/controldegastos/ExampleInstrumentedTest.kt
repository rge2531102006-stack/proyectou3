package com.example.controldegastos

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.controldegastos", appContext.packageName)
    }

    @Test
    fun firestoreServerIsReachable() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val firebaseApp = requireNotNull(FirebaseApp.initializeApp(appContext))
        val expenses = FirebaseFirestore.getInstance(firebaseApp)
            .collection("expenses")
            .limit(1)

        try {
            Tasks.await(expenses.get(Source.SERVER), 30, TimeUnit.SECONDS)
        } catch (error: ExecutionException) {
            val firestoreError = error.cause as? FirebaseFirestoreException
            assertEquals(
                "Firestore must respond or explicitly deny the read",
                FirebaseFirestoreException.Code.PERMISSION_DENIED,
                firestoreError?.code
            )
        }
    }
}