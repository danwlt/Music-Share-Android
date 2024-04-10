package com.example.musicshare

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class SpotifyApiConnector private constructor() {
    private val client = OkHttpClient()

    fun performSearch(searchObject: String): List<SelectableItem> {
        val request = requestFactory("https://spotify23.p.rapidapi.com/search/?q=$searchObject&type=multi&offset=0&limit=10&numberOfTopResults=5")
        val artistsObject = mutableListOf<String>()
        val searchResults = mutableListOf<SelectableItem>()

        try{

            val response = client.newCall(request).execute()

            response.body?.string()?.let {
                val json = parseJsonResponse(it)

                val tracks = json["tracks"] as? Map<*, *>

                val items = tracks?.get("items") as? List<*>

                items?.forEach { item ->
                    if (item is Map<*, *>) {
                        artistsObject.clear()
                        val data = item["data"] as? Map<*, *>
                        val name = data?.get("name")
                        val uri = data?.get("uri")

                        val album = data?.get("albumOfTrack") as? Map<*, *>
                        val albumName = album?.get("name")

                        val coverArt = album?.get("coverArt") as? Map<*, *>
                        val coverArtSources = coverArt?.get("sources") as? List<*>

                        val firstCoverArtSource = coverArtSources?.firstOrNull() as? Map<*, *>
                        val url = firstCoverArtSource?.get("url")
                        val width = firstCoverArtSource?.get("width")
                        val height = firstCoverArtSource?.get("height")

                        val artists = data?.get("artists") as? Map<*, *>
                        val artistItems = artists?.get("items") as? List<*>

                        val artistNames = artistItems?.mapNotNull { artist ->
                            val profile = (artist as? Map<*, *>)?.get("profile") as? Map<*, *>
                            profile?.get("name") as? String
                        }

                        artistNames?.forEach { name ->
                            artistsObject.add(name)
                            Log.i("Artist", "Artist Name: $name")
                        }

                        val urlObject = url.toString()
                        val nameObject = name.toString()

                        Log.i("Cover", "URL: $url, Width: $width, Height: $height")

                        Log.i("Track Data", "Name: $name, URI: $uri, Album: $albumName")
                        searchResults.add(SelectableItem(urlCover = urlObject, artists = artistNames!!, name = nameObject))
                    }
                    Log.i("RESULT", "$artistsObject")
                }

                Log.i("Network Answer", "${json["tracks"]}") }
                Log.i("RESULTS", "$searchResults")

            return searchResults
        }  catch (e: IOException) {
            e.printStackTrace()
            return searchResults
    }

    }


    companion object {
        @Volatile
        private var instance: SpotifyApiConnector? = null

        fun getInstance(): SpotifyApiConnector {
            return instance ?: synchronized(this) {
                instance ?: SpotifyApiConnector().also { instance = it }
            }
        }
    }
}

private fun requestFactory(url: String): Request{

    var request = Request.Builder()
        .url(url)
        .get()
        /*
            INSERT SPOTIFY API KEY BELOW
            GET FROM https://rapidapi.com/Glavier/api/spotify23/
        */
        .addHeader("X-RapidAPI-Key", "API-KEY")
        .addHeader("X-RapidAPI-Host", "spotify23.p.rapidapi.com")
        .build()
    return request
}

private fun parseJsonResponse(jsonResponse: String): Map<String, Any> {
    val gson = Gson()
    val type = object : TypeToken<Map<String, Any>>() {}.type
    return gson.fromJson(jsonResponse, type)
}

data class SelectableItem(val urlCover: String, val artists: List<String>, val name: String) {

}