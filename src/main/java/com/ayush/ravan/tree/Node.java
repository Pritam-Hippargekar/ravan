package com.ayush.ravan.tree;


//node class that defines BST node
public class Node {
    //instance variable of Node class
    int data;
    Node left, right;

    public Node(int data){
        this.data = data;
        left = right = null;
    }
}
