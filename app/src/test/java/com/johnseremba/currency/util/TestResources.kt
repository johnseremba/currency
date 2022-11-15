package com.johnseremba.currency.util

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

object TestResources {
    private const val BASE_DIR_JSON = "json/"
    private val resourceCache: MutableMap<String, String> = HashMap()

    fun getJsonAsString(filename: String, directory: String = BASE_DIR_JSON): String {
        return getResourceString("$directory$filename")
    }

    private fun getResourceString(resourceName: String): String {
        if (!resourceCache.containsKey(resourceName)) {
            val stream = this::class.java.getResourceAsStream("/$resourceName")
            stream?.let {
                resourceCache[resourceName] = getDataFromStream(it)
            }
        }
        return resourceCache[resourceName] ?: ""
    }

    private fun getDataFromStream(stream: InputStream): String {
        val bufferedReader = BufferedReader(InputStreamReader(stream))
        val stringBuffer = StringBuilder()
        var line: String?

        while (bufferedReader.readLine().also { line = it } != null) {
            stringBuffer.append(line)
        }
        return stringBuffer.toString()
    }
}
