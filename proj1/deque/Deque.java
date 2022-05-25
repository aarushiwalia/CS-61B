package deque;

public interface Deque<T> {
    /* add the first item to the list */
    void addFirst(T item);

    /* add the last item to the list */
    void addLast(T item);

    /* return list size */
    int size();

    /* print out each element of the list (or simply, the list) */
    void printDeque();

    /* remove and return the first element of the deque */
    T removeFirst();

    /* remove and return the last element of the deque */
    T removeLast();

    /* return the element at the specified index from the list */
    T get(int index);

    /* return true if the element is empty, else return false */
    default boolean isEmpty() {
        return size() == 0;
    }
}
