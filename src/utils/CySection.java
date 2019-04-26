// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package utils;
import java.awt.event.ActionEvent;
import org.cytoscape.application.swing.AbstractCyAction;
import main.sunrise;
/**
 * Insert title of section in menu
 */
public class CySection extends AbstractCyAction {
	private static final long serialVersionUID = 1L;
	static String[] titles={"Score Id and Compact Table:","Search Max Score Subnetwork:","By Selection in Compact:","By Selection in Any Net:","Complementary Functions:"};
	public CySection(int item){
		super(titles[item],sunrise.getAdapter().getCyApplicationManager(),"network",sunrise.getAdapter().getCyNetworkViewManager());		
		setPreferredMenu(sunrise.name);
	}
	public void actionPerformed(ActionEvent e){
	}
}
