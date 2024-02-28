#include "Loc.h"

#include <cmath> // For std::sqrt

// Default constructor
Loc::Loc() : x(0.0), y(0.0) {}

// Parameterized constructor
Loc::Loc(double x, double y) : x(x), y(y) {}

// Copy constructor
Loc::Loc(const Loc& orig) : x(orig.x), y(orig.y){}

// Destructor
Loc::~Loc() {}

// Distance calculation function
double Loc::dist(const Loc& loc) const {
    double dx = loc.x - x;
    double dy = loc.y - y;
    
    return std::sqrt(dx * dx + dy * dy);
}

// Getter function for x
double Loc::getX() const {
    return x;
}

// Getter function for y
double Loc::getY() const {
    return y;
}

void Loc::set(double x, double y){
    this->x = x;
    this->y = y;
}

// Overloaded operator<< function
std::ostream& operator<<(std::ostream& os, const Loc& loc) {
    os << "(" << loc.x << ", " << loc.y << ")";
    return os;
}

bool operator == (const Loc &x, const Loc& y){
    return x.dist(y) <= Loc::equalityThreshold;
}
