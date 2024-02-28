/* 
 * File:   DefectManager.cpp
 * Author: E. Dov Neimand
 * 
 */


#include "DefectManager.h"
#include <unordered_set>

Defect* DefectManager::getPair(const Defect& lonely, std::unordered_set<Defect*>& eligibles, const bool birth, double dist, int time) const {
    //TODO: There is no precaution yet taken in case the other baby/recently deceased already has a partner.


    Defect* closest = nullptr;
    for (Defect* eligible : eligibles) {
        if (lonely.near(eligible->snapShot(birth), dist, time) &&
                (closest == nullptr
                || closest->sTDist(lonely) > eligible->sTDist(lonely)))
            closest = eligible;
    }

    if (closest != nullptr) {
        eligibles.erase(closest);
        return closest;
    }
    return nullptr;
}

bool DefectManager::isNewDefect(const SnapDefect& sd) const {
    return sd.getID() >= (sd.getCharge() ? posDefects : negDefects).size();
}

std::ifstream DefectManager::reader() const {
    std::ifstream file(fileName);

    if (!file.is_open())
        throw std::runtime_error("Error: Unable to open file " + fileName);

    std::string firstLine;
    std::getline(file, firstLine);

    return file;
}

int DefectManager::fileLength() const {
    int length = 0;
    std::ifstream file = reader();
    std::string line;
    while (std::getline(file, line)) length++;
    return length;
}

void DefectManager::loadDefects() {
    clearDefects();
    endTime = 0;
    forEachLine([this](const SnapDefect & sd) {
        endTime++;
        if (sd.isTracked()) {
            std::vector<Defect*>& defects = sd.getCharge() ? posDefects : negDefects;
            if (isNewDefect(sd)) defects.push_back(new Defect(sd));
            else defects[sd.getID()]->update(sd);
            }
    });
}

void DefectManager::pairDefects(double dist, int time) {
    clearPairing();
    loadDefects();
    setThresholds(dist, time);

    std::unordered_set<Defect*> possibleBirths(negDefects.begin(), negDefects.end());
    std::unordered_set<Defect*> possibleDeaths = possibleBirths;

    for (Defect* lonely : posDefects) {
        lonely->setTwin(getPair(lonely->birthSnap(), possibleBirths, true, dist, time));
        lonely->setSpouse(getPair(*lonely, possibleDeaths, false, dist, time));
    }
}

DefectManager::DefectManager(const std::string& fileName) : fileName(fileName) {

}

DefectManager::~DefectManager() {
    clearDefects();
}

void DefectManager::setThresholds(int time, int dist) {
    this-> timeThreshold = time;
    this-> distThreshold = dist;
}

std::ostream& operator<<(std::ostream& os, const DefectManager& dm) {
    for (Defect* defect : dm.posDefects) {
        bool spouse = defect->hasSpouse(), twin = defect->hasTwin();
        if (spouse || twin) os << defect -> getID() << " has: \n";
        if (spouse) os << " spouse: " << defect->getSpouse() << "\n";
        if (twin) os << " twin: " << defect->getTwin() << "\n";

    }
    return os;
}

void DefectManager::forEachLine(std::function<void(const SnapDefect&) > processFunc) const {
    std::ifstream file = reader();

    while (!file.eof()) {
        SnapDefect sd = SnapDefect::fromLine(file);
        processFunc(sd);
    }

    file.close();
}

void DefectManager::removeFromFileUntrackedDefects() {
    std::ifstream fromFile = reader();

    std::string newFileName = "modified" + fileName;

    std::ofstream toFile(newFileName);

    std::string line;

    while (std::getline(fromFile, line)) {
        std::istringstream lineStream(line);
        SnapDefect sd = SnapDefect::fromLine(lineStream);
        if (sd.isTracked()) toFile << line << "\n";

    }

    fileName = newFileName;
}

void DefectManager::setFile(const std::string& fileName) {
    this->fileName = fileName;
    posDefects.clear();
    negDefects.clear();
    endTime = -1;
}

