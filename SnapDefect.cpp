/* 
 * File:   DnapDefect.cpp
 * Author: E. Dov Neimand
 */

#include "SnapDefect.h"
#include "DefectManager.h"
#include <fstream>
#include <iostream>

SnapDefect::SnapDefect(const SpaceTemp& loc, int id, bool charge)
    :SpaceTemp(loc), id(id), charge(charge){
}

/**
 * Moces the reader forward by the number of commas provided
 * @param file The reader.
 * @param numCommas The number of commas the reader needs to move forward.
 */
void progress(std::istream& file, int numCommas){
    
    for(int soFar = 0, j = 0; soFar < numCommas; j++){ 
//        std::cout << (char)file.peek() << std::endl;
        if(file.eof()) throw std::runtime_error("End of file reached unexpectedly. i = " + std::to_string(j));
 
        if(file.get() == ',') soFar++;
    }
}

SnapDefect SnapDefect::fromLine(std::istream& file){
    progress(file, 3);
    
    int id;
    if(file.peek() == ',') id = NO_ID;
    else file >> id;
    
    progress(file, 2);
    
    double x;
    file >> x;
    
    progress(file, 1);
    double y;
    file >> y;
    
    progress(file, 2);
    double t;
    file >> t;
        
    progress(file, 20);
    double dCharge;
    file >> dCharge;
    bool positive = dCharge > 0;
    
    for(int i = 0; i < 3; i++) file.get(); //There seems to be 3 empty spaces at the end of each line?
    
    return SnapDefect(SpaceTemp(Loc(x, y), t), id*(positive? 1:-1), positive); //TODO: Before the number -1 was used for no charge, now that's not safe anymore.
}

SnapDefect::SnapDefect(const SnapDefect& orig): SpaceTemp(orig), id(orig.id), charge(orig.charge) {
}

SnapDefect::~SnapDefect() {
}

int SnapDefect::getID() const{
    return id;
}

bool SnapDefect::getCharge() const{
    return charge;
}

bool SnapDefect::isTracked() const{
    return getID() != NO_ID;
}
