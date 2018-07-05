package com.klauncher.ui.applications

import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import com.nhaarman.mockito_kotlin.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class IconLoaderTest {

    @After
    fun cleanUp() {
        IconLoader.cache.clear()
    }

    @Test
    fun loadIcon() {
        // Given
        val resolveInfo = mock<ResolveInfo>()
        val drawable = mock<Drawable>()
        val loader = spy(IconLoader(RuntimeEnvironment.application))
        doReturn(drawable).whenever(loader).loadApplicationIcon(resolveInfo)
        doReturn("com.chrome").whenever(loader).getPackageName(resolveInfo)

        // When
        val icon = loader.loadIcon(resolveInfo)

        // Then
        assertEquals(icon, drawable)
    }

    @Test
    fun loadIcon_cache_use() {
        // Given
        val packageName = "com.chrome"
        val resolveInfo = mock<ResolveInfo>()
        val drawable = mock<Drawable>()
        val loader = spy(IconLoader(RuntimeEnvironment.application))
        doReturn(drawable).whenever(loader).loadApplicationIcon(any())
        doReturn(packageName).whenever(loader).getPackageName(any())

        loader.loadIcon(resolveInfo)

        reset(loader)
        doReturn(drawable).whenever(loader).loadApplicationIcon(any())
        doReturn(packageName).whenever(loader).getPackageName(any())

        // When
        val icon = loader.loadIcon(resolveInfo)

        // Then
        assertEquals(icon, drawable)
        verify(loader, never()).loadAndStore(resolveInfo, packageName)
    }
}