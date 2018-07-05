package com.klauncher.ui.applications

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import com.klauncher.model.Application
import com.klauncher.repository.EntryDao
import com.klauncher.repository.Database
import com.klauncher.repository.Repository
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.util.concurrent.CountDownLatch
import com.klauncher.repository.tables.Entry as DBApplication

@RunWith(RobolectricTestRunner::class)
class ApplicationsPresenterTest {

    @Test
    fun increaseClickCount() {
        // Given
        val view = mock<ApplicationsContract.View> { on { getContext() } doReturn RuntimeEnvironment.application }
        val presenter = spy(ApplicationsPresenter(view))
        val repository = spy(Repository(RuntimeEnvironment.application))
        val application = Application("vvm", "package")

        doNothing().whenever(repository).increaseCountByOne(application.packageName, application.name)
        doReturn(repository).whenever(presenter).repository()
        doNothing().whenever(presenter).loadItems()

        // When
        presenter.increaseClickCount(application)

        // Then
        verify(presenter).loadItems()
        verify(repository).increaseCountByOne(application.packageName, application.name)
    }

    @Test
    fun openApplication() {
        // Given
        val packageManager = mock<PackageManager>()
        val context = mock<Context>() { on { getPackageManager() } doReturn packageManager }
        val view = mock<ApplicationsContract.View> { on { getContext() } doReturn context }
        val presenter = spy(ApplicationsPresenter(view))
        val application = Application("vvm", "package")

        val intent = Intent("open_action")

        doNothing().whenever(presenter).increaseClickCount(application)
        doReturn(intent).whenever(packageManager).getLaunchIntentForPackage("package")

        // When
        presenter.openApplication(application)

        // Then
        argumentCaptor<Intent>().apply {
            verify(context).startActivity(capture())
            assertEquals("open_action", firstValue.action)
        }
    }

    @Test
    fun fetchAndCount() {
        // Given
        val apps = listOfApps()
        val rows = listOfRows()

        val view = mock<ApplicationsContract.View> { on { getContext() } doReturn RuntimeEnvironment.application }
        val presenter = spy(ApplicationsPresenter(view))

        doReturn(emptyList<ResolveInfo>()).whenever(presenter).queryActivities(any())
        doReturn(apps).whenever(presenter).mapToApplications(any(), any())

        val database = mock<Database>()
        val applicationDao = mock<EntryDao>()
        doReturn(rows).whenever(applicationDao).listAll()
        doReturn(applicationDao).whenever(database).entryDao()
        doReturn(database).whenever(presenter).database()

        // When
        val latch = CountDownLatch(1)
        presenter.fetchAndCount().subscribe { _, _ -> latch.countDown() }

        latch.await()

        // Then
        assertEquals(5, apps[0].clickCount)
        assertEquals(7, apps[1].clickCount)
        assertEquals(0, apps[2].clickCount)
    }

    @Test
    fun loadCountInfo() {
        // Given
        val view = mock<ApplicationsContract.View>()
        val presenter = spy(ApplicationsPresenter(view))

        val database = mock<Database>()
        val applicationDao = mock<EntryDao>()
        doReturn(emptyList<DBApplication>()).whenever(applicationDao).listAll()
        doReturn(applicationDao).whenever(database).entryDao()
        doReturn(database).whenever(presenter).database()

        // When
        val latch = CountDownLatch(1)

        presenter.loadCountInfo().subscribe { entries, _ ->

            // Then
            assertEquals(0, entries.size)

            latch.countDown()
        }

        latch.await()
    }

    @Test
    fun applyClickCount() {
        // Given
        val apps = listOfApps()
        val rows = listOfRows()
        val view = mock<ApplicationsContract.View>()
        val presenter = ApplicationsPresenter(view)

        // When
        presenter.applyClickCount(apps, rows)

        // Then
        assertEquals(5, apps[0].clickCount)
        assertEquals(7, apps[1].clickCount)
        assertEquals(0, apps[2].clickCount)
    }

    @Test
    fun loadItems() {
        // Given
        val view = mock<ApplicationsContract.View> { on { getContext() } doReturn RuntimeEnvironment.application }
        val presenter = spy(ApplicationsPresenter(view))

        val list = listOf<ResolveInfo>(ResolveInfo())
        doReturn(list).whenever(presenter).queryActivities(any())
        doReturn("Chrome").whenever(presenter).loadLabel(any(), any())
        doReturn("com.chrome").whenever(presenter).loadPackagename(any())
        doReturn(null).whenever(presenter).loadIcon(any())

        // When
        val latch = CountDownLatch(1)
        presenter.loadItemsSynchronously(mock<PackageManager>())
                .subscribe { applications ->
                    view.setItems(applications)
                    latch.countDown()
                }

        latch.await()

        // Then
        argumentCaptor<List<Application>>().apply {
            verify(view).setItems(capture())

            with(firstValue) {
                assertEquals(1, size)
                with(get(0)) {
                    assertEquals("Chrome", name)
                    assertEquals("com.chrome", packageName)
                }
            }
        }
    }

    @Test
    fun createLauncherIntent() {
        // Given
        val view = mock<ApplicationsContract.View>()
        val presenter = ApplicationsPresenter(view)

        // When
        val intent = presenter.createLauncherIntent()

        // Then
        assertEquals(Intent.ACTION_MAIN, intent.action)
        assertTrue(intent.hasCategory(Intent.CATEGORY_LAUNCHER))
    }

    private fun listOfRows() = listOf(
            DBApplication("one", "com.one", 5),
            DBApplication("two", "com.two", 7)
    )

    private fun listOfApps() = listOf(
            Application("one", "com.one"),
            Application("one", "com.two"),
            Application("one", "com.three")
    )
}