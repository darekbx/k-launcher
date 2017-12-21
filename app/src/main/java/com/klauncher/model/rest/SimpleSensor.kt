package com.klauncher.model.rest

data class SimpleSensor(
        val address: Address,
        val id: Int,
        val location: Location,
        val name: String,
        val pollutionLevel: Int,
        val vendor: String)