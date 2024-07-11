package main;

/**
 *
 * @author E. Dov Neimand
 */
public class ArgsCreatePictures extends ArgumentProcessor{
    
    public final int targetRow;
    
    public ArgsCreatePictures(String[] args) {
        super(args);
        
        targetRow = Integer.parseInt(args[2]);
    }
    
    /**
     * Some default arguments for the creation of pictures.
     * @return Some default arguments for the creation of pictures.
     */
    public static String[] defaultPictureCreationArgs(){
        return new String[]{"BacteriaPairs.csv", "images/output", "5794"};
    }    
}
