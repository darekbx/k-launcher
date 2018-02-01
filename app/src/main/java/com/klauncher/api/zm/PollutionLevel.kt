package com.klauncher.api.zm

import com.google.gson.Gson
import com.klauncher.model.rest.zm.*
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.toSingle
import okhttp3.OkHttpClient
import okhttp3.Request

class PollutionLevel {

    companion object {
        val ZM_API_ADDRESS = "http://www.zm.org.pl/powietrze/json/"
        val PROBES_ORDER = arrayOf("Warszawa-Marszałkowska", "Warszawa-Komunikacyjna", "Warszawa-Ursynów", "Warszawa-Targówek")
    }

    fun loadPollution(): Single<ActualPollution> {
        val request = Request.Builder().url(ZM_API_ADDRESS).build()
        return OkHttpClient()
                .newCall(request)
                .toSingle()
                .map { call -> call.execute() }
                .map { response -> response.body()?.string() ?: "" }
                .map { json -> removeFunctionWrapper(json) }
                .map { json -> parseJson(json) }
                .map { wrapper -> createActualPollutionModel(wrapper) }
    }

    fun getActualPollution(results: TypedResults): Pollution {
        val probes = arrayOf(results.sonda1, results.sonda2, results.sonda3, results.sonda4)
        return Observable
                .fromArray(PROBES_ORDER.asList())
                .flatMapIterable { it }
                .map { name -> probes.first { probe -> probe.name == name } }
                .filter { it.isValid() }
                .blockingFirst()
    }

    private fun removeFunctionWrapper(input: String) = input.substring(15, input.length - 1)

    private fun parseJson(json: String) = Gson().fromJson(json, Wrapper::class.java)

    private fun createActualPollutionModel(wrapper: Wrapper) =
            ActualPollution(
                    getActualPollution(wrapper.results.pm_10),
                    getActualPollution(wrapper.results.pm_25),
                    wrapper.time
            )
}