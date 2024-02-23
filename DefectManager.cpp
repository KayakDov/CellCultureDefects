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
    for (Defect* eligible : eligibles){
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
    return posDefects.find(sd.getID()) == posDefects.end();
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
            if (isNewDefect(sd)) (sd.getCharge() ? posDefects : negDefects)
                    .emplace(sd.getID(), new Defect(sd));
            else posDefects.find(sd.getID())->second->copy(sd);
            }
    });

}

void DefectManager::pairDefects(double dist, int time) {
    if (posDefects.size() == 0 && negDefects.size() == 0) loadDefects();
    else clearPairing();

    std::unordered_set<Defect*> births;
    std::unordered_set<Defect*> deaths;
    births.reserve(negDefects.size());
    deaths.reserve(negDefects.size());
    for (auto pair : negDefects) {
        Defect& nd = *(pair.second);
        if (nd.birthSnap().getTime() > time) births.insert(&nd);
        if (endTime - nd.getTime() > time) deaths.insert(&nd);
    }

    for (const auto& pair : posDefects) {
        Defect& lonely = *(pair.second);
        lonely.setTwin(getPair(lonely.birthSnap(), births, true, dist, time));
        lonely.setSpouse(getPair(lonely, deaths, false, dist, time));
    }
}

DefectManager::DefectManager(const std::string& fileName) : fileName(fileName) {

}

DefectManager::~DefectManager() {
    clearDefects();
}

std::ostream& operator<<(std::ostream& os, const DefectManager& dm) {
    for (auto iter = dm.posDefects.begin(); iter != dm.posDefects.end(); iter++) {
        bool spouse = iter->second->hasSpouse(), twin = iter->second->hasTwin();
        if (spouse || twin) os << iter -> second -> getID() << " has: \n";
        if (spouse) os << " spouse: " << iter->second->getSpouse() << "\n";
        if (twin) os << " twin: " << iter->second->getTwin() << "\n";

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

int DefectManager::count(const Relationship& rel) const {
    int num = 0;
    for (const auto& pair : posDefects) {
        bool add = false;
        Defect& def = *pair.second;
        switch (rel) {
            case Relationship::SPOUSE: add = def.hasSpouse();
                break;
            case Relationship::TWIN: add = def.hasTwin();
                break;
            case Relationship::SPOUSE_AND_TWIN: add = def.hasTwin() && def.hasSpouse();
                break;
            case Relationship::NONE: add = !def.hasSpouse() && !def.hasTwin();
                break;
            case Relationship::ALL: add = true;
        }
        if (add) num++;

    }
    return num;
}

void DefectManager::clearDefects() {
    for (auto& pair : posDefects) delete pair.second;
    for (auto& pair : negDefects) delete pair.second;
    posDefects.clear();
    negDefects.clear();
}

void DefectManager::clearPairing() {
    for (auto& pair : posDefects) pair.second->clearPairs();
    for (auto& pair : negDefects) pair.second->clearPairs();
}