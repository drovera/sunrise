// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package other;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.JFrame;
import main.sunrise;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import dialog.ListDialog;
import dialog.TextBox;
import utils.CyUtils;
/**
 * List nodes common to selected networks
 */
public class InterNetNodes extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	final static String title="  Nodes Common to Networks";
	public InterNetNodes(){
		super(title,sunrise.getAdapter().getCyApplicationManager(),"network",sunrise.getAdapter().getCyNetworkViewManager());		
		setPreferredMenu(sunrise.name);
	}
	String[] dummy={"$"};
	public void actionPerformed(ActionEvent e) {
		JFrame frame=sunrise.getAdapter().getCySwingApplication().getJFrame();
		CyUtils cu=new CyUtils();
		TreeMap<String,CyNetwork> nameToNet=cu.getNameToNet();
		String[] values=nameToNet.keySet().toArray(dummy);
		ListDialog ld=new ListDialog(frame,title,"Select Networks to Display Their Common Nodes",values);
		ArrayList<String> select=new ArrayList<String>();
		ld.launchDialog(select);
		if(select.isEmpty()) return;
		TreeSet<String> common=new TreeSet<String>();
		cu.net=nameToNet.get(select.get(0));
		for(CyNode node:cu.net.getNodeList()) common.add(cu.getName(node));
		HashSet<String> nodeList=new HashSet<String>();
		for(int n=1;n<select.size();n++){
			cu.net=nameToNet.get(select.get(n));
			nodeList.clear();
			for(CyNode node:cu.net.getNodeList()) nodeList.add(cu.getName(node));
			common.retainAll(nodeList);
		}
		StringBuffer text=new StringBuffer();
		for(String name:common){
			text.append(name);
			text.append("\r\n"); 
		}
		StringBuffer ttl=new StringBuffer(select.get(0));
		for(int s=1;s<select.size();s++){
			ttl.append("|");
			ttl.append(select.get(s));
		}
		new TextBox(frame,ttl.toString(),0.2,0.8,text.toString()).setVisible(true);
	}
}