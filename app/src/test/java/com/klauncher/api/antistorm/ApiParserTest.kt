package com.klauncher.api.antistorm

import org.junit.Test

import org.junit.Assert.*

class ApiParserTest {

    val testData = """timestamp:1539720005,1539719401,1539718803,1539718201,1539717601,1539717002,1539716401,1539715801,1539715201,1539714602<br>nazwa_folderu:2018.10.16,2018.10.16,2018.10.16,2018.10.16,2018.10.16,2018.10.16,2018.10.16,2018.10.16,2018.10.16,2018.10.16<br>nazwa_pliku:22-0,21-50,21-40,21-30,21-20,21-10,21-0,20-50,20-40,20-30<br>nazwa_pliku_front:20181016.1958,20181016.1948,20181016.1938,20181016.1928,20181016.1918,20181016.198,20181016.1858,20181016.1848,20181016.1838,20181016.1828<br>aktywnosc_burz:1,0,0,0,0,0,0,0,0,5<br>"""

    @Test
    fun parse() {
        // Given
        val parser = ApiParser()

        // When
        val response = parser.parse(testData)

        // Then
        assertEquals(10, response.timestamp.size)
        assertEquals(10, response.folderNames.size)
        assertEquals(10, response.fileNames.size)
        assertEquals(10, response.fileFrontNames.size)
        assertEquals(10, response.stormActivity.size)

        assertEquals(1539720005, response.timestamp[0])
        assertEquals(1539714602, response.timestamp[9])

        assertEquals("2018.10.16", response.folderNames[0])
        assertEquals("2018.10.16", response.folderNames[9])

        assertEquals("22-0", response.fileNames[0])
        assertEquals("20-30", response.fileNames[9])

        assertEquals("20181016.1958", response.fileFrontNames[0])
        assertEquals("20181016.1828", response.fileFrontNames[9])

        assertEquals(1, response.stormActivity[0])
        assertEquals(5, response.stormActivity[9])
    }
}