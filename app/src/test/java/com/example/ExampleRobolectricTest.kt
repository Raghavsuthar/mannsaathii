package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.LocaleHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read app name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("MannSaathi", appName)
  }

  @Test
  fun `verify locale helper translations for Hindi Gujarati English`() {
    assertEquals("MannSaathi", LocaleHelper.get("app_name", "en"))
    assertEquals("मान्न साथी", LocaleHelper.get("app_name", "hi"))
    assertEquals("મનસાથી", LocaleHelper.get("app_name", "gu"))

    assertNotNull(LocaleHelper.get("medical_disclaimer", "en"))
    assertNotNull(LocaleHelper.get("medical_disclaimer", "hi"))
    assertNotNull(LocaleHelper.get("medical_disclaimer", "gu"))
  }
}
