package ru.ifmo.ctddev.berinchik.arrayset;

import java.util.*;

/**
 * Created by vadim on 20.02.17.
 */
public class ArraySet<E> extends AbstractSet<E> implements NavigableSet<E> {
    private ReversibleList<E> list;
    private final Comparator <? super E> comparator;
    private boolean wasComp;

    @SuppressWarnings("unchecked")
    private int compare(Object o1, Object o2) {
        return comparator.compare((E) o1, (E) o2);
    }

    public ArraySet() {
        this(Collections.emptyList(), null);
    }

    public ArraySet(Comparator <? super E> comp) {
        this(Collections.emptyList(), comp);
    }

    public ArraySet(Collection<? extends E> c) {
        this(c, null);
    }

    public ArraySet(Collection <? extends E> c, Comparator <? super E> comp) {
        if (comp != null) {
            comparator = comp;
            wasComp = true;
        } else {
            comparator = (o1, o2) -> ((Comparable<E>) o1).compareTo(o2);
            wasComp = false;
        }
        SortedSet<E> set = new TreeSet<>(comp);
        set.addAll(c);
        List<E> realList = new ArrayList<>(set);
        list = new ReversibleList<>(realList, false);
    }

    private ArraySet(ReversibleList<E> list, Comparator <? super E> comp) {
        this.list = list;
        comparator = comp;
    }

    private int binarySearch(E o) {
        int ret = Collections.binarySearch(list, o, comparator);
        return Collections.binarySearch(list, o, comparator);
    }

    private boolean inRange(int pos) {
        return pos >= 0 && pos < size();
    }

    private int lowerIndex(E o) {
        int pos = binarySearch(o);
        return pos < 0 ? ~pos - 1 : pos - 1;
    }

    private int higherIndex(E o) {
        int pos = binarySearch(o);
        return pos < 0 ? ~pos : pos + 1;
    }

    private int floorIndex(E o) {
        int pos = binarySearch(o);
        return pos < 0 ? ~pos - 1 : pos;
    }

    private int ceilingIndex(E o) {
        int pos = binarySearch(o);
        return pos < 0 ? ~pos : pos;
    }

    @Override
    public E lower(E e) {
        int index = lowerIndex(e);
        return inRange(index) ? list.get(index) : null;
    }

    @Override
    public E floor(E e) {
        int index = floorIndex(e);
        return inRange(index) ? list.get(index) : null;
    }

    @Override
    public E ceiling(E e) {
        int index = ceilingIndex(e);
        return inRange(index) ? list.get(index) : null;
    }

    @Override
    public E higher(E e) {
        int index = higherIndex(e);
        return inRange(index) ? list.get(index) : null;
    }

    @Override
    public E pollFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E pollLast() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableSet<E> descendingSet() {
        ReversibleList<E> newList = new ReversibleList<>(list.realList, !list.revFlag);
        return new ArraySet(newList, Collections.reverseOrder(comparator));
    }

    @Override
    public Spliterator<E> spliterator() {
        return Collections.unmodifiableList(list).spliterator();
    }

    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        } else {
            return list.get(0);
        }
    }

    @Override
    public E last() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        } else {
            return list.get(size() - 1);
        }
    }

    @Override
    public Iterator<E> iterator() {
        return Collections.unmodifiableList(list).iterator();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        return binarySearch((E) o) >= 0;
    }

    @Override
    public Iterator<E> descendingIterator() {
        return descendingSet().iterator();
    }

    @Override
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        if (compare(fromElement, toElement) > 0) {
            throw new IllegalArgumentException("fromElement is greater than toElement'");
        }
        int from = fromInclusive ? ceilingIndex(fromElement) : higherIndex(fromElement);
        int to = toInclusive ? floorIndex(toElement) : lowerIndex(toElement);

        return new ArraySet(list.subList(from, Math.max(from, to + 1)), comparator);
    }

    @Override
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        try {
            return subSet(first(), true, toElement, inclusive);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return new ArraySet<>(Collections.emptyList(), comparator);
        }
    }

    @Override
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        try {
            return subSet(fromElement, inclusive, last(), true);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return new ArraySet<>(Collections.emptyList(), comparator);
        }
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        try {
            return headSet(toElement, false);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return new ArraySet<>(Collections.emptyList(), comparator);
        }
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        try {
            return tailSet(fromElement, true);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return new ArraySet<>(Collections.emptyList(), comparator);
        }
    }

    @Override
    public Comparator<? super E> comparator() {
        return wasComp ? comparator : null;
    }

    public int size() {
        return list.size();
    }

    private class ReversibleList<T> extends AbstractList implements RandomAccess {
        List<T> realList;
        boolean revFlag;

        private ReversibleList(List<T> realList, boolean revFlag) {
            this.realList = realList;
            this.revFlag = revFlag;
        }

        @Override
        public T get(int index) {
            return revFlag ? realList.get(size() - 1 - index) : realList.get(index);
        }

        @Override
        public Iterator<T> iterator() {
            if (revFlag) {
                return new Itr<>(realList.listIterator(realList.size()));
            } else {
                return realList.iterator();
            }
        }

        @Override
        public int size() {
            return realList.size();
        }

        @Override
        public ReversibleList<T> subList(int fromIndex, int toIndex) {
            return new ReversibleList<>(realList.subList(fromIndex, toIndex), revFlag);
        }

        private class Itr<T> implements Iterator {
            ListIterator<T> realItr;

            Itr(ListIterator<T> realItr) {
                this.realItr = realItr;
            }

            @Override
            public void remove() {
                realItr.remove();
            }

            @Override
            public boolean hasNext() {
                return realItr.hasPrevious();
            }

            @Override
            public T next() {
                return realItr.previous();
            }
        }
    }
}
