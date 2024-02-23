
/* 
 * File:   SpaceTemp.h
 * Author: E. Dov Neimand
 *
 */

#ifndef SPACETEMP_H
#define SPACETEMP_H
#include "Loc.h"
/**
 * A point in spacetime.
 * @param x The x vale.
 * @param y The y value.
 * @param t The time value.
 */
class SpaceTemp: public Loc {
public:
    /**
     * The constructor.
     * @param x The x value.
     * @param y The y value.
     * @param t The time value.
     */
    SpaceTemp(Loc loc, int t);
    SpaceTemp(const SpaceTemp& orig);
    virtual ~SpaceTemp();
    
    /**
     * The time of this location.
     * @return The time of this location.
     */
    int getTime() const;
    /**
     * Sets the time value of this location.
     * @param t The new time value.
     */
    void setTime(int t);
    
    /**
     * A distance that accounts for time.  Unfortunatly the way in which time
     * is accounted for is kind of arbitrary.  The spacial temporal distance
     * just adds the time difference to the spacial difference. 
     * @param st The spacial temporal point we want to know how far away it is.
     * @return A distance between this point and the proffered point.
     */
    double sTDist(const SpaceTemp& st) const;
    
    /**
     * Sets the time and location to that of the proffered spaceTemp.
     * @param st The spaceTemp whos values are to be copied.
     */
    void copy(const SpaceTemp& st);
    
    /**
     * Is this point near the proffered point.  This neighborhood is defined
     * by a cylinder with (x,y) as the circle and the third dimension t. 
     * @param st The proffered point.
     * @param distThreshhold st must be closer than distThreshold in the plane
     * in order to be considered near.
     * @param timeThreshold st must be closer in time to this than time threshold
     * to be considered near. 
     * @return true if they are within a cylinder neighborhood of each other.
     */
    bool near(const SpaceTemp& st, const double& distThreshhold, const double& timeThreshold) const;
private:
    int time;
};

#endif /* SPACETEMP_H */

