package com.chernishenko.giphytestapp

import com.chernishenko.giphytestapp.exceptions.EmptyBodyException
import com.chernishenko.giphytestapp.repositories.GiphyRepository
import com.chernishenko.giphytestapp.services.GiphyService
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class GiphyUnitTests {

    private val giphyService: GiphyService = GiphyService()
    private val giphyRepository: GiphyRepository = mockk<GiphyRepository>()

    @Before fun setup() {
        val gson = Gson()
        coEvery { giphyRepository.getGifs(any(), any()) } returns gson.fromJson(TEST_RESPONSE, JsonObject::class.java)
        coEvery { giphyRepository.getGifs("Empty", any()) } returns gson.fromJson(NO_RESULTS_RESPONSE, JsonObject::class.java)
        coEvery { giphyRepository.getGifs("No pagination", any()) } returns gson.fromJson(MISSING_PAGINATION_RESPONSE, JsonObject::class.java)
        coEvery { giphyRepository.getGifs("Unhappy", any()) } returns gson.fromJson("{}", JsonObject::class.java)

        giphyService.giphyRepository = giphyRepository
    }

    @Test fun `Test GiphyService - Happy flow`() = runTest {
        val result = giphyService.searchGifs("Test", 0)

        assertEquals(5, result.data.size)
        assertEquals(25, result.nextPage)
        assertEquals(10, result.total)
    }

    @Test fun `Test GiphyService - No results flow`() = runTest {
        val result = giphyService.searchGifs("Empty", 0)

        assertEquals(0, result.data.size)
        assertEquals(0, result.nextPage)
        assertEquals(0, result.total)
    }

    @Test fun `Test GiphyService - Missing pagination flow`() = runTest {
        val result = giphyService.searchGifs("No pagination", 0)

        assertEquals(5, result.data.size)
        assertEquals(0, result.nextPage)
        assertEquals(0, result.total)
    }

    @Test fun `Test GiphyService - empty response`() {
        assertThrows(EmptyBodyException::class.java) {
            runTest {
                giphyService.searchGifs("Unhappy", 0)
            }
        }
    }

    @Test fun `Test GiphyService item parsing`() = runTest {
        val result = giphyService.searchGifs("Test", 0)
        val list = result.data

        for (item in list) {
            assertNotNull(item.url)
        }
        assertNull(list[0].webp)
        for (i in 1 until list.size) {
            assertNotNull(list[i].webp)
        }
    }
}
