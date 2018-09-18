package com.klauncher.model.rest.airly

data class Measurement(
        val values: List<Value>,
        val indexes: List<Index>,
        val fromDateTime: String)