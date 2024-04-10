package com.example.musicshare

import android.util.Log
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.Exception

class BackendHandler {
    private val client = OkHttpClient()

    fun addLike(id: String): Boolean {
        val request: Request = requestFactory("/like", HttpMethod.POST, """{"id" : "$id"}""")
        try{
            val response = client.newCall(request).execute()
            response.body?.string()?.let {
                Log.i("RESPONSE", it)}
            return true
        }
        catch (exception: Exception){
            Log.i("Add like failed", exception.toString())
            return false
        }
    }

    fun removeLike(id: String): Boolean {
        val request: Request = requestFactory("/unlike", HttpMethod.POST, """{"id" : "$id"}""")
        try{
            val response = client.newCall(request).execute()
            response.body?.string()?.let {
                Log.i("RESPONSE", it)}
            return true
        }
        catch (exception: Exception){
            Log.i("Remove like failed", exception.toString())
            return false
        }
    }

    fun addPost(item: SelectableItem): Boolean{
        var artists = ""

        for ((index, artist) in item.artists.withIndex()) {
            artists += artist

            // Add a comma and empty space if it's not the last artist
            if (index < item.artists.size - 1) {
                artists += ", "
            }
        }
        val request: Request = requestFactory("/post", HttpMethod.POST, "{\"username\": \"${App.instance.username}\", \"song\": \"${item.name}\", \"artist\": \"${artists}\", \"imgLink\": \"${item.urlCover}\"}")
        try{
            val response = client.newCall(request).execute()
            response.body?.string()?.let {
                Log.i("RESPONSE ADD POST", it)}
            return true
        }
        catch (exception: Exception){
            Log.i("Adding post failed", exception.toString())
            return false
        }
    }

    fun deletePost(id: String): Boolean {
        try{
            val request: Request = requestFactory("/delete", HttpMethod.DELETE, """{"id" : "$id"}""")
            val response = client.newCall(request).execute()
            response.body?.string()?.let {
                Log.i("RESPONSE DELETING POST", it)}
            return true
        }
        catch (exception: Exception){
            Log.i("Deleting post failed", exception.toString())
            return false
        }
    }
    fun getAllPosts(type: String, id: String = ""): Pair<Boolean, List<Item>>{
        try {

            val request: Request = if(id != ""){
                requestFactory("/getPosts?type=$type&id=$id", HttpMethod.GET)
            } else{
                requestFactory("/getPosts?type=$type", HttpMethod.GET)
            }

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            val gson = Gson()
            val responseData: ResponseItem = gson.fromJson(responseBody, ResponseItem::class.java)
            return Pair(true, responseData.posts)
        }
        catch (exception: Exception){
            Log.i("Getting posts failed", exception.toString())
            return Pair(false, mutableListOf())
        }
    }

    fun getOwnPosts(): Pair<Boolean, List<Item>>{
        try {
            val request: Request = requestFactory("/getOwnPosts/${App.instance.username}", HttpMethod.GET)
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            val gson = Gson()
            val responseData: ResponseItem = gson.fromJson(responseBody, ResponseItem::class.java)
            Log.i("Response Data Profile", "$responseData")
            return Pair(true, responseData.posts)
        }
        catch (exception: Exception){
            Log.i("Getting own posts failed", exception.toString())
            return Pair(false, mutableListOf())
        }
    }
}

private fun requestFactory(path: String, type: HttpMethod, body: String = ""): Request {
    var request: Request
    when (type) {
        HttpMethod.POST -> {
            // Handle POST request
            val requestBody = body.toRequestBody("application/json".toMediaType())
            request = Request.Builder()
                .url("http://10.0.2.2:8000$path")
                .post(body = requestBody)
                .build()
        }
        HttpMethod.GET -> {
            // Handle GET request
            request = Request.Builder()
                .url("http://10.0.2.2:8000$path")
                .get()
                .build()
        }
        HttpMethod.DELETE -> {
            // Handle DELETE request
            val requestBody = body.toRequestBody("application/json".toMediaType())
            request = Request.Builder()
                .url("http://10.0.2.2:8000$path")
                .delete(requestBody)
                .build()
        }
    }

    return request
}
data class ResponseItem(val message: String, val posts: List<Item>)

enum class HttpMethod {
    POST,
    GET,
    DELETE
}
