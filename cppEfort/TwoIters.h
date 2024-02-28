/*
/* 
 * File:   TwoIters.h
 * Author: E. Dov Neimand
 *
 * Created on 25 February 2024, 19:29
 */

#ifndef TWOITERS_H
#define TWOITERS_H
#include <iterator>
#include "Defect.h"
#include <vector>

/**
 * An iterator that concatenates two other iterators.
 */
class TwoDefectIters : public std::iterator<std::forward_iterator_tag, Defect*> {
public:
    using iterator = std::vector<Defect*>::iterator;
    
    /**
     * The constructor, a concatenation of two iterators.
     * @param first
     * @param second
     * @param firstEnd
     */
    TwoDefectIters(iterator first, iterator second, iterator firstEnd);
    
    /**
     * An end iterator for this iterator.
     * @param firstEnd The end of the first iterator of the two concatenated.
     * @param secondEnd The end of the second iterator of the two concatenated.
     * @return An iterator that is at the end of the iterable set.
     */
    static TwoDefectIters end(iterator firstEnd, iterator secondEnd);
    
    TwoDefectIters& operator++();
    Defect* operator*() const;
    bool operator==(const TwoDefectIters& other) const;
    bool operator!=(const TwoDefectIters& other) const;
private:
    iterator first, second, firstEnd;

};

#endif /* TWOITERS_H */

