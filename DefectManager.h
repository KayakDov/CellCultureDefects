
/* 
 * File:   DefectManager.h
 * Author: E. Dov Neimand
 *
 */

#ifndef DEFECTMANAGER_H
#define DEFECTMANAGER_H

#include <unordered_map>  // Include the unordered_map header
#include <fstream>         // Include the fstream header for std::ifstream
#include <string>          // Include the string header for std::string
#include "Defect.h"
#include "Loc.h"
#include <stdexcept>
#include <fstream>
#include <functional>
#include <unordered_set>
#include <vector>

/**
 * This class creates and tracks defects.
 */
class DefectManager {
public:

    /**
     * Recomended defaault values for cells based on experimental observations.
     */
    static constexpr int DEFAULT_DIST_THRESH = 40, DEFAULT_TIME_THRESH = 4;
    

    /**
     * The constructor.  This method will iterate thought the proffered file, and 
     * track all the paired defects.  Defects can be printed with the << operator.
     * @param fileName The local path of the data file.
     * them to be paired.
     */
    DefectManager(const std::string& fileName);

    virtual ~DefectManager();

    /**
     * Performs a method for each line in the file mapped to a SnapDefect.
     * @param processFunc The method performed.
     */
    void forEachLine(std::function<void(const SnapDefect&) > processFunc) const;

    /**
     * Print a list of defects and their creation and annihilation partners.
     */
    friend std::ostream& operator<<(std::ostream& os, const DefectManager& dm);


    /**
     * Loads al the defects from the file. (Be sure it's correctly it's formatted.)
     */
    void loadDefects();

    /**
     * Matches all the defects have the correct twins and spouses.
     * @param timeThreshold  The temporal proximity required of two births or deaths
     * for them to be paired.
     * @param distThreshold The distance required between two births or deaths for
     */
    void pairDefects(double distThreshold = DEFAULT_DIST_THRESH, int timeThreshold = DEFAULT_TIME_THRESH);
    
    /**
     * Without changing the file, it is replaced for this object by an identical 
     * one without untracked defects.
     */
    void removeFromFileUntrackedDefects();
    
    /**
     * The percentage of lines that are tracked defects.
     * @return The percentage of lines that are tracked defects.
     */
    double percentTracked() const;
    
    
    enum class Relationship {SPOUSE, TWIN, SPOUSE_AND_TWIN, NONE, ALL, ELIGIBLE};
    /**
     * The number of a type of defect.
     * @return The number of paired defects.
     */
    int countPos(const Relationship& rel) const;
    
    /**
     * The average life span of the defects.
     * @return The average life span of the defects.
     */
    double averageLifeSpan() const;
    
    /**
     * The standard deviation of the lifespan of the defects.
     * @return 
     */
    double standardDeviationLifeSpan() const;
    
private:
    std::vector<Defect*> posDefects, negDefects;
    std::string fileName;
    int endTime = -1;
    int distThreshold, timeThreshold;

    /**
     * Tries to finds the creation or fusion pair of the personOfInterest
     * @param lonely A defect for whom a pair is sought.  If a birth pair is 
     * sought, this should be a snapshot of the defect from creation.  If 
     * a fusion pair is sought this can be the defect at time last seen.
     * @param eligables A set of eligible pairs to chose from.
     * @param birth True for a creation pair, false for a fusion pair.
     * @param time The temporal proximity required of two births or deaths
     * for them to be paired.
     * @param dist The distance required between two births or deaths for
     * @retreturn returns the pair.  If there is none, then a nullptr is 
     * returned.
     */
    Defect* getPair(const Defect& lonely, std::unordered_set<Defect*>& eligibles, const bool birth, double dist, int time) const;

    /**
     * Has the proffered defect been seen before?
     * @param sd The defect to be searched for in history.
     * @return True if the defect has been seen before, false otherwise.
     */
    bool isNewDefect(const SnapDefect& sd) const;

    /**
     * Sets the distance and time thresholds for creation and annihilation pairing.
     * @param time The new time threshold.
     * @param dist The new distance threshold.
     */
    void setThresholds(int time, int dist);
    
    
    /**
     * Offers up a prepared reader for the file, assuming the file is formatted 
     * correctly.
     * @return A reader for the file.
     */
    std::ifstream reader() const;
    
    /**
     * The number of lines in the file.
     * @return The number of lines in the file.
     */
    int fileLength() const;
    
    /**
     * Clears stored defects and frees up memory.
     */
    void clearDefects();
    
    /**
     * Clears all spouses and twins.
     */
    void clearPairing();
    
    
    /**
     * Sets a new file.  This clears the defects and endTime.
     * @param fileName The name of the new file.
     */
    void setFile(const std::string& fileName);
    
};

#endif /* DEFECTMANAGER_H */
