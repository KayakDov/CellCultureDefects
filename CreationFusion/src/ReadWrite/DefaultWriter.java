package ReadWrite;

import GeometricTools.Vec;
import SnapManagement.PairSnDef;
import defectManagement.DefectManager;
import java.io.IOException;
import java.util.Arrays;

/**
 * A default file writer.
 *
 * @author E. Dov Neimand
 */
public class DefaultWriter extends FormatedFileWriter {

    public DefaultWriter(String fileName) throws IOException {
        super(
                fileName, ',',
                frame, //0
                posID, //1
                posX, //2
                posY, //3
                posTail, //4
                negID, //5
                negX, //6
                negY, //7
                negTail[0],//8
                negTail[1],//9
                negTail[2], //11
                dist,
                mpAngle, //12
                anglePRel, //13
                angleMRel[0],//14
                angleMRel[1],//15
                angleMRel[2],//16
                fuseUp, //17
                pVelAngle, //18
                pVelAngleRel, //19
                anglePRelVelAngle, //20
                fusion, //21
                creation, //22
                mpTailAngleRel[0], //23
                mpTailAngleRel[1], //24
                mpTailAngleRel[2], //25
                mpPhase //26
        );
    }

    /**
     * Sets a prefix for each ID.
     *
     * @param idBegin The prefix for the id.
     * @return This writer.
     */
    public DefaultWriter setIdBegin(String idBegin) {
        
        Arrays.stream(cols)
                .filter(col -> col instanceof IDCol)
                .map(col -> (IDCol)col)
                .forEach(idCol -> idCol.setPrefix(idBegin));
        
        return this;
    }

    /**
     * The class for an ID column.
     */
    private static class IDCol extends Column{
        
        private String prefix = "";
        private final boolean charge;

        public IDCol(boolean charge) {
            super((charge?"plus_":"min_") + "id");
            this.charge = charge;
        }
        
        public String apply(PairSnDef sdp) {
            return prefix + (charge?sdp.pos:sdp.neg).getID() + "";
        }

