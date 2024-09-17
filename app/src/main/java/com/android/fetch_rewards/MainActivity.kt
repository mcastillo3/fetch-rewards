package com.android.fetch_rewards

import android.icu.text.Transliterator.Position
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewParent
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.fetch_rewards.model.Item
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
    private lateinit var listIdSpinner: Spinner
    private var allItems: List<Item> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        listIdSpinner = findViewById(R.id.listIdSpinner)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        itemAdapter = ItemAdapter(emptyList())
        recyclerView.adapter = itemAdapter

        // Fetch items from API
        fetchItems()
    }

    private fun fetchItems() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiService = RetrofitInstance.apiService
                val items = apiService.getItems()

                // Filter out items with blank or null names
                allItems = items.filter { !it.name.isNullOrBlank() }

                Log.d("MainActivity", "Fetched items: ${allItems.size}")

                // Get unique listIds and update spinner
                val uniqueListIds = allItems.map { it.listId }.distinct().sorted()

                withContext(Dispatchers.Main) {
                    itemAdapter.updateItems(allItems)

                    Log.d("MainActivity", "Items passed to adapter: ${allItems.size}")

                    setupSpinner(uniqueListIds)
                }
            } catch (e: Exception) {
                e.printStackTrace()

                // Display an error message using Snackbar
                withContext(Dispatchers.Main) {
                    showError("Failed to fetch items. Check your network connection.")
                }
            }
        }
    }

    private fun setupSpinner(listIds: List<Int>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listIds.map { it.toInt() })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        listIdSpinner.adapter = adapter

        // Set listener for spinner item selection
        listIdSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedListId = parent.getItemIdAtPosition(position).toInt()+1

                // Filter items by the selected listId
                filterItemsByListId(selectedListId)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun filterItemsByListId(listId: Int) {
        val filteredItems = allItems.filter { it.listId == listId }
        itemAdapter.updateItems(filteredItems)
    }

    private fun showError(message: String) {
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_LONG).show()
    }
}

