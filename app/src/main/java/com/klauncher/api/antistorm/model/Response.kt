package com.klauncher.api.antistorm.model

data class Response(
        val timestamp: MutableList<Long> = mutableListOf(),
        val folderNames: MutableList<String> = mutableListOf(),
        val fileNames: MutableList<String> = mutableListOf(),
        val fileFrontNames: MutableList<String> = mutableListOf(),
        val stormActivity: MutableList<Long> = mutableListOf()) {

    fun isValid() = fileFrontNames.isNotEmpty() && fileNames.isNotEmpty() && folderNames.isNotEmpty()
}