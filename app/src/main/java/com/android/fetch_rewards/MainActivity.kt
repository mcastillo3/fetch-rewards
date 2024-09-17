package com.android.fetch_rewards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.fetch_rewards.network.RetrofitInstance
import com.android.fetch_rewards.ui.ItemAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter

    // To display Snackbar or failure
    private lateinit var rootView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rootView = findViewById(R.id.recyclerView)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        itemAdapter = ItemAdapter(emptyList())
        recyclerView.adapter = itemAdapter

        fetchItems()
    }

    private fun fetchItems() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiService = RetrofitInstance.apiService
                val items = apiService.getItems()

                // Filter out items with blank or null names
                val filteredItems = items.filter { !it.name.isNullOrBlank() }

                // Group items by listID and sort them by listID and name
                val groupAndSortedItems = filteredItems.sortedWith(compareBy( {it.listId }, { it.name }))

                withContext(Dispatchers.Main) {
                    itemAdapter
                }
            } catch (e: Exception) {
                // Display an error message using Snackbar
                withContext(Dispatchers.Main) {
                    showError("Failed to fetch items. Please try again.")
                }
            }
        }
    }

    private fun showError(message: String) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }
}

