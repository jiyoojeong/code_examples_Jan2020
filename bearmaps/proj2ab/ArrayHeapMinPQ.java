package bearmaps.proj2ab;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;
//array list version
public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {
    private List<T> minHeap;
    private HashMap<T, Double> priorities;
    private HashMap<T, Integer> indices;
    private int count;

    public ArrayHeapMinPQ() {
        minHeap = new ArrayList<>(16);
        minHeap.add(0, null); //acts as a place holder for minheap, will not ever be accessed.
        count = 0;
        priorities = new HashMap<>(16);
        indices = new HashMap<>(16);
    }

    //adds it in order by priority, smallest at the beginning at the last index!
    //assumes priority will not be negative
    @Override
    public void add(T item, double priority) {
        if (contains(item)) {
            throw new IllegalArgumentException("this items alr in the heap!");
        }
        //add to back
        minHeap.add(item);
        //update count
        count++;
        //add to HashMap
        priorities.put(item, priority);
        indices.put(item, count);
        //bubble up
        bubbleUp(count);
    }

    /* Returns true if the PQ contains the given item. */
    @Override
    public boolean contains(T item) {
        Set<T> all = priorities.keySet();
        return all.contains(item); //not constant time!
    }

    /* Returns the minimum item. Throws NoSuchElementException if the PQ is empty. */
    @Override
    public T getSmallest() {
        if (count == 0) {
            throw new NoSuchElementException("there's nothing in PQ!");
        }
        //get top of heap
        return minHeap.get(1);
    }

    //moves the object T at index i to the back!
    private void bubbleUp(int i) {
        if (i != 1) {
            double pI = priorities.get(minHeap.get(i)); //pointer
            double pP = priorities.get(minHeap.get(i / 2)); //parent
            while (i > 1 && pP > pI) {
                swap(i, i / 2);
                i = i / 2;
                if (i / 2 < 1) {
                    break;
                }
                pI = priorities.get(minHeap.get(i));
                pP = priorities.get(minHeap.get(i / 2));
            }
        }
    }


    private void bubbleDown(int i) {
        //mistakes were made
        while (2 * i <= count) {
            int j = 2 * i;
            T d = minHeap.get(j);
            if (j + 1 <= count) {
                T s = minHeap.get(j + 1);
                if (j < count && priorities.get(d) > priorities.get(s)) {
                    j++;
                }
            }

            T og = minHeap.get(i);
            T nog = minHeap.get(j); // this is diff than last one
            if (priorities.get(og) <= priorities.get(nog)) {
                break;
            }
            //swap
            swap(i, j);
            i = j;
        }

    }

    private void swap(int i, int j) {
        T og = minHeap.get(i);
        T nog = minHeap.get(j);

        minHeap.set(i, nog);
        indices.remove(og);
        indices.put(og, j);

        minHeap.set(j, og);
        indices.remove(nog);
        indices.put(nog, i);
    }

    /* Removes and returns the minimum item. Throws NoSuchElementException if the PQ is empty. */
    @Override
    public T removeSmallest() { //fix!
        if (count == 0) {
            throw new NoSuchElementException("there's nothing in PQ!");
        }
        //get top of heap
        T smol = minHeap.get(1);
        if (count == 1) {
            count--;
            minHeap.remove(smol);
        } else {
            //swap first for constant time
            T biggo = minHeap.set(count, smol);
            minHeap.set(1, biggo);
            minHeap.remove(count); //remove the new smallest (which is at the end)
            count--;
            //bubble down, the biggest number is now at the front, must bubble down
            bubbleDown(1);
        }
        priorities.remove(smol);
        return smol;
    }

    /* Returns the number of items in the PQ. */
    @Override
    public int size() {
        return count;
    }

    /* Changes the priority of the given item. Throws NoSuchElementException if the item
     * doesn't exist. */
    @Override
    public void changePriority(T item, double priority) {
        //starting over

        int i = indices.get(item);
        if (i == 0) {
            throw new NoSuchElementException("item isn't in PQ.. sed");
        }

        double oldP = priorities.remove(item);
        priorities.put(item, priority);

        //sort
        if (oldP <= priority) {
            //System.out.println(i);
            bubbleDown(i);
        } else {
            //System.out.println(i);
            bubbleUp(i);
        }
    }

    private int index(T item) {
        double lostP = priorities.get(item);
        for (int j = 1; j <= count;) {
            if (minHeap.get(j).equals(item)) {
                return j;
            }

            if (lostP > priorities.get(minHeap.get(j))) {
                j *= 2;
            } else {
                j = j / 2 + 1;
            }
        }
        return 0;
    }

    //@source Print Heap Demo from Hug
    public void printSimpleHeapDrawing() {
        int depth = ((int) (Math.log(minHeap.size()) / Math.log(2)));
        int level = 0;
        int itemsUntilNext = (int) Math.pow(2, level);
        for (int j = 0; j < depth; j++) {
            System.out.print(" ");
        }

        for (int i = 1; i < minHeap.size(); i++) {
            System.out.printf("%d ", minHeap.get(i));
            System.out.print("(p: ");
            System.out.printf("%.3f", priorities.get(minHeap.get(i)));
            System.out.print(") ");
            if (i == itemsUntilNext) {
                System.out.println();
                level++;
                itemsUntilNext += Math.pow(2, level);
                depth--;
                for (int j = 0; j < depth; j++) {
                    System.out.print(" ");
                }
            }
        }
        System.out.println();
    }

}
