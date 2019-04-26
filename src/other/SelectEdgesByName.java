// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package other;
import java.awt.event.ActionEvent;
import main.sunrise;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
import utils.CyUtils;
import dialog.TextFromPasteBox;
/**
 * Select nodes by a name list 
 */
public class SelectEdgesByName extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	final static String title="  Select Edges by a Name List";
	public SelectEdgesByName(){
		super(title,sunrise.getAdapter().getCyApplicationManager(),"network",sunrise.getAdapter().getCyNetworkViewManager());
		setPreferredMenu(sunrise.name);
	}
	public void actionPerformed(ActionEvent e){
		CyApplicationManager applicationManager=sunrise.getAdapter().getCyApplicationManager();
		CySwingApplication swingApplication=sunrise.getAdapter().getCySwingApplication();
		CyNetwork network=applicationManager.getCurrentNetwork();
		CyUtils cu=new CyUtils(network,true);
		(new TextFromPasteBox(swingApplication.getJFrame(),
				"Select Edges By Names In "+cu.getName(),"Paste Edge List",cu)).setVisible(true);
	}
}

