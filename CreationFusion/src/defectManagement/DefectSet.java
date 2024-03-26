package defectManagement;

import SnapManagement.Defect;
import SnapManagement.NegDefect;
import SnapManagement.PosDefect;
import snapDefects.PosSnapDefect;
import snapDefects.SnapDefect;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;
import snapDefects.NegSnapDefect;

/**
 * A set for defects. Adding unique SnapDefects can be done concurrently. That
 * is, So long as any two SnapDefects have different times or different ids then
 * they can be added concurrently.
 *
 * @author E. Dov Neimand
 * @param <T> Either positive or negative defects
 */
public abstract class DefectSet<T extends Defect> implements Collection<T> {

    private int size = 0;
    private final Lock[] locks;

    /**
     * Sets an element of the underlying array.
     *
     * @param i The index of the element to be set.
     * @param defect The defect to be placed at the index.
     */
    protected abstract void set(int i, T defect);

    /**
     * returns the defect at the given id.
     *
     * @param id An ID.
     * @return The defect with the given ID.
     */
    protected abstract T get(int i);

    /**
     * The charge of the elements in this collection.
     *
     * @return The charge of the elements in this collection.
     */
    public abstract boolean charge();

    /**
     * The length of the underlying array,
     *
     * @return The length of the underlying array,
     */
    protected abstract Defect[] array();

    /**
     * Constructor.
     *
     * @param maxSize The max size of this set.
     */
    public DefectSet(int maxSize) {
        locks = new ReentrantLock[maxSize];
        Arrays.setAll(locks, i -> new ReentrantLock(false));
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        if (!(o instanceof Defect)) return false;
        else return contains((Defect) o);
    }

    /**
     * Does this set contain a defect with the id and charge of the proffered
     * defect.
     *
     * @param def The desired defect.
     * @return True if the set contains it, false otherwise.
     */
    public boolean contains(Defect def) {
        return get(def.getID()) != null
                && get(def.getID()).getCharge() == def.getCharge();
    }

    /**
     * The first node in the set.
     *
     * @return The first node in the set.
     */
    private int getNext(int start) {
        for (int i = start; i < array().length; i++)
            if (get(i) != null) return i;
        return array().length;
    }

    @Override
    public Iterator<T> iterator() {

        return new Iterator<T>() {
            int next = getNext(0);

            @Override
            public boolean hasNext() {
                return next < array().length;
            }

            @Override
            public T next() {
                int current = next;
                next = getNext(current + 1);
                return get(current);
            }
        };
    }

    @Override
    public Object[] toArray() {
        return Arrays.stream(array()).filter(def -> def != null).toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        System.arraycopy(array(), 0, ts, 0, Math.min(array().length, ts.length));
        return ts;
    }

    /**
     * Throws an exception if the charge parameter does not match the charge
     * field.
     *
     * @param charge
     */
    void testCharge(hasChargeID charged) {
        if (charged.getCharge() != charge())
            throw new RuntimeException("You're trying "
                    + "to add a " + charged.getCharge() + " charge to a " + charge()
                    + " set.");
    }

    /**
     * Returns true if added without over writing anything.
     *
     * @param e The element to add.
     * @return Returns true if added without over writing anything.
     */
    @Override
    public boolean add(T e) {
        testCharge(e);
        boolean vacant = !has(e);
        if (vacant) size++;

        set(e.getID(), e);
        return vacant;
    }

    /**
     * Is this id present in this set.
     *
     * @param id The desired ID.
     * @return True if the ID is present in the set, false otherwise.
     */
    private boolean has(hasChargeID id) {
        return get(id.getID()) != null;
    }

    /**
     * returns the defect at the given id.
     *
     * @param id A value with an id.
     * @return The defect with an id the same as the one passed, or null if
     * there is none.
     */
    public T get(hasChargeID id) {
        return get(id.getID());
    }

    /**
     * Adds a snap defect to this set. If there is no corresponding defect, then
     * one is created and added. If there is a corresponding defect then
     *
     * @param sd The snapDefect to be added.
     */
    public void add(SnapDefect sd) {

        testCharge(sd);

        if (has(sd)) {
            if (get(sd).followingLifeCourse()) get(sd).addLifeSnap(sd);
            else get(sd).updateBirthDeath(sd.loc);
        } else addNew(sd);

    }

    /**
     * Creates and inserts a defect matching the SnapDefect. If another snap
     * defect with the same ID is inserted at the same time, it'll have to wait,
     * and then be redirected back to add(SnapDefect sd).
     *
     * @param sd The SnapDefect from which a new defect is created and inserted.
     */
    private void addNew(SnapDefect sd) {

        locks[sd.getID()].lock();
        try {
            if (!has(sd)) {
                if (sd.getCharge()) add((T) new PosDefect(sd));
                else add((T) new NegDefect(sd));
            } else add(sd);
        } finally {
            locks[sd.getID()].unlock();
        }

    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    /**
     * removes a defect.
     *
     * @param def The defect to be removed.
     * @return True if teh defect was present. False otherwise.
     */
    public boolean remove(Defect def) {
        boolean has = get(def.getID()) != null;
        if (!has) return false;
        size--;
        set(def.getID(), null);
        return true;
    }

    @Override
    public void clear() {
        size = 0;
        Arrays.fill(array(), null);
    }

    @Override
    public boolean containsAll(Collection<?> clctn) {
        return clctn.stream().allMatch(item -> contains(item));
    }

    @Override
    public boolean addAll(Collection<? extends T> clctn) {
        boolean alreadyHas = clctn.stream().anyMatch(item -> contains(item));
        clctn.forEach(item -> add(item));
        return !alreadyHas;
    }

    @Override
    public boolean removeAll(Collection<?> clctn) {
        boolean hasAll = containsAll(clctn);
        clctn.forEach(item -> remove(item));
        return hasAll;
    }

    @Override
    public boolean retainAll(Collection<?> clctn) {
        boolean hasAll = containsAll(clctn);
        stream().filter(def -> !clctn.contains(def)).forEach(def -> remove(def));
        return hasAll;
    }

    @Override
    public Stream<T> stream() {
        return Arrays.stream(array()).filter(def -> def != null) .map(def ->(T)def);
    }

    /**
     * Some tests for this class.
     *
     * @param args
     */
    public static void main(String[] args) {
        DefectSet ds = new PosDefectSet(4);

        ds.add(new PosSnapDefect(0, 0, 3, 2, .5));
        ds.add(new PosSnapDefect(0, 0, 2, 3, .5));

        Iterator<Defect> iter = ds.iterator();

        while (iter.hasNext()) System.out.println(iter.next().getID());
    }

}
