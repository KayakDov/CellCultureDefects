///* 
// * File:   Filter.cpp
// * Author: E. Dov Neimand
// * 
// * Created on 26 February 2024, 7:51
// */
//
//#include "Filter.h"
//
//
//template<typename iterator>
//FilteredIterator<iterator>::FilteredIterator(iterator begin, iterator end, const std::function<bool(Defect)>& predicate): begin(begin), predicate(predicate), end(end) {
//}
//
//template<typename iterator>
//FilteredIterator<iterator>::FilteredIterator(const FilteredIterator& from): begin(from.begin), predicate(from.predicate), end(from.end) {
//}
//
//template<typename iterator>
//FilteredIterator<iterator>::~FilteredIterator() {
//}
//
//template<typename iterator>
//FilteredIterator<iterator>& FilteredIterator<iterator>::operator ++(){
//    while(begin != end && !predicate(*(*begin))) ++begin;
//    return *this;
//} 
//template<typename iterator>
//Defect* FilteredIterator<iterator>::operator *() const{
//    return *begin;
//}
//
//template<typename iterator>
//bool FilteredIterator<iterator>::operator ==(const FilteredIterator<iterator>& other) const{
//    return *begin == *(other.begin);
//}
//
//template<typename iterator>
//bool FilteredIterator<iterator>::operator!=(const FilteredIterator<iterator>& other) const{
//    return !(*this == other);
//}
//
//template<typename iterator>
//FilteredIterator<iterator> FilteredIterator<iterator>::filterEnd(){
//    return FilteredIterator<iterator>(end, end, predicate);
//}