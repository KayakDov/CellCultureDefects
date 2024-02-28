
/* 
 * File:   StatisticalTools.tpp
 * Author: E. Dov Neimand
 * 
 */

#include <numeric>
#include <algorithm>
#include <stdexcept> // Include for std::invalid_argument
#include "StatisticalTools.h"

template<typename Iterator>
StatisticalTools<Iterator>::StatisticalTools(Iterator first, Iterator last): first(first), last(last){
}

template<typename Iterator>
StatisticalTools<Iterator>::StatisticalTools(const StatisticalTools& orig) {
}

template<typename Iterator>
StatisticalTools<Iterator>::~StatisticalTools() {
}

template<typename Iterator>
double StatisticalTools<Iterator>::mean() const{
    return (double)(std::accumulate(first, last, 0))/std::distance(first, last);
}

template<typename Iterator>
double StatisticalTools<Iterator>::mean(const std::function<double(double)>& f) const{

    if(first == last) throw std::invalid_argument("Empty range provided.");

    int count = 0; double sum = 0;
    for(Iterator iter = first; iter != last; iter++, count ++) sum += f(*iter);
    
    return sum/count;
}

template<typename Iterator>
double  StatisticalTools<Iterator>::variance(const std::function<double(double)>& f) const{
    double avg = mean(f);
    
    
    return mean([avg, f](double d){
        double dif = f(d) - avg;
        return dif*dif;
    });
}