        /**
         * sets the prefix for the id.  This may be empty, or the experiment number.
         * @param prefix The prefix.
         */
        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }
        
        
    }
    
    /**
     * The positive id of the pair.
     */
    public static IDCol posID = new IDCol(DefectManager.POS);

    /**
     * The frame of the pair.
     */
    public static Column frame = new Column("FRAME") {
        @Override
        public String apply(PairSnDef sdp) {
            return sdp.pos.loc.getTime() + "";
        }
    };

    /**
     * The x value of the positive defect.
     */
    public static Column posX = new Column("xp") {
        @Override
        public String apply(PairSnDef sdp) {
            return sdp.pos.loc.getX() + "";
        }
    };

    /**
     * The y position of th epositive defect
     */
    public static Column posY = new Column("yp") {
        @Override
        public String apply(PairSnDef sdp) {
            return sdp.pos.loc.getY() + "";
        }
    };

    /**
     * The anlge of the positive tail.
     */
    public static Column posTail = new Column("angp1") {
        @Override
        public String apply(PairSnDef sdp) {
            return sdp.pos.tailAngle().rad() + "";
        }
    };

    /**
     * The ID of the negative defect.
     */
    public static IDCol negID = new IDCol(DefectManager.NEG);

    /**
     * The x value of the negative defect.
     */
    public static Column negX = new Column("xm") {
        @Override
        public String apply(PairSnDef sdp) {
            return sdp.neg.loc.getX() + "";
        }
    };

    /**
     * The y value of the negative defect.
     */
    public static Column negY = new Column("ym") {
        @Override
        public String apply(PairSnDef sdp) {
            return sdp.neg.loc.getY() + "";
        }
    };

    /**
     * The negative tail angles.
     */
    public static Column[] negTail = new Column[]{
        new Column("angm1") {
            @Override
            public String apply(PairSnDef sdp) {
                return sdp.neg.tailAngle()[0].rad() + "";
            }
        },
        new Column("angm2") {
            @Override
            public String apply(PairSnDef sdp) {
                return sdp.neg.tailAngle()[1].rad() + "";
            }
        },
        new Column("angm3") {
            @Override
            public String apply(PairSnDef sdp) {
                return sdp.neg.tailAngle()[2].rad() + "";
            }
        }
    };
    
    
    
    /**
     * The distance between the defects.
     */
    public static Column dist = new Column("distance") {
        @Override
        public String apply(PairSnDef sdp) {
            return sdp.dist() + "";
        }
    };

    /**
     * The angle of the4 vector from the positive defect to the negative defect.
     */
    public static Column mpAngle = new Column("mp_angle") {
        @Override
        public String apply(PairSnDef sdp) {
            return sdp.mpAngle().rad() + "";
        }
    };

    /**
     * The angle of the positive tail relative to mpAngle.
     */
    public static Column anglePRel = new Column("angp1_rel") {
        @Override
        public String apply(PairSnDef sdp) {
            return sdp.anglePRel().rad() + "";
        }
    };

    /**
     * The angles of the negative tails relative to mpAngle.
     */
    public static Column[] angleMRel = new Column[]{
        new Column("angm1_rel") {
            @Override
            public String apply(PairSnDef sdp) {
                return sdp.ang123Rel()[0].rad() + "";
            }
        },
        new Column("angm2_rel") {
            @Override
            public String apply(PairSnDef sdp) {
                return sdp.ang123Rel()[1].rad() + "";
            }
        },
        new Column("angm3_rel") {
            @Override
            public String apply(PairSnDef sdp) {
                return sdp.ang123Rel()[2].rad() + "";
            }
        }
    };

    /**
     * Does the tail point clockwise or counter clockwise.
     */
    public static Column fuseUp = new Column("fuse_up") {
        @Override
        public String apply(PairSnDef sdp) {
            return sdp.fuseUp ? "TRUE" : "FALSE";
        }
    };

    /**
     * The angle of the positive defect's velocity.
     */
    public static Column pVelAngle = new Column("p_vel_angle") {
        @Override
        public String apply(PairSnDef sdp) {
            Vec vel = sdp.pos.getVelocity();
            return (vel != null ? vel.angle().rad() : "") + "";
        }
    };

    /**
     * The angle of the positive defect's velocity relative to the negative defect.
     */
    public static Column pVelAngleRel = new Column("p_vel_angle_rel") {
        @Override
        public String apply(PairSnDef sdp) {
            Vec vel = sdp.relVelocity();
            return vel != null ? vel.angle().rad() + "" : "";
        }
    };

    /**
     * The angle of the positive tail relative to the positive velocity.
     */
    public static Column anglePRelVelAngle = new Column("anglep1_rel_vel_angle") {
        @Override
        public String apply(PairSnDef sdp) {
            return sdp.anglep1_rel_vel_angle().rad() + "";
        }
    };

    /**
     * Is this a fusion event?
     */
    public static Column fusion = new Column("fusion") {
        @Override
        public String apply(PairSnDef sdp) {
            return !sdp.birth ? "TRUE" : "FALSE";
        }
    };

    /**
     * Is this a creation event?
     */
    public static Column creation = new Column("creation") {
        @Override
        public String apply(PairSnDef sdp) {
            return sdp.birth ? "TRUE" : "FALSE";
        }
    };

    /**
     * The negative tail angle relative to the positive tail angle.
     */
    public static Column[] mpTailAngleRel = new Column[]{
        new Column("mp_angl1") {
            @Override
            public String apply(PairSnDef sdp) {
                return sdp.mp123()[0].rad() + "";
            }
        },
        new Column("mp_angl2") {
            @Override
            public String apply(PairSnDef sdp) {
                return sdp.mp123()[1].rad() + "";
            }
        },
        new Column("mp_angl3") {
            @Override
            public String apply(PairSnDef sdp) {
                return sdp.mp123()[2].rad() + "";
            }
        }
    };

    /**
     * The average of the negative angles relative to the positive angle mod 2pi/3.
     */
    public static Column mpPhase = new Column("mp_phase") {
        @Override
        public String apply(PairSnDef sdp) {
            return sdp.mpPhase() + "";
        }
    };

}
