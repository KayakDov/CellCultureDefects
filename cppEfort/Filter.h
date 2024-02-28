
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
#include <functional>

/**
 * A filter for an iterator.  It filters out all values that do not meet the
 * filter criteria.
 */
template<typename iterator>
class FilteredIterator {
public:

    /**
     * A constructor.
     * @param iter The underlying iterator from which some values will be kept,
     * and other skipped over.
     * @param f A method that returns true for those elements to be iterated over
     * and false for those to be skipped.
     */

    FilteredIterator(iterator begin, iterator end, const std::function<bool(Defect)>& predicate) : iter(begin), predicate(predicate), end(end) {
    }

    FilteredIterator(const FilteredIterator& from) : iter(from.iter), predicate(from.predicate), end(from.end) {
    }
    virtual ~FilteredIterator();

    /**
     * The end iterator, an iterator pointing to one past the last valid element.
     * @param end An iterator at the end of the underlying iterable set.
     * @return One after the last value in this iterable set.
     */
    FilteredIterator filterEnd() {
        return FilteredIterator<iterator>(end, end, predicate);
    }

    FilteredIterator& operator++() {
        while (iter != end && !predicate(*(*iter))) ++iter;
        return *this;
    }

    Defect* operator*() const {
        return *iter;
    }

    /**
     * The equals method only checks if the current element is equal.
     * @param other The other FilteredArray to be checked.
     * @return true if the current element is equal, false otherwise.
     */
    bool operator==(const FilteredIterator& other) const {
        return *iter == *(other.iter);
    }

    bool operator!=(const FilteredIterator& other) const {
        return !(*this == other);
    }

private:
    iterator iter, end;
    const std::function<bool(Defect)>& predicate;
};

//#include "Filter.cpp"

#endif /* FILTER_H */

