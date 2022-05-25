package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private Node sentinel;
    private int size;

    private class Node {
        private T item;
        private Node prev;
        private Node next;

        Node(T i, Node p, Node n) {
            item = i;
            prev = p;
            next = n;
        }
    }

    /* Create an empty linked list deque */
    public LinkedListDeque() {
        size = 0;
        sentinel = new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
    }

    @Override
    public void addFirst(T item) {
        /* adds item to the front of the list */
        Node node = new Node(item, sentinel, sentinel.next);
        sentinel.next = node;
        node.next.prev = sentinel.next;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        Node node = new Node(item, sentinel.prev, sentinel);
        sentinel.prev.next = node;
        sentinel.prev = node;
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node current = sentinel.next;
        while (current != sentinel) {
            System.out.print(current.item + " ");
            current = current.next;
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T current = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size -= 1;
        return current;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T current = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size -= 1;
        return current;
    }

    @Override
    public T get(int index) {
        if (index > size) {
            return null;
        }
        Node current = sentinel.next;
        while (index > 0) {
            current = current.next;
            index -= 1;
        }
        return current.item;
    }

    /* Get ith item recursively */
    public T getRecursive(int index) {
        if (index >= size) {
            return null;
        }
        return getRecursiveHelper(index, sentinel.next);
    }

    private T getRecursiveHelper(int index, Node current) {
        if (index == 0) {
            return current.item;
        } else {
            current = current.next;
            return getRecursiveHelper(index - 1, current);
        }
    }

    /* Create an iterator class with an index instance variable.
    *  Determine if the object is still iterable.
    *  If so, return the ith item and increment index.
    *  Return new iterator. */
    private class LinkedListIterator implements Iterator<T> {
        private int index;
        LinkedListIterator() {
            index = 0;
        }
        public boolean hasNext() {
            return index < size;
        }
        public T next() {
            T returnItem = get(index);
            index += 1;
            return returnItem;
        }
    }

    public Iterator<T> iterator() {
        return new LinkedListIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<T> other = (Deque<T>) o;
        if (this.size != other.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (!(this.get(i).equals(other.get(i)))) {
                return false;
            }
        }
        return true;
    }
}


