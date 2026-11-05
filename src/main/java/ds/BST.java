package ds;

import java.util.LinkedList;

/**
 * Binary Search Tree (Week 7-8).
 * Every node has a key (text), some data (int[]), a left child and a right child.
 * Smaller keys go left, bigger keys go right.
 * insert / search: O(log n) on average, O(n) in the worst case.
 *
 * The three traversals from the slides are included:
 *   inOrder   = Left, Root, Right  (gives the keys in sorted order)
 *   preOrder  = Root, Left, Right
 *   postOrder = Left, Right, Root
 */
public class BST {

    private static class Node {
        String key;
        int[] data;
        Node left;
        Node right;

        Node(String key, int[] data) {
            this.key = key;
            this.data = data;
            this.left = null;
            this.right = null;
        }
    }

    private Node root;
    private int size;

    /** Adds the key, or replaces its data when the key is already in the tree. */
    public void insert(String key, int[] data) {
        if (root == null) {
            root = new Node(key, data);
            size = 1;
            return;
        }
        Node current = root;
        while (true) {
            int result = key.compareTo(current.key);
            if (result == 0) {
                current.data = data;
                return;
            } else if (result < 0) {
                if (current.left == null) {
                    current.left = new Node(key, data);
                    size++;
                    return;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new Node(key, data);
                    size++;
                    return;
                }
                current = current.right;
            }
        }
    }

    /** Returns the data for the key, or null when the key is not in the tree. */
    public int[] search(String key) {
        Node current = root;
        while (current != null) {
            int result = key.compareTo(current.key);
            if (result == 0) {
                return current.data;
            }
            current = (result < 0) ? current.left : current.right;
        }
        return null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int height() {
        return height(root);
    }

    private int height(Node node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(height(node.left), height(node.right));
    }

    public LinkedList<String> inOrder() {
        LinkedList<String> keys = new LinkedList<String>();
        inOrder(root, keys);
        return keys;
    }

    private void inOrder(Node node, LinkedList<String> keys) {
        if (node == null) {
            return;
        }
        inOrder(node.left, keys);
        keys.add(node.key);
        inOrder(node.right, keys);
    }

    public LinkedList<String> preOrder() {
        LinkedList<String> keys = new LinkedList<String>();
        preOrder(root, keys);
        return keys;
    }

    private void preOrder(Node node, LinkedList<String> keys) {
        if (node == null) {
            return;
        }
        keys.add(node.key);
        preOrder(node.left, keys);
        preOrder(node.right, keys);
    }

    public LinkedList<String> postOrder() {
        LinkedList<String> keys = new LinkedList<String>();
        postOrder(root, keys);
        return keys;
    }

    private void postOrder(Node node, LinkedList<String> keys) {
        if (node == null) {
            return;
        }
        postOrder(node.left, keys);
        postOrder(node.right, keys);
        keys.add(node.key);
    }
}
