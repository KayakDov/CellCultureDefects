
/* 
 * File:   StatisticalTools.h
 * Author: E. Dov Neimand
 *
 
 */

#ifndef STATISTICALTOOLS_H
#define STATISTICALTOOLS_H
#include <iterator> // Include the <iterator> header for std::distance
#include <functional>

template<typename Iterator>
class StatisticalTools {
public:
    StatisticalTools(Iterator first,Iterator last);
    StatisticalTools(const StatisticalTools& orig);
    virtual ~StatisticalTools();
    
    double mean() const;
    
    double mean(const std::function<double(double)>& f) const;
    
    double variance(const std::function<double(double)>& f) const;
    
private:
    const Iterator first, last;

};

// Include template function definitions here
#include "StatisticalTools.tpp"

#endif /* STATISTICALTOOLS_H */

