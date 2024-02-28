/* 
 * File:   Filter.cpp
 * Author: E. Dov Neimand
 * 
 * Created on 26 February 2024, 7:51
 */

#include "Filter.h"


template<typename Filter>
FilteredIterator<Filter>::FilteredIterator(iterator iter, iterator end, Filter f): orig(iter), filt(f), endIter(end) {
}


template<typename Filter>
FilteredIterator<Filter>::FilteredIterator(const FilteredIterator& from): orig(from.orig), filt(from.filt), endIter(from.endIter) {
}


template<typename Filter>
FilteredIterator<Filter>::~FilteredIterator() {
}


template<typename Filter>
FilteredIterator<Filter>& FilteredIterator<Filter>::operator ++(){
    while(orig != endIter && !filt(*orig)) orig++;
    return *this;
} 


template<typename Filter>
Defect* FilteredIterator<Filter>::operator *() const{
    return *orig;
}


template<typename Filter>
bool FilteredIterator<Filter>::operator ==(const FilteredIterator& other) const{
    return *orig == *(other.orig);
}


template<typename Filter>
bool FilteredIterator<Filter>::operator!=(const FilteredIterator& other) const{
    return !(*this == other);
}


template<typename Filter>
FilteredIterator<Filter> FilteredIterator<Filter>::end(){
    return FilteredIterator(end, end, filt);
}