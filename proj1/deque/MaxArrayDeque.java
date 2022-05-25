package deque;

import java.util.Comparator;
/* Instantiate a comparator and set it equal to the comparator object parameter.
*  Make a call to super() as you are extending the class; then return the max of the comparator.
*  Iterate over instantiated array and compare the the values to find the max element. */
public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> compared;
    public MaxArrayDeque(Comparator<T> c) {
        super();
        this.compared = c;
    }

    public T max() {
        return max(this.compared);
    }

    public T max(Comparator<T> c) {
        if (this.isEmpty()) {
            return null;
        }
        int index = 0;
        ArrayDeque<T> arrayD = new ArrayDeque<T>();
        for (int i = 0; i < this.size(); i++) {
            int x = c.compare(get(i), get(index));
            if (x >= 0) {
                index = i;
            }
        }
        return get(index);
    }
}
