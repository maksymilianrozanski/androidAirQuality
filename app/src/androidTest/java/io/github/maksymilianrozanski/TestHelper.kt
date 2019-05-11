package io.github.maksymilianrozanski

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.isInternal
import org.hamcrest.Matchers.not
import org.mockito.Mockito

fun <T> any(): T {
    Mockito.any<T>()
    return uninitialized()
}

private fun <T> uninitialized(): T = null as T

fun stubAllIntents() {
    intending(not<Intent>(isInternal())).respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))
    intending(isInternal()).respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))
}