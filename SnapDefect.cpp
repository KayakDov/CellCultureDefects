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
        if(file.eof()) throw std::runtime_error("End of file reached unexpectedly. i = " + std::to_string(j));
 
        if(file.get() == ',') soFar++;
    }
}


SnapDefect SnapDefect::fromLine(std::istream& file){
    constexpr int LABEL = 1, ID = 2, TRACK_ID = 3, QUALITY = 4, POSITION_X = 5, 
        POSITION_Y = 6, POSITION_Z  = 7, POSITION_T = 8, FRAME = 9, RADIUS = 10,
        VISIBILITY = 11, MANUAL_SPOT_COLOR = 12, MEAN_INTENSITY_CH1 = 13,
        MEDIAN_INTENSITY_CH1 = 14, MIN_INTENSITY_CH1 = 15, MAX_INTENSITY_CH1 = 16,
	TOTAL_INTENSITY_CH1 = 17, STD_INTENSITY_CH1 = 18, CONTRAST_CH1 = 19,
        SNR_CH1 = 20, x_img = 21, y_img = 22, x_img1 = 23, y_img1 = 24, ang1 = 25,
	ang2 = 26, ang3 = 27, charge = 28;
    
    progress(file, TRACK_ID);
    
    int id;
    if(file.peek() == ',') id = NO_ID;
    else file >> id;
    
    progress(file, POSITION_T - TRACK_ID);
    double t;
    file >> t;
    
    progress(file, x_img - POSITION_T);
    
    double x;
    file >> x;
    
    progress(file, y_img - x_img);
    double y;
    file >> y;
        
    progress(file, charge - y_img);
    double dCharge;
    file >> dCharge;
    bool positive = dCharge > 0;
    
    for(int i = 0; i < 3; i++) file.get(); //There seems to be 3 empty spaces at the end of each line?
        
    return SnapDefect(SpaceTemp(Loc(x, y), t), id, positive); //TODO: Before the number -1 was used for no charge, now that's not safe anymore.
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
