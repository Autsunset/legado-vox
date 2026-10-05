package io.legado.app.ui.main

import androidx.navigation3.runtime.NavKey
import org.junit.Assert.assertEquals
import org.junit.Test

class MainNavigatorReadAloudTest {

    @Test
    fun `opens cloud TTS manager on top of reader`() {
        val reader = MainRouteReadBook(bookUrl = "book")
        val cloudTts = MainRouteCloudTtsEngines(bookUrl = "book")
        val backStack = mutableListOf<NavKey>(MainRouteHome, reader)

        MainNavigator.navigateToRoute(backStack, MainRouteCloudTtsEngines())

        assertEquals(listOf(MainRouteHome, reader, MainRouteCloudTtsEngines()), backStack)
    }

    @Test
    fun `resets to home before cloud TTS manager from unrelated route`() {
        val cloudTts = MainRouteCloudTtsEngines()
        val backStack = mutableListOf<NavKey>(
            MainRouteHome,
            MainRouteSettings,
        )

        MainNavigator.navigateToRoute(backStack, MainRouteCloudTtsEngines())

        assertEquals(listOf(MainRouteHome, MainRouteCloudTtsEngines()), backStack)
    }

    @Test
    fun `opens voice casting without replacing the reader`() {
        val reader = MainRouteReadBook(bookUrl = "book")
        val casting = MainRouteBookVoiceCasting(bookUrl = "book")
        val backStack = mutableListOf<NavKey>(MainRouteHome, reader)

        MainNavigator.navigateToRoute(backStack, casting)

        assertEquals(listOf(MainRouteHome, reader, casting), backStack)
    }

    @Test
    fun `opens TTS cache without replacing the reader`() {
        val reader = MainRouteReadBook(bookUrl = "book")
        val backStack = mutableListOf<NavKey>(MainRouteHome, reader)

        MainNavigator.navigateToRoute(backStack, MainRouteTtsCache)

        assertEquals(listOf(MainRouteHome, reader, MainRouteTtsCache), backStack)
    }

    @Test
    fun `keeps voice casting in the stack when managing its engines`() {
        val reader = MainRouteReadBook(bookUrl = "book")
        val casting = MainRouteBookVoiceCasting(bookUrl = "book")
        val engines = MainRouteCloudTtsEngines(bookUrl = "book")
        val backStack = mutableListOf<NavKey>(MainRouteHome, reader, casting)

        MainNavigator.navigateToRoute(backStack, engines)

        assertEquals(listOf(MainRouteHome, reader, casting, engines), backStack)
    }
}
