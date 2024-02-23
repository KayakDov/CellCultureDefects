
/* 
 * File:   SpaceTemp.cpp
 * Author: E. Dov Neimand
 * 
 */

#include "SpaceTemp.h"


SpaceTemp::SpaceTemp(Loc loc, int t):Loc(loc), time(t) {
}

SpaceTemp::SpaceTemp(const SpaceTemp& orig):Loc(orig.getX(), orig.getY()), time(orig.getTime()) {
}

SpaceTemp::~SpaceTemp() {
}

int SpaceTemp::getTime() const{
    return time;
}

void SpaceTemp::setTime(int t){
    this->time = t;
}

void SpaceTemp::copy(const SpaceTemp& st){
    setTime(st.getTime());
    set(st.getX(), st.getY());
}

bool SpaceTemp::near(const SpaceTemp& st, const double& distThreshhold, const double& timeThreshold) const{
    return dist(st) <= distThreshhold && std::abs(time - st.time) <= timeThreshold;
}

double SpaceTemp::sTDist(const SpaceTemp& st) const{
    return dist(st) + std::abs(st.getTime() - getTime());
}