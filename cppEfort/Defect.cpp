
#include "Defect.h"
#include <cmath> // For std::sqrt

// Constructor with all parameters

Defect::Defect(const SnapDefect& sd)
: SnapDefect(sd), birth(*this), twinID(NO_ID), spouseID(NO_ID) {
}

Defect::Defect(const SnapDefect& sd, Defect* twin)
: SnapDefect(sd), birth(sd), spouseID(NO_ID) {
    setTwin(twin);
}

bool Defect::hasTwin() const {
    // Check if a twin defect is set
    return twinID != NO_ID;
}

bool Defect::hasSpouse() const {
    // Check if a spouse defect is set
    return spouseID != NO_ID;
}

void Defect::setTwin(Defect* twin) {
    if (twin != nullptr) {
        this->twinID = twin->getID();
        twin->twinID = getID();
    }
}

void Defect::setSpouse(Defect* spouse) {
    if (spouse != nullptr) {
        this->spouseID = spouse->getID();
        spouse->spouseID = getID();
    }
}

const SpaceTemp* Defect::getBirth() const {
    return &birth;
}

int Defect::getTwin() const {
    return twinID;
}

int Defect::getSpouse() const {
    return spouseID;
}

const SnapDefect Defect::birthSnap() const {
    return SnapDefect(birth, getID(), getCharge());
}

std::ostream& operator<<(std::ostream& os, const Defect& def) {
    return os << "ID: " << def.getID() << (def.getCharge() ? "+" : "-") << " born at " << def.birth << ", time: [" << def.birth.getTime() << ", " << def.getTime() << "]";
}

void Defect::clearPairs(){
    twinID = spouseID = SnapDefect::NO_ID;
}

const SnapDefect Defect::snapShot(bool birth) const{
    return birth? birthSnap() : SnapDefect(*this);
}

int Defect::age() const{
    return  this->getTime() - getBirth()->getTime();
}

void Defect::update(const SpaceTemp& st){
    timeMIA += st.getTime() - getTime() - 1;
    copy(st);
}