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
    private String dataName;
    
    public final float posToNegRatioTotall;

    /**
     * Constructs a tool for analysis of system charge over time.
     * @param dm The name of the experiment.
     * @param dataName 
     */
    public ChargeTracker(DefectManager dm, String dataName) {
        this.dm = dm;
        posToNegRatioTotall = (float) dm.positives().count() / dm.negatives().count();
        this.dataName = dataName;

        graphChargeOfTime();
        graphCumulativeCharge();
        graphRatioOfTime();
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
        LineChart.factory("Total Charge " + dataName, "time", "charge", pos, neg, total);
    }
    
    /**
     * Graphs the ratio as a function of time.
     */
    public void graphRatioOfTime(){
        NamedData ratioOfTime = new NamedData(
                dm.timeStream().mapToObj(i -> new Vec(i, dm.posToNegRatio(i))).collect(Collectors.toList()), 
                "Pos to Neg Charge Ratio as a Function Of Time");
        LineChart.factory(dataName, "time", "positive/negative ratio", ratioOfTime);
    }
    
    /**
     * Graphs the cumulative charge.
     */
    public void graphCumulativeCharge(){
        NamedData ratioOfTime = new NamedData(
                dm.cumulativeSystemCharge(), 
                "Pos to Neg Charge Ratio as a Function Of Time");
        
        NamedData constRatio = new NamedData(
                new ArrayList<>(List.of(new Vec(0,0), new Vec(dm.getEndTime(),dm.totalCharge()))), 
                "constant ratio projection"
        );
        
        LineChart.factory("total charge = " + dm.totalCharge() + " For " + dataName, "time", "cumulative charge", ratioOfTime, constRatio);
    }

}
