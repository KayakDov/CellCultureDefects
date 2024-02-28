package creationfusion;

import java.util.Arrays;

/**
 * Represents a snapshot of a defect at a moment in time.
 */
public class SnapDefect extends SpaceTemp {

    private final int id;
    private final boolean charge;
    
    public final static int NO_ID = Integer.MAX_VALUE;

    /**
     * Constructs a new SnapDefect instance with the specified location, ID, and charge.
     * @param loc The location of the defect.
     * @param id The ID of the defect.
     * @param charge The charge of the defect.
     */
    public SnapDefect(SpaceTemp loc, int id, boolean charge) {
        super(loc);
        this.id = id;
        this.charge = charge;
    }
    
    /**
     * Constructs a new SnapDefect instance with the specified location, ID, and charge.
     * @param x The x value.
     * @param y The y value.
     * @param t The time value.
     * @param id The ID of the snap Defect.
     * @param charge The charge of the snap defect.
     */
    public SnapDefect(double x, double y, int t, int id, boolean charge){
        super(x, y, t);
        this.id = id;
        this.charge = charge;
    }

    
    public final static int LABEL = 1, ID = 2, TRACK_ID = 3, QUALITY = 4, POSITION_X = 5, 
        POSITION_Y = 6, POSITION_Z  = 7, POSITION_T = 8, FRAME = 9, RADIUS = 10,
        VISIBILITY = 11, MANUAL_SPOT_COLOR = 12, MEAN_INTENSITY_CH1 = 13,
        MEDIAN_INTENSITY_CH1 = 14, MIN_INTENSITY_CH1 = 15, MAX_INTENSITY_CH1 = 16,
	TOTAL_INTENSITY_CH1 = 17, STD_INTENSITY_CH1 = 18, CONTRAST_CH1 = 19,
        SNR_CH1 = 20, x_img = 21, y_img = 22, x_img1 = 23, y_img1 = 24, ang1 = 25,
	ang2 = 26, ang3 = 27, chargeInd = 28;

    
    
    
    /**
     * Parses a line from a line of a formated file constructs a SnapDefect from it.
     * The line needs to be delineated by comas and the indices of its values
     * must match the public final static ints above.
     * @param line The formated line to be parsed.
     * @return The snap defect generated from the line.
     */
    public static SnapDefect fromLine(String line) {
        String[] split = line.split(",");
        
        double x = Double.parseDouble(split[x_img]);
        double y = Double.parseDouble(split[y_img]);
        int time = (int)Double.parseDouble(split[POSITION_T]);
        boolean charge = Double.parseDouble(split[chargeInd]) > 0;
        int ID = "".equals(split[TRACK_ID])?NO_ID:(int)Double.parseDouble(split[TRACK_ID]);
        return new SnapDefect(x, y, time, ID, charge);
    }

    /**
     * Gets the ID of this defect.
     * @return The ID of the defect.
     */
    public int getID() {
        return id;
    }

    /**
     * Gets the charge of this defect.
     * @return true if the defect has a positive charge, false otherwise.
     */
    public boolean getCharge() {
        return charge;
    }

    /**
     * Checks if this defect is tracked (i.e., has an ID assigned).
     * @return true if the defect is tracked, false otherwise.
     */
    public boolean isTracked() {
        return id != NO_ID;
    }
}
