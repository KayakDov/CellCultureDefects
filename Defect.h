
/* 
 * File:   Defect.h
 * Author: E. Dov Neimand
 *
 * Describes a defect.
 */

#ifndef DEFECT_H
#define DEFECT_H

#include <string>
#include "Loc.h"
#include "SnapDefect.h"
/**
 * A defect, tracked over time.  
 * @param sd A defect at one moment in time.
 */
class Defect: public SnapDefect {
public:

    /**
     * Constructor
     * @param The SnapDefect that gives birth to this one.
     */
    Defect(const SnapDefect& sd);

    /**
     * Constructor
     * @param sd The SnapDefect that gives birth to this one.
     * @param twin The oppositely charged defect created with this one.
     * false otherwise.
     */
    Defect(const SnapDefect& sd, Defect* twin);

    
    /**
     * Was there another oppositely charged Defect born in close proximity?
     * @return True if another oppositely charged defect was born in close 
     * proximity.
     */
    bool hasTwin() const;

    /**
     * Has this defect been recognized as having fused with another defect.
     * @return True if this defect is believed to have fused with another 
     * defect.
     */
    bool hasSpouse() const;

    /**
     * Sets another defect as having been created together with this charge.
     * @param twin The charge that was created with this charge.
     */
    void setTwin(Defect* twin);

    /**
     * Sets another charge as having fused with this charge.
     * @param spouse The charge that has fused with this charge.
     */
    void setSpouse(Defect* spouse);
    
    /**
     * The birthday
     * @return The birthday
     */
    const SpaceTemp* getBirth() const;
        
    /**
     * Gets a SnapDefect of the birth.  That is, a snap shot in time of this 
     * defect at birth.
     * @return A SnapDeefect of the birth.
     */
    const SnapDefect birthSnap() const;
    
    /**
     * Either the birth SnapDefect, or the last seen Snap Defect.
     * @param birth True if the moment of birth is desired, false if the time 
     * last updated is required.
     * @return The moment of birth or the time last seen.
     */
    const SnapDefect snapShot(bool birth) const;
    
    /**
     * The twin of this defect.
     * @return The ID of twin of this defect.
     */
    int getTwin() const;
    
    /**
     * The ID of the spouse of this defect.
     * @return The ID of the spouse of this defect.
     */
    int getSpouse() const;
    
    /**
     * Overload of the output stream operator (<<) to output a Defect object to an ostream.
     * 
     * This function overloads the output stream operator (<<) to allow easy printing of Defect objects
     * to an output stream, such as std::cout.
     * 
     * @param os The output stream to which the Defect object will be printed.
     * @param defect The Defect object to be printed.
     * @return A reference to the output stream after printing the Defect object.
     */
    friend std::ostream& operator<<(std::ostream& os, const Defect& def);
    
    /**
     * Resets this Defect to have no pairs.
     */
    void clearPairs();


    /**
     * The age of this defect.
     * @return 
     */
    int age() const;
private:
    int twinID = SnapDefect::NO_ID, spouseID = SnapDefect::NO_ID;
    const SpaceTemp birth; 
};

#endif /* DEFECT_H */