double DefectManager::percentTracked() const {
    int untracked = 0, tracked = 0;
    forEachLine([&tracked, &untracked](const SnapDefect & sd) {
        if (sd.isTracked()) tracked++;
        else untracked++;
    });
    return (double) tracked / (tracked + untracked);
}

int DefectManager::countPos(const Relationship& rel) const {
    int num = 0;
    for (Defect* def : posDefects) {
        bool add = false;

        switch (rel) {
            case Relationship::SPOUSE: add = def->hasSpouse();
                break;
            case Relationship::TWIN: add = def->hasTwin();
                break;
            case Relationship::SPOUSE_AND_TWIN: add = def->hasTwin() && def->hasSpouse();
                break;
            case Relationship::NONE: add = !def->hasSpouse() && !def->hasTwin();
                break;
            case Relationship::ALL: add = true;
                break;
        }

        if (add) num++;

    }
    return num;
}

void DefectManager::clearDefects() {
    for (auto& defect : posDefects) delete defect;
    for (auto& defect : negDefects) delete defect;
    posDefects.clear();
    negDefects.clear();
}

void DefectManager::clearPairing() {
    for (auto& defect : posDefects) defect->clearPairs();
    for (auto& defect : negDefects) defect->clearPairs();
}

int DefectManager::numPositiveDefect() const {
    return posDefects.size();
}

int DefectManager::numNegativeDefects() const {
    return negDefects.size();
}

TwoDefectIters DefectManager::AllDefects::begin() {
    return TwoDefectIters(dm.posDefects.begin(), dm.negDefects.begin(), dm.posDefects.end());
}

TwoDefectIters DefectManager::AllDefects::end() {
    return TwoDefectIters::end(dm.posDefects.end(), dm.negDefects.end());
}

DefectManager::AllDefects::AllDefects(DefectManager& dm) : dm(dm) {
};

DefectManager::AllDefects DefectManager::all() {
    return AllDefects(*this);
}

template<typename iterator>
DefectManager::FilterDefects<iterator>::FilterDefects(const std::function<bool(Defect)>& predicate, std::vector<Defect*>* pos, std::vector<Defect*>* neg)
        :predicate(predicate), pos(pos), neg(neg){}

template<typename iterator>
FilteredIterator<iterator> DefectManager::FilterDefects<iterator>::begin(){
    if(pos != nullptr && neg != nullptr) 
        return FilteredIterator<iterator>(
                TwoDefectIters(pos->begin(), neg->begin(), pos->end()), 
                TwoDefectIters::end(pos->end(), neg->end()), 
                predicate);
    if(pos != nullptr) return FilteredIterator<iterator>(pos->begin(), pos->end(), predicate);
    if(neg != nullptr) return FilteredIterator<iterator>(neg->begin(), neg->end(), predicate);
    else throw std::invalid_argument("At least one of the vectors passed must not be a nullptr.");
}

template<typename iterator>
FilteredIterator<iterator> DefectManager::FilterDefects<iterator>::end(){
    if(pos != nullptr && neg != nullptr) return FilteredIterator<iterator>(
            TwoDefectIters::end(pos->end(), neg->end()), 
            TwoDefectIters::end(pos->end(), neg->end()), 
            predicate);
    if(pos != nullptr) return FilteredIterator<iterator>(pos->end(), pos->end(), predicate);
    if(neg != nullptr) return FilteredIterator<iterator>(neg->end(), neg->end(), predicate);
    else throw std::invalid_argument("At least one of the vectors passed must not be a nullptr.");
}

DefectManager::FilterDefects<TwoDefectIters> DefectManager::all(const std::function<bool(Defect)>& predicate){
    return FilterDefects<TwoDefectIters>(predicate, &posDefects, &negDefects);
}

DefectManager::FilterDefects<std::vector<Defect*>::iterator> DefectManager::positive(const std::function<bool(Defect)>& predicate){
    return FilterDefects<std::vector<Defect*>::iterator>(predicate, &posDefects, nullptr);
}

DefectManager::FilterDefects<std::vector<Defect*>::iterator> DefectManager::negative(const std::function<bool(Defect)>& predicate){
    return FilterDefects<std::vector<Defect*>::iterator>(predicate, nullptr, &negDefects);
}
