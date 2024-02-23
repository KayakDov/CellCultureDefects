
/* 
 * File:   StatisticalTools.h
 * Author: E. Dov Neimand
 *
 
 */

#ifndef STATISTICALTOOLS_H
#define STATISTICALTOOLS_H
#include <iterator> // Include the <iterator> header for std::distance


template<typename Iterator>
class StatisticalTools {
public:
    StatisticalTools(Iterator first,Iterator last);
    StatisticalTools(const StatisticalTools& orig);
    virtual ~StatisticalTools();
    
    double mean() const;

    template<typename UnaryFunc>
    double mean(const UnaryFunc& f) const;
    
    template<typename UnaryFunc>
    double variance(const UnaryFunc& f) const;
    
private:
    const Iterator first, last;

};

// Include template function definitions here
#include "StatisticalTools.tpp"

#endif /* STATISTICALTOOLS_H */

