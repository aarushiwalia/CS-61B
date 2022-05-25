package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private int size;
    private int firstIndex;
    private int lastIndex;
    private static int initialLength = 8;

    public ArrayDeque() {
        items = (T[]) new Object[initialLength];
        size = 0;
        firstIndex = 0;
        lastIndex = 1;
    }

    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[firstIndex] = item;
        firstIndex = removeOne(firstIndex);
        size += 1;
    }

    @Override
    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[lastIndex] = item;
        lastIndex = addOne(lastIndex);
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        int current = addOne(firstIndex);
        while (current != lastIndex) {
            System.out.print(items[current] + " ");
            current = addOne(firstIndex);
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }

        int first = addOne(firstIndex);
        T firstItem = items[first];
        items[first] = null;
        firstIndex = first;
        size -= 1;

        if (items.length >= 16 && size < items.length * 0.25) {
            resize(items.length / 2);
        }

        return firstItem;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        int last = removeOne(lastIndex);
        T lastItem = items[last];
        items[last] = null;
        lastIndex = last;
        size -= 1;

        if (items.length >= 16 && size < items.length * 0.25) {
            resize(items.length / 2);
        }

        return lastItem;
    }

    private int removeOne(int index) {
        return (index - 1 + items.length) % items.length;
    }

    private int addOne(int index) {
        return (index + 1) % items.length;
    }

    @Override
    public T get(int index) {
        if (index > size) {
            return null;
        } else {
            return items[(addOne(firstIndex) + index) % items.length];
        }
    }

    /* Resize object (if the size is greater than or equal to the length) */
    private void resize(int x) {
        T[] myArray = (T[]) new Object[x];
        int current = addOne(firstIndex);
        for (int i = 0; i < size; i += 1) {
            myArray[i] = items[current];
            current = addOne(current);
        }

        items = myArray;
        firstIndex = x - 1;
        lastIndex = size;
    }

    /* Create an iterator class with an index instance variable.
     *  Determine if the object is still iterable.
     *  If so, return the ith item and increment index.
     *  Return new iterator. */
    private class ArrayDequeIterator implements Iterator<T> {
        private int index;
        ArrayDequeIterator() {
            index = 0;
        }
        public boolean hasNext() {
            return index < size;
        }
        public T next() {
            T returnItem = items[index];
            index += 1;
            return returnItem;
        }
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
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
