package ru.mip3x.algo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.random.Random

class BTreeTest {
    @Test
    fun emptyTree() {
        val tree = BTree<Int>()

        assertFalse(tree.contains(42))

        assertEquals(emptyList<Int>(), tree.inOrder())
    }

    @Test
    fun singleInsert() {
        val tree = BTree<Int>()

        assertTrue(tree.insert(10))
        assertTrue(tree.contains(10))

        assertEquals(listOf(10), tree.inOrder())
    }

    @Test
    fun duplicateInsertAllowed() {
        val tree = BTree<Int>()

        assertTrue(tree.insert(10))
        assertTrue(tree.insert(10))
        assertTrue(tree.insert(10))

        assertTrue(tree.contains(10))
        assertEquals(listOf(10, 10, 10), tree.inOrder())
    }

    @Test
    fun rootSplitOnSixthInsert() {
        val tree = BTree<Int>()

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
        val tree = BTree<Int>()
        val values = listOf(7, 1, 9, 3, 8)

        for (v in values) {
            assertTrue(tree.insert(v))
        }

        assertEquals(values.sorted(), tree.inOrder())
    }

    @Test
    fun manyInsertsAndSearches() {
        val tree = BTree<Int>()
        val values = (1..5).toList()

        for (v in values) {
            assertTrue(tree.insert(v))
        }

        for (v in values) {
            assertTrue(tree.contains(v))
        }
        assertFalse(tree.contains(0))
        assertFalse(tree.contains(6))

        assertEquals(values, tree.inOrder())
    }

    @Test
    fun removeFromLeaf() {
        val tree = BTree<Int>()
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
        val tree = BTree<Int>()
        val values = listOf(10, 20, 30, 40)
        for (v in values) {
            tree.insert(v)
        }

        assertFalse(tree.remove(999))

        assertEquals(values, tree.inOrder())
    }

    @Test
    fun removeWithMergesAndInternalNodeCases() {
        val tree = BTree<Int>()
        val values = (1..20).toList()
        for (v in values) {
            tree.insert(v)
        }

        val toRemove = listOf(15, 1, 19, 12, 20, 8, 9, 10, 11, 13, 14)
        for (v in toRemove) {
            assertTrue(tree.remove(v))
            assertFalse(tree.contains(v))
        }

        val expected = values.filterNot { it in toRemove }

        assertEquals(expected, tree.inOrder())
    }

