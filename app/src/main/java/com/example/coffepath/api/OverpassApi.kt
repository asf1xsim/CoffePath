package com.example.coffepath.api

import com.example.coffepath.model.OverpassResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OverpassApi {

    @POST("interpreter")
    suspend fun getCafes(@Body query: String): Response<OverpassResponse>

    companion object {
        fun buildQuery(south: Double, west: Double, north: Double, east: Double): String {
            return """
                [out:json][timeout:25];
                (
                  node["amenity"="cafe"]($south,$west,$north,$east);
                  way["amenity"="cafe"]($south,$west,$north,$east);
                  relation["amenity"="cafe"]($south,$west,$north,$east);
                );
                out center;
            """.trimIndent()
        }
    }
}