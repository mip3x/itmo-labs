package ru.mip3x.algo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BTreeTest {
    @Test
    fun emptyTree() {
        val tree = BTree()

        assertFalse(tree.contains(42))

        assertEquals(emptyList<Int>(), tree.inOrder())
    }

    @Test
    fun singleInsert() {
        val tree = BTree()

        assertTrue(tree.insert(10))
        assertTrue(tree.contains(10))

        assertEquals(listOf(10), tree.inOrder())
    }

    @Test
    fun duplicateInsertAllowed() {
        val tree = BTree()

        assertTrue(tree.insert(10))
        assertTrue(tree.insert(10))
        assertTrue(tree.insert(10))

        assertTrue(tree.contains(10))
        assertEquals(listOf(10, 10, 10), tree.inOrder())
    }

    @Test
    fun rootSplitOnSixthInsert() {
        val tree = BTree()

        val values = listOf(10, 20, 30, 40, 50, 60)
        for (v in values) {
            assertTrue(tree.insert(v))
        }

        assertTrue(tree.contains(30))
        assertTrue(tree.contains(60))
        assertFalse(tree.contains(999))

        assertEquals(values, tree.inOrder())
    }

    @Test
    fun unsortedInsertsStaySortedInOrder() {
        val tree = BTree()
        val values = listOf(7, 1, 9, 3, 8, 2, 6, 5, 4, 10, 15, 12, 11, 14, 13)

        for (v in values) {
            assertTrue(tree.insert(v))
        }

        assertEquals(values.sorted(), tree.inOrder())
    }

    @Test
    fun manyInsertsAndSearches() {
        val tree = BTree()
        val values = (1..50).toList()

        for (v in values) {
            assertTrue(tree.insert(v))
        }

        for (v in values) {
            assertTrue(tree.contains(v))
        }
        assertFalse(tree.contains(0))
        assertFalse(tree.contains(51))

        assertEquals(values, tree.inOrder())
    }
}