    @Test
    fun removeDeletesOneDuplicateAtATime() {
        val tree = BTree<Int>()
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

    @Test
    fun removeInternalNodeUsesPredecessor() {
        val tree = BTree<Int>()

        val values = (1..60).toList()
        val insertionOrder = values.shuffled(kotlin.random.Random(42))
        insertionOrder.forEach { tree.insert(it) }

        val keyToRemove = 16
        val expected = values.filterNot { it == keyToRemove }

        assertTrue(tree.remove(keyToRemove))
        assertFalse(tree.contains(keyToRemove))
        assertEquals(expected, tree.inOrder())
    }

    @Test
    fun fillChildBorrowsFromNextOnLeafChild() {
        val tree = BTree<Int>()
        (1..120).shuffled(Random(7)).forEach { tree.insert(it) }

        val root = requireNotNull(getField(tree, "root"))
        val (parent, idx) = requireNotNull(findBorrowNextCase(root, requireInternalChild = false))

        val childBeforeKeys = keysOf(childAt(parent, idx)).size
        val parentKeyBefore = keysOf(parent)[idx]

        invokePrivate(tree, "fillChild", parent, idx)

        val childAfterKeys = keysOf(childAt(parent, idx)).size
        val parentKeyAfter = keysOf(parent)[idx]

        assertEquals(childBeforeKeys + 1, childAfterKeys)
        assertNotEquals(parentKeyBefore, parentKeyAfter)
    }

    @Test
    fun fillChildBorrowsFromNextOnInternalChild() {
        val tree = BTree<Int>()
        (1..220).shuffled(Random(42)).forEach { tree.insert(it) }

        val root = requireNotNull(getField(tree, "root"))
        val (parent, idx) = requireNotNull(findBorrowNextCase(root, requireInternalChild = true))

        val childBeforeKeys = keysOf(childAt(parent, idx)).size
        val childBeforeChildren = childrenOf(childAt(parent, idx)).size

        invokePrivate(tree, "fillChild", parent, idx)

        val childAfterKeys = keysOf(childAt(parent, idx)).size
        val childAfterChildren = childrenOf(childAt(parent, idx)).size

        assertEquals(childBeforeKeys + 1, childAfterKeys)
        assertEquals(childBeforeChildren + 1, childAfterChildren)
    }

    @Test
    fun borrowFromPreviousOnInternalChildMovesChildPointer() {
        val tree = BTree<Int>()
        (1..220).shuffled(Random(99)).forEach { tree.insert(it) }

        val root = requireNotNull(getField(tree, "root"))
        val (parent, idx) = requireNotNull(findBorrowPreviousInternalCase(root))

        val childBeforeKeys = keysOf(childAt(parent, idx)).size
        val childBeforeChildren = childrenOf(childAt(parent, idx)).size

        invokePrivate(tree, "borrowFromPrevious", parent, idx)

        val childAfterKeys = keysOf(childAt(parent, idx)).size
        val childAfterChildren = childrenOf(childAt(parent, idx)).size

        assertEquals(childBeforeKeys + 1, childAfterKeys)
        assertEquals(childBeforeChildren + 1, childAfterChildren)
    }

    @Test
    fun getSuccessorDescendsThroughInternalNodes() {
        val tree = BTree<Int>()
        (1..220).shuffled(Random(123)).forEach { tree.insert(it) }

        val root = requireNotNull(getField(tree, "root"))
        val internalNode = requireNotNull(findNonLeafNode(root))

        val successor = invokePrivate(tree, "getSuccessor", internalNode) as Int
        val expected = minKeyInSubtree(internalNode)

        assertEquals(expected, successor)
        assertTrue(tree.contains(successor))
    }

    private fun invokePrivate(target: Any, methodName: String, vararg args: Any): Any? {
        val method = target.javaClass.declaredMethods.first {
            it.name == methodName && it.parameterCount == args.size
        }
        method.isAccessible = true
        return method.invoke(target, *args)
    }

    private fun getField(target: Any, name: String): Any? {
        val field = target.javaClass.getDeclaredField(name)
        field.isAccessible = true
        return field.get(target)
    }

    @Suppress("UNCHECKED_CAST")
    private fun keysOf(node: Any): MutableList<Int> = getField(node, "keys") as MutableList<Int>

    @Suppress("UNCHECKED_CAST")
    private fun childrenOf(node: Any): MutableList<Any> = getField(node, "children") as MutableList<Any>

    private fun isLeaf(node: Any): Boolean = getField(node, "isLeaf") as Boolean

    private fun childAt(parent: Any, idx: Int): Any = childrenOf(parent)[idx]

    private fun findBorrowNextCase(node: Any, requireInternalChild: Boolean): Pair<Any, Int>? {
        if (isLeaf(node)) return null

        val keys = keysOf(node)
        val children = childrenOf(node)

        for (i in 0 until keys.size) {
            val child = children[i]
            val right = children[i + 1]

            val childOk = if (requireInternalChild) !isLeaf(child) else isLeaf(child)
            val rightOk = if (requireInternalChild) !isLeaf(right) else isLeaf(right)

            if (childOk && rightOk && keysOf(child).size == 2 && keysOf(right).size > 2) {
                return node to i
            }
        }

        for (c in children) {
            val found = findBorrowNextCase(c, requireInternalChild)
            if (found != null) return found
        }

        return null
    }

    private fun findBorrowPreviousInternalCase(node: Any): Pair<Any, Int>? {
        if (isLeaf(node)) return null

        val keys = keysOf(node)
        val children = childrenOf(node)

        for (i in 1..keys.size) {
            val child = children[i]
            val left = children[i - 1]

            if (!isLeaf(child) && !isLeaf(left) && keysOf(child).size == 2 && keysOf(left).size > 2) {
                return node to i
            }
        }

        for (c in children) {
            val found = findBorrowPreviousInternalCase(c)
            if (found != null) return found
        }

        return null
    }

    private fun findNonLeafNode(node: Any): Any? {
        if (!isLeaf(node)) return node
        return null
    }

    private fun minKeyInSubtree(node: Any): Int {
        var current = node
        while (!isLeaf(current)) {
            current = childrenOf(current).first()
        }
        return keysOf(current).first()
    }
}
