package creationfusion;

import GeometricTools.Rectangle;
import ReadWrite.FormatedFileWriter;
import java.util.Arrays;
import java.util.stream.DoubleStream;

/**
 *
 * @author E. Dov Neimand
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
                
        DefectManager dm = new DefectManager("PlusAndMinusTM.csv").setWindow(new Rectangle(900, 0, 1800, 900));
                
        dm.loadPairs();
        dm.loadLifeCourses();
        
        
        FormatedFileWriter ffw = FormatedFileWriter.defaultWriter("RPE1_pairs.csv");
        
        dm.positives().filter(def -> def.hasTwin()).flatMap(posDefect -> posDefect.defectPairs(DefectManager.BIRTH)).forEach(sd -> ffw.writeLine(sd));
        dm.positives().filter(def -> def.hasSpouse()).flatMap(posDefect -> posDefect.defectPairs(DefectManager.DEATH)).forEach(sd -> ffw.writeLine(sd));

    }

}
