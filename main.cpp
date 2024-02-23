
#include <cstdlib>
#include <iostream>
#include "DefectManager.h"
#include <fstream>
#include <functional>
#include <sstream>
#include <string>
#include "StatisticalTools.h"
#include <vector>
using namespace std;
/**
 * Prints a chart that is the output of the function.
 * @param func The functions from (int, double) -> int whose output will be 
 * printed.
 * @param startT The first int value.
 * @param endT The int max, inclusive.
 * @param jumpT the int increment.
 * @param startD The first double value.
 * @param endD The double max, inclusive.
 * @param jumpD The double increment.
 * @param out An output stream.
 */
void chart(std::function<int(double, int)> func,
                  int startT, int endT, int jumpT,
                  double startD, double endD, double jumpD, std::ostream& out) {


    out << "Time\\Dist";
    for (double d = startD; d <= endD; d += jumpD) out << d << "\t";
    
    out << "\n";

    for (int t = startT; t <= endT; t += jumpT) {
        out << t << "\t";
        for (double d = startD; d <= endD; d += jumpD) out << func(d, t) << "\t";
        out << "\n";
    }

}





int main(int argc, char** argv) {

//    DefectManager dm("PlusAndMinusTM.csv");
//    dm.pairDefects(5, 3);//dist, time
    
//    cout << dm;
    
//    cout << "spouse: " << dm.count(DefectManager::Relationship::SPOUSE) << endl;
//    cout << "twin: " << dm.count(DefectManager::Relationship::TWIN) << endl;
//    cout << "both: " << dm.count(DefectManager::Relationship::SPOUSE_AND_TWIN)<< endl;
//    cout << "Num defects = " << dm.count(DefectManager::Relationship::ALL)<< endl;
    
//    
//    chart([&dm](double dist, int time){
//        dm.pairDefects(dist, time);
//        return dm.count(DefectManager::Relationship::SPOUSE);
//    }, 1, 10, 1, 1, 10, 1, std::cout);
//    
    
    std::vector<double> vec = {1, 2, 3};
    
    StatisticalTools<std::vector<double>::iterator> tool(vec.begin(), vec.end());
    
    cout<< tool.variance([](double x){return 2*x;});
    
    return 0;
}
