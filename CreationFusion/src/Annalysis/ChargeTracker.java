package Annalysis;


import Charts.LineChart;
import Charts.NamedData;
import GeometricTools.Vec;
import defectManagement.DefectManager;
import static defectManagement.DefectManager.NEG;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tracks the system charge
 *
 * @author E. Dov Neimand
 */
public class ChargeTracker {
    
    private DefectManager dm;
    
    /**
     * Constructs a tool for analysis of system charge over time.
     * @param dm The name of the experiment. 
     */
    public ChargeTracker(DefectManager dm) {
//        graphChargeOfTime();
//        graphCumulativeCharge();
//        graphRatioOfTime();
    }
    
    /**
     * Graphs the charge of the system as a function of time.
     */
    public void graphChargeOfTime(){
        NamedData pos = new NamedData(
                dm.ofTime(i -> dm.numOfCharge(i, DefectManager.POS)), 
                "positive"
        );
        NamedData neg = new NamedData(
                dm.ofTime(i -> -dm.numOfCharge(i, DefectManager.POS)), 
                "negative"
        );
        NamedData total = new NamedData(
                dm.ofTime(i -> dm.numOfCharge(i, DefectManager.POS) - dm.numOfCharge(i, NEG)), 
                "total"
        )
                ;
        LineChart.factory("Total Charge " + dm.getName(), "time", "charge", pos, neg, total);
    }
    
    /**
     * Graphs the ratio as a function of time.
     */
    public void graphRatioOfTime(){
        NamedData ratioOfTime = new NamedData(
                dm.timeStream().mapToObj(i -> new Vec(i, dm.posToNegRatio(i))).collect(Collectors.toList()), 
                "Pos to Neg Charge Ratio as a Function Of Time");
        LineChart.factory(dm.getName(), "time", "positive/negative ratio", ratioOfTime);
    }
        

}
