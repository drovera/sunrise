// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package other;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.List;
import main.sunrise;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import utils.CyUtils;
import dialog.TextBox;
/**
 * List selected nodes in the current network
 */
public class ListSelectedNodes extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	final static String title="  List Selected Nodes";
	public ListSelectedNodes(){
		super(title,sunrise.getAdapter().getCyApplicationManager(),"network",sunrise.getAdapter().getCyNetworkViewManager());
		setPreferredMenu(sunrise.name);
	}
	public void actionPerformed(ActionEvent e) {
		CyApplicationManager applicationManager=sunrise.getAdapter().getCyApplicationManager();
		CySwingApplication swingApplication=sunrise.getAdapter().getCySwingApplication();
		CyNetwork network=applicationManager.getCurrentNetwork();
		List<CyNode> selected=CyTableUtil.getNodesInState(network,"selected",true);
		CyUtils cu=new CyUtils(network);
		StringBuffer text=new StringBuffer();
		Iterator<CyNode> iter=selected.iterator();
		while(iter.hasNext()){
			CyNode node=iter.next();
			text.append(cu.getName(node));
			text.append("\r\n");
		}	
		new TextBox(swingApplication.getJFrame(),title+" of "+cu.getName(),0.2,0.8,text.toString()).setVisible(true);
	}
}