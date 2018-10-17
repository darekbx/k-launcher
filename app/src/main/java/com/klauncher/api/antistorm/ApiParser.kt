package com.klauncher.api.antistorm

import com.klauncher.api.antistorm.model.Response

/*
timestamp:1539720005,1539719401,1539718803,1539718201,1539717601,1539717002,1539716401,1539715801,1539715201,1539714602
<br>nazwa_folderu:2018.10.16,2018.10.16,2018.10.16,2018.10.16,2018.10.16,2018.10.16,2018.10.16,2018.10.16,2018.10.16,2018.10.16
<br>nazwa_pliku:22-0,21-50,21-40,21-30,21-20,21-10,21-0,20-50,20-40,20-30
<br>nazwa_pliku_front:20181016.1958,20181016.1948,20181016.1938,20181016.1928,20181016.1918,20181016.198,20181016.1858,20181016.1848,20181016.1838,20181016.1828
<br>aktywnosc_burz:0,0,0,0,0,0,0,0,0,0
<br>
*/

class ApiParser {

    fun parse(content: String): Response {
        val timestampKey = "timestamp:"
        val foldersKey = "nazwa_folderu:"
        val filesKey = "nazwa_pliku:"
        val frontFilesKey = "nazwa_pliku_front:"
        val stormKey = "aktywnosc_burz:"
        val chunks = content.split("<br>")

        val response = Response()

        extractLongs(chunks, timestampKey)?.let { response.timestamp.addAll(it) }
        extractStrings(chunks, foldersKey)?.let { response.folderNames.addAll(it) }
        extractStrings(chunks, filesKey)?.let { response.fileNames.addAll(it) }
        extractStrings(chunks, frontFilesKey)?.let { response.fileFrontNames.addAll(it) }
        extractLongs(chunks, stormKey)?.let { response.stormActivity.addAll(it) }

        return response
    }

    private fun extractLongs(chunks: List<String>, key: String) = chunks
            .find { it.startsWith(key) }
            ?.substring(key.length)
            ?.split(",")
            ?.map { it.toLong() }

    private fun extractStrings(chunks: List<String>, key: String) = chunks
            .find { it.startsWith(key) }
            ?.substring(key.length)
            ?.split(",")
}