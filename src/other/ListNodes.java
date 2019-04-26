// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package other;
import java.awt.event.ActionEvent;
import main.sunrise;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import dialog.TextBox;
/**
 * List nodes in the current network
 * If a node points to a network (nested network), list nodes inside the network
 */
public class ListNodes extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	final static String title="  List Nodes & Within Nodes";
	public ListNodes(){
		super(title,sunrise.getAdapter().getCyApplicationManager(),"network",sunrise.getAdapter().getCyNetworkViewManager());		
		setPreferredMenu(sunrise.name);
	}
	public void actionPerformed(ActionEvent e) {
		CyApplicationManager applicationManager=sunrise.getAdapter().getCyApplicationManager();
		CySwingApplication swingApplication=sunrise.getAdapter().getCySwingApplication();
		CyNetwork network=applicationManager.getCurrentNetwork();
		StringBuffer text=new StringBuffer("Nodes\tWithin_Nodes\r\n");
		for(CyNode node:network.getNodeList())
			if(node.getNetworkPointer()==null){
				text.append(network.getRow(node).get(CyNetwork.NAME,String.class));
				text.append("\r\n"); 
			}
			else{
				for(CyNode nodeIn:node.getNetworkPointer().getNodeList()){
					text.append(network.getRow(node).get(CyNetwork.NAME,String.class));
					text.append("\t");	
					text.append(node.getNetworkPointer().getRow(nodeIn).get(CyNetwork.NAME,String.class));
					text.append("\r\n");
				}
			}
		new TextBox(swingApplication.getJFrame(),title+" of "+network.getRow(network).get(CyNetwork.NAME,String.class),0.2,0.8,text.toString()).setVisible(true);
	}
}