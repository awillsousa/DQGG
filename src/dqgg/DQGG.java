package dqgg;

import java.awt.Color;
import java.io.File;
import java.net.URL;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 *
 * @author AntonioSousa
 */
public class DQGG {        
    public static final BD bd = new BD("/BD/DQGG.db");
    //public static final BD bd = new BD("BD/DQGG.db");
    public static final Color corConsulta  = new Color(255,255,153);
    public static final Color corInsercao  = new Color(153,255,204);
    public static final Color corAlteracao = Color.WHITE;
    public static final Color corDesabilitado = Color.GRAY;
    public static final String pathApp = System.getProperty("user.dir")+"/";    
    public static final String pathImg   = pathApp;
    public static final boolean modoDesenv = false;
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("path App: " + DQGG.pathApp);
        Principal formPrincipal = new Principal();        
        formPrincipal.setVisible(true);
    }
    
    
}
