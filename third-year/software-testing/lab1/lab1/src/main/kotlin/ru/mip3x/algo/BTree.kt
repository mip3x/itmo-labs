package ru.mip3x.algo

class BTree {
    private class Node(
        var isLeaf: Boolean
    ) {
        val keys = mutableListOf<Int>()
        val children = mutableListOf<Node>()
    }

    private var root: Node = Node(isLeaf = true)
    private val maxKeys: Int = 4
    private val minKeys: Int = maxKeys / 2

    fun contains(key: Int) : Boolean {
        return contains(root, key)
    }

    private fun contains(node: Node, key: Int) : Boolean {
        for (i in 0 until node.keys.size) {
            if (key <= node.keys[i]) {
                if (key == node.keys[i])
                    return true
                if (node.isLeaf)
                    return false
                return contains(node.children[i], key)
            }
        }

        if (node.isLeaf)
            return false

        return contains(node.children.last(), key)
    }

    fun insert(key: Int) : Boolean {
        insertNonFull(root, key)

        if (root.keys.size > maxKeys) {
            val newRoot = Node(isLeaf = false)
            newRoot.children.add(root)
            splitChild(newRoot, 0)
            root = newRoot
        }

        return true
    }

    fun remove(key: Int): Boolean {
        val removed = removeFromNode(root, key)
        if (!removed) {
            return false
        }

        if (!root.isLeaf && root.keys.isEmpty()) {
            root = root.children.first()
        }
        return true
    }

    private fun splitChild(parent: Node, childIndex: Int) {
        val fullChild = parent.children[childIndex]
        val rightChild = Node(isLeaf = fullChild.isLeaf)

        val mid = fullChild.keys.size / 2
        val median = fullChild.keys[mid] // key that will be up in parent

        // right keys
        for (i in mid + 1 until fullChild.keys.size)
            rightChild.keys.add(fullChild.keys[i])

        // if not leaf, right children are transferred also
        if (!fullChild.isLeaf) {
            for (i in mid + 1 until fullChild.children.size)
                rightChild.children.add(fullChild.children[i])
        }

        // delete right keys from left (old) node
        fullChild.keys.subList(mid, fullChild.keys.size).clear()

        if (!fullChild.isLeaf) {
            fullChild.children.subList(mid + 1, fullChild.children.size).clear()
        }

        // lift median to parent
        parent.keys.add(childIndex, median)

        parent.children.add(childIndex + 1, rightChild)
    }

    private fun insertNonFull(node: Node, key: Int) {
        if (node.isLeaf) {
            insertIntoLeaf(node, key)
            return
        }

        var i = node.keys.size - 1
        while (i >= 0 && key < node.keys[i]) {
            i--
        }
        i++

        insertNonFull(node.children[i], key)

        if (node.children[i].keys.size > maxKeys) {
            splitChild(node, i)
        }
    }

    private fun insertIntoLeaf(node: Node, key: Int) {
        node.keys.add(key)
        var i = node.keys.size - 2
        while (i >= 0 && key < node.keys[i]) {
            node.keys[i + 1] = node.keys[i]
            i--
        }
        node.keys[i + 1] = key
    }

    private fun removeFromNode(node: Node, key: Int): Boolean {
        var idx = 0
        while (idx < node.keys.size && key > node.keys[idx]) {
            idx++
        }

        if (idx < node.keys.size && node.keys[idx] == key) {
            if (node.isLeaf) {
                node.keys.removeAt(idx)
                return true
            }
            return removeFromInternalNode(node, idx)
        }

        if (node.isLeaf) {
            return false
        }

        if (node.children[idx].keys.size == minKeys) {
            fillChild(node, idx)
            if (idx > node.keys.size) {
                idx--
            }
        }

        return removeFromNode(node.children[idx], key)
    }

    private fun removeFromInternalNode(node: Node, keyIndex: Int): Boolean {
        val key = node.keys[keyIndex]
        val leftChild = node.children[keyIndex]
        val rightChild = node.children[keyIndex + 1]

        if (leftChild.keys.size > minKeys) {
            val predecessor = getPredecessor(leftChild)
            node.keys[keyIndex] = predecessor
            return removeFromNode(leftChild, predecessor)
        }

        if (rightChild.keys.size > minKeys) {
            val successor = getSuccessor(rightChild)
            node.keys[keyIndex] = successor
            return removeFromNode(rightChild, successor)
        }

        mergeChildren(node, keyIndex)
        return removeFromNode(leftChild, key)
    }

    private fun getPredecessor(node: Node): Int {
        var current = node
        while (!current.isLeaf) {
            current = current.children.last()
        }
        return current.keys.last()
    }

    private fun getSuccessor(node: Node): Int {
        var current = node
        while (!current.isLeaf) {
            current = current.children.first()
        }
        return current.keys.first()
    }

    private fun fillChild(parent: Node, childIndex: Int) {
        if (childIndex > 0 && parent.children[childIndex - 1].keys.size > minKeys) {
            borrowFromPrevious(parent, childIndex)
            return
        }

        if (childIndex < parent.keys.size && parent.children[childIndex + 1].keys.size > minKeys) {
            borrowFromNext(parent, childIndex)
            return
        }

        if (childIndex < parent.keys.size) {
            mergeChildren(parent, childIndex)
        } else {
            mergeChildren(parent, childIndex - 1)
        }
    }

    private fun borrowFromPrevious(parent: Node, childIndex: Int) {
        val child = parent.children[childIndex]
        val sibling = parent.children[childIndex - 1]

        child.keys.add(0, parent.keys[childIndex - 1])
        parent.keys[childIndex - 1] = sibling.keys.removeAt(sibling.keys.lastIndex)

        if (!child.isLeaf) {
            child.children.add(0, sibling.children.removeAt(sibling.children.lastIndex))
        }
    }

    private fun borrowFromNext(parent: Node, childIndex: Int) {
        val child = parent.children[childIndex]
        val sibling = parent.children[childIndex + 1]

        child.keys.add(parent.keys[childIndex])
        parent.keys[childIndex] = sibling.keys.removeAt(0)

        if (!child.isLeaf) {
            child.children.add(sibling.children.removeAt(0))
        }
    }

    private fun mergeChildren(parent: Node, leftChildIndex: Int) {
        val leftChild = parent.children[leftChildIndex]
        val rightChild = parent.children[leftChildIndex + 1]
        val middleKey = parent.keys.removeAt(leftChildIndex)

        leftChild.keys.add(middleKey)
        leftChild.keys.addAll(rightChild.keys)
        if (!leftChild.isLeaf) {
            leftChild.children.addAll(rightChild.children)
        }

        parent.children.removeAt(leftChildIndex + 1)
    }

    fun inOrder(): List<Int> {
        val result = mutableListOf<Int>()
        inOrder(root, result)
        return result
    }

    private fun inOrder(node: Node, out: MutableList<Int>) {
        // add children fewer than last key
        for (i in 0 until node.keys.size) {
            if (!node.isLeaf)
                inOrder(node.children[i], out)
            out.add(node.keys[i])
        }

        // add last children (the biggest)
        if (!node.isLeaf)
            inOrder(node.children.last(), out)
    }
}
