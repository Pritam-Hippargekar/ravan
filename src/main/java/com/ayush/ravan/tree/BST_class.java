package com.ayush.ravan.tree;

public class BST_class {
    // BST root node
    Node root;

    // Constructor for BST =>initial empty tree
    // Constructor for initialise the root to null BYDEFAULT
    BST_class(){
        root = null;
    }

    // method to check the given tree is Binary search tree or not
    public boolean isBSTOrNot(Node root, int minValue, int maxValue) {
        // check for root is not null or not
        if (root == null) {
            return true;
        }
        // check for current node value with left node value and right node value and recursively check for left sub tree and right sub tree
        if(root.data >= minValue && root.data <= maxValue
                && isBSTOrNot(root.left, minValue, root.data)
                && isBSTOrNot(root.right, root.data, maxValue)){
            return true;
        }
        return false;
    }

    public Node insertNode(Node root, int newData){
        // Base Case: root is null or not
        if (root == null) {
            // Insert the new data, if root is null.
            root = new Node(newData);
            // return the current root to his sub tree
            return root;
        }
        // Here checking for root data is greater or equal to newData or not
        else if (root.data >= newData) {
            // if current root data is greater than the new data then now process the left sub-tree
            root.left = insertNode(root.left, newData);
        } else {
            // if current root data is less than the new data then now process the right sub-tree
            root.right = insertNode(root.right, newData);
        }
        return root;
    }

}
