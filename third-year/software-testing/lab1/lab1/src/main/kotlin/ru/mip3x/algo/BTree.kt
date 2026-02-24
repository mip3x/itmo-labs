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
