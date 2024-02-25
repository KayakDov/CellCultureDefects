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
 * An iterator that goes through all the defects.
 */
class TwoDefectIters : public std::iterator<std::forward_iterator_tag, Defect*> {
public:
    using iterator = std::vector<Defect*>::iterator;
    
    TwoDefectIters(iterator first, iterator second, iterator firstEnd);
    static TwoDefectIters end(iterator firstEnd, iterator secondEnd);
    TwoDefectIters& operator++();
    Defect* operator*() const;
    bool operator==(const TwoDefectIters& other) const;
    bool operator!=(const TwoDefectIters& other) const;
private:
    iterator first, second, firstEnd;

};

#endif /* TWOITERS_H */

