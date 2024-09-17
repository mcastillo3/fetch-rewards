package com.android.fetch_rewards

import com.android.fetch_rewards.model.Item
import org.junit.Assert.*
import org.junit.Test

class ItemProcessorTest {

    // Sample data for testing
    private val sampleItems = listOf(
        Item(1, 1, "Item 1"),
        Item(2, 1, "Item 2"),
        Item(3, 2, "Item 3"),
        Item(4, 2, ""),
        Item(5, 3, null),
        Item(6, 3, "Item 4")
    )

    @Test
    fun testSorting() {
        val sortedItems = sampleItems
            .filter { !it.name.isNullOrBlank() }
            .sortedWith(compareBy({ it.listId }, { it.name }))

        // Check if items are sorted by listId and then by name
        assertEquals("Item 1", sortedItems[0].name)
        assertEquals("Item 2", sortedItems[1].name)
        assertEquals("Item 3", sortedItems[2].name)
        assertEquals("Item 4", sortedItems[3].name)
    }

    @Test
    fun testGrouping() {
        val filteredItems = sampleItems.filter { !it.name.isNullOrBlank() }
        val groupedItems = filteredItems.groupBy { it.listId }

        // Check if items are correctly grouped by listId
        assertTrue(groupedItems.containsKey(1))
        assertTrue(groupedItems.containsKey(2))
        assertTrue(groupedItems.containsKey(3))

        // Check the count of items in each group
        assertEquals(2, groupedItems[1]?.size)
        assertEquals(1, groupedItems[2]?.size)
        assertEquals(1, groupedItems[3]?.size)
    }
}