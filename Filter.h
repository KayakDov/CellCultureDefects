
/* 
 * File:   Filter.h
 * Author: E. Dov Neimand
 *
 * Created on 26 February 2024, 7:51
 */

#ifndef FILTER_H
#define FILTER_H

#include <iterator>
#include <vector>
#include "Defect.h"

/**
 * A filter for an iterator.  It filters out all values that do not meet the
 * filter criteria.
 */
template<typename Filter>
class FilteredIterator {
public:
    using iterator = std::vector<Defect*>::iterator;
    
    /**
     * A constructor.
     * @param iter The underlying iterator from which some values will be kept,
     * and other skipped over.
     * @param f A method that returns true for those elements to be iterated over
     * and false for those to be skipped.
     */
    
    FilteredIterator(iterator iter, iterator end, Filter f);
    FilteredIterator(const FilteredIterator& from);
    virtual ~FilteredIterator();
    
    /**
     * The end iterator, an iterator pointing to one past the last valid element.
     * @param end An iterator at the end of the underlying iterable set.
     * @return One after the last value in this iterable set.
     */
    FilteredIterator end();
    
    FilteredIterator& operator++();
    Defect* operator*() const;
    /**
     * The equals method only checks if the current element is equal.
     * @param other The other FilteredArray to be checked.
     * @return true if the current element is equal, false otherwise.
     */
    bool operator==(const FilteredIterator& other) const;
    bool operator!=(const FilteredIterator& other) const;
private:
    iterator orig, endIter;
    Filter filt;

};

#endif /* FILTER_H */

