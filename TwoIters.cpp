
/* 
 * File:   TwoIters.cpp
 * Author: E. Dov Neimand
 * 
 * Created on 25 February 2024, 19:29
 */

#include "TwoIters.h"


TwoDefectIters& TwoDefectIters::operator++(){
    ++(first != firstEnd? first: second);
    return *this;
}

Defect* TwoDefectIters::operator*() const{
    return *(first!= firstEnd? first:second);
}

bool TwoDefectIters::operator==(const TwoDefectIters& other) const{
    return other.first == first && other.second == second;
}

bool TwoDefectIters::operator!=(const TwoDefectIters& other) const{
    return !(other == *this);
}

TwoDefectIters::TwoDefectIters(iterator posIter, iterator negIter, iterator posIterEnd)
:first(posIter), second(negIter), firstEnd(posIterEnd){}

TwoDefectIters TwoDefectIters::end(iterator firstEnd, iterator secondEnd) {
    return TwoDefectIters(firstEnd, secondEnd, firstEnd);
}