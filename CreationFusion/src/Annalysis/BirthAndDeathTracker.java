package Annalysis;

import Charts.BarChart;
import Charts.HeatMap;
import Charts.Histogram;
import Charts.LineChart;
import Charts.NamedData;
import Charts.ScatterPlot;
import GeometricTools.OpenSpaceTimeBall;
import GeometricTools.Vec;
import SnapManagement.PairSnDef;
import defectManagement.DefectManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.math3.util.Precision;

/**
 *
 * @author E. Dov Neimand
 */
public class BirthAndDeathTracker {

    private final DefectManager dm;

    public BirthAndDeathTracker(DefectManager dm) {
        this.dm = dm;
    }

    /**
     * The angle of the positive defect tail relative to the negative defect
     * location.
     *
     * @param isBirth True for twins and false for spouses.
     * @param maxDist The maximum distance between the paired defects.
     */
    public void angleNearFusion(boolean isBirth, int maxDist) {
        
        var pairs = dm.pairsAtDistance(pair -> pair.anglePRel().deg(), isBirth, maxDist);
        HeatMap.factory(
                dm.getName() + ": angPRel as a function of distance",
                "distance",
                "angPRel",
                pairs,
                1000,
                3,
                Double.NaN,
                360
        );
        
        Histogram.factory(
                pairs.stream().mapToDouble(vec -> vec.getY()).toArray(), 
                50, 
                dm.getName() + ": angle between pos tail and conecting line", 
                "angle"
        );
    }

    /**
     * The angle of the positive defect tail relative to the negative defect
     * location.
     *
     * @param birth True for twins and false for spouses.
     */
    public void phaseNearFusion(boolean birth) {
        HeatMap.factory(
                "mpPhase as a function of distance",
                "distance",
                "mpPhase",
                dm.pairsAtDistance(pair -> pair.mpPhase(), birth, 60),
                1000,
                .3,
                Double.NaN,
                2 * Math.PI / 3
        );
    }

    /**
     * number of creations preceding time t minus number of fusions preceding
     * time t
     *
     * @return A graph of points: number of creations preceding time t minus
     * number of fusions preceding time t
     */
    private NamedData pairedCreationFusion() {
        return new NamedData(
                dm.ofTime(t -> dm.all().filter(def
                -> def.getBirth().getTime() <= t && def.hasTwin() && def.isEligable(DefectManager.DEATH)).count()
                - dm.all().filter(def -> def.getDeath().getTime() <= t && def.hasSpouse() && def.isEligable(DefectManager.BIRTH)).count()
                ),
                "number of creations preceding time t minus number of fusions  preceding time t"
        );
    }

    /**
     * number of defects alive at time t that are eligible for pairing
     *
     * @return number of defects alive at time t that are eligible for pairing
     */
    private NamedData livingDefects() {
        return new NamedData(
                dm.ofTime(t -> dm.all().filter(def
                -> def.aliveAt(t)
                && def.isEligable(DefectManager.BIRTH)
                && def.isEligable(DefectManager.DEATH))
                .count()
                ),
                "number of defects alive at time t that are eligable for pairing"
        );
    }

    /**
     * The number of defects as a function of time.
     */
    public void numberOfDefects() {

        NamedData paired = pairedCreationFusion(), living = livingDefects(),
                multiple = new NamedData(
                        paired.stream().map(vec -> new Vec(vec.getX(), vec.getY() * 1.7)).collect(Collectors.toList()),
                        "paired defects * 1.7"
                );

        LineChart.factory(
                "number of defects at time t by birth and by frame for " + dm.getName(),
                "time", "number of defects", livingDefects(), pairedCreationFusion(), multiple);
    }

    /**
     * percent of near fusions that result in fusions at the given mpPhase.
     * @param generalProximity A distance for which paired annihilation might be possible.
     * @param deathProximity Definition of near for considering annihilations to be paired.
     */
    public void percentMergeAtPhase(double generalProximity, OpenSpaceTimeBall deathProximity) {
        
        Map<Double, List<PairSnDef>> nearCollsionsAtPhase
                = dm.nearCollsionsSnPairs(generalProximity)
                    .collect(
                            Collectors.groupingBy(pair ->Precision.round(pair.mpPhase(), 1))
                    );
        
        Map<Double, Double> percentColisionAtPhase = new HashMap<>(nearCollsionsAtPhase.size());
        nearCollsionsAtPhase.entrySet().forEach(entry -> percentColisionAtPhase.put(
                entry.getKey(), 
                (double)entry.getValue().stream().filter(pair -> pair.shareEvent(deathProximity, dm, DefectManager.DEATH)).count()/entry.getValue().size()
        ));

        //I need to build a map from the phases to the percent of near collisions that are collisions for each phase.
        
        BarChart.factory(dm.getName(), "mpPhase", "percent of dist < "+ generalProximity + " that result in dist < " + deathProximity.rSpace + " paired annihilation", percentColisionAtPhase);
    }
    
    /**
     * The speed as a function of the angle.
     * @param birth true for birth, false for death
     */
    public void speedFunctionOfAngle(boolean birth){
        
        List<Vec> vecs = dm.pairs(birth).filter(pair -> pair.hasVelocity()).map(pair -> 
                        new Vec(
                                pair.mpPhase(), 
                                pair.relVelocity().dot(pair.pos.loc.minus(pair.neg.loc).angle().vec())
                        )
                ).collect(Collectors.toList());
        
//        NamedData data = new NamedData(
//                vecs, 
//                "speed at angle");
        
        HeatMap.factory(dm.getName(), "mpAngle", "speed", vecs, 1000, .6, 2*Math.PI/3, Double.POSITIVE_INFINITY);
    }
    
    
    /**
     * The distance as a function of frame.
     * @param timeRange The time covered by the analysis.
     * @param minLongevity The minimum amount of time a defect must be alive to be considered.
     */
    public void distanceOfFrame(int timeRange, int minLongevity){
                
                NamedData ann = new NamedData(dm.pairs(DefectManager.DEATH)
                        .filter(pair -> pair.timeFromEvent < timeRange)
                        .filter(pair -> dm.getDefect(pair.pos).age() > minLongevity && dm.getDefect(pair.neg).age() > minLongevity)
                        .map(pair -> new Vec(-pair.timeFromEvent, pair.dist()))                        
                        .collect(Collectors.toList()), "Annihilation");
                
                NamedData creation = new NamedData(dm.pairs(DefectManager.BIRTH)
                        .filter(pair -> pair.timeFromEvent < timeRange)
                        .filter(pair -> dm.getDefect(pair.pos).age() > minLongevity && dm.getDefect(pair.neg).age() > minLongevity)
                        .map(pair -> new Vec(pair.timeFromEvent, pair.dist()))
                        .collect(Collectors.toList()), "Creation");
                
                
                
                LineChart.factory("Distance Evolution Between Defect Pairs: " + dm.getName(), "Frame", "Distance", ann, creation);

    }
}
