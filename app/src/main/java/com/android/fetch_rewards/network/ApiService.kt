package com.android.fetch_rewards.network

import com.android.fetch_rewards.model.Item
import retrofit2.http.GET

interface ApiService {
    @GET("hiring.json")
    suspend fun getItems(): List<Item>

}