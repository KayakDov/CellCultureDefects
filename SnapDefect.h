
/* 
 * File:   SnapDefect.h
 * Author: E. Dov Neimand
 *
 */

#ifndef SNAPDEFECT_H
#define SNAPDEFECT_H

#include <limits>
#include "Loc.h"
#include "SpaceTemp.h"
#include <sstream>

/**
 * A Defect at a moment in time, without history or future.
 * @param loc The location.
 * @param id The id.
 * @param charge The charge of the defect.
 * @param time The time this snapshot of the defect was taken.
 */
class SnapDefect: public SpaceTemp {
public:
    static const int NO_ID = std::numeric_limits<int>::max();
    
    /**
     * The constructor.
     * @param loc The location of this defect.
     * @param id The id of this defect.
     * @param charge The charge of this defect.
     */
    SnapDefect(const SpaceTemp& loc, int id, bool charge);
    
    /**
     * Will read a line from the file and build a SnapDefect from it.
     * @param file The file to have a line read from.
     */
    static SnapDefect fromLine(std::istream& file);
    
    SnapDefect(const SnapDefect& orig);
    virtual ~SnapDefect();
    
    /**
     * The ID.
     * @return The ID.
     */
    int getID() const;
    
    /**
     * The charge of the defect.
     * @return The charge of the defect.
     */
    bool getCharge() const;
    
    bool isTracked() const;
private:
    const int id;
    const bool charge;

};

#endif /* SNAPDEFECT_H */

