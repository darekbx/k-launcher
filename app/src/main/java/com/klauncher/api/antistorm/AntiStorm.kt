package com.klauncher.api.antistorm

class AntiStorm {

    companion object {
        const val BASE_URL = "https://antistorm.eu"
        const val API = "/ajaxPaths.php?lastTimestamp=0&type=radar"
        const val IMAGE_WIND = "/archive/%s/%s-radar-velocityMapImg.png" // folderName, fileName
        const val IMAGE_PROBABILITY = "/archive/%s/%s-radar-probabilitiesImg.png" // folderName, fileName
        const val IMAGE_RAIN = "/visualPhenom/%s-radar-visualPhenomenon.png" // frontFileName
        const val IMAGE_STORM = "/visualPhenom/%s-storm-visualPhenomenon.png" // frontFileName
    }



}