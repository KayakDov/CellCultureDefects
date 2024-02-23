
/* 
 * File:   Loc.h
 * Author: edov
 *
 * Created on 16 February 2024, 10:27
 */

#ifndef LOC_H
#define LOC_H

#include <ostream>
#include "Loc.h"

/**
 * A location in the coordinate plane.
 */
class Loc {
public:
    
    static constexpr double equalityThreshold = 1e-10;

    Loc();
    
    /**
     * The constructor;
     * @param x The x value.
     * @param y The y value.
     */
    Loc(double x, double y);
    Loc(const Loc& orig);
    virtual ~Loc();
    
    /**
     * The distance planar distance to another point. 
     * @param loc The proffered point.
     * @return The distance between another point and this one
     */
    double dist(const Loc& loc) const;
    /**
     * The x location.
     * @return The x location.
     */
    double getX() const;
    /**
     * The y location.
     * @return The y location.
     */
    double getY() const;
    
    /**
     * Sets the x and y values.
     * @param x The x location.
     * @param y The y location.
     */
    void set(double x, double y);
    
    friend std::ostream& operator << (std::ostream& os, const Loc& loc);
    friend bool operator == (const Loc &x, const Loc &y);
private:
    double x, y;
};

#endif /* LOC_H */

