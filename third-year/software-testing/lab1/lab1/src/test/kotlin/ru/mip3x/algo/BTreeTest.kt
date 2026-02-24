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

    @Test
    fun removeFromLeaf() {
        val tree = BTree()
        val values = listOf(10, 20, 30, 40)
        for (v in values) {
            tree.insert(v)
        }

        assertTrue(tree.remove(30))
        assertFalse(tree.contains(30))

        assertEquals(listOf(10, 20, 40), tree.inOrder())
    }

    @Test
    fun removeMissingKeyReturnsFalse() {
        val tree = BTree()
        val values = listOf(10, 20, 30, 40)
        for (v in values) {
            tree.insert(v)
        }

        assertFalse(tree.remove(999))

        assertEquals(values, tree.inOrder())
    }

    @Test
    fun removeWithMergesAndInternalNodeCases() {
        val tree = BTree()
        val values = (1..30).toList()
        for (v in values) {
            tree.insert(v)
        }

        val toRemove = listOf(15, 1, 30, 12, 20, 8, 9, 10, 11, 13, 14)
        for (v in toRemove) {
            assertTrue(tree.remove(v))
            assertFalse(tree.contains(v))
        }

        val expected = values.filterNot { it in toRemove }

        assertEquals(expected, tree.inOrder())
    }

    @Test
    fun removeDeletesOneDuplicateAtATime() {
        val tree = BTree()
        tree.insert(10)
        tree.insert(10)
        tree.insert(10)
        tree.insert(5)
        tree.insert(15)

        assertEquals(listOf(5, 10, 10, 10, 15), tree.inOrder())

        assertTrue(tree.remove(10))
        assertEquals(listOf(5, 10, 10, 15), tree.inOrder())

        assertTrue(tree.remove(10))
        assertEquals(listOf(5, 10, 15), tree.inOrder())

        assertTrue(tree.remove(10))
        assertEquals(listOf(5, 15), tree.inOrder())
        assertFalse(tree.contains(10))
    }
}
