package com.ayush.ravan.linkList;

public class CustomLinkedList {

    private Node headNode;
    static class Node{
        String data;
        Node nextNode;

        public Node(String dataValue){
            this.data = dataValue;
            nextNode = null;
        }

        public Node(String dataValue, Node nextNode) {
            data = dataValue;
            nextNode = nextNode;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public Node getNextNode() {
            return nextNode;
        }

        public void setNextNode(Node nextNode) {
            this.nextNode = nextNode;
        }
    }


//    public static void main(String ...args) {
//    }
}
