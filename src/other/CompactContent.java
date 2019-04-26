// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package other;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import utils.CyUtils;
import utils.GeneticUtils;
import dialog.TextBox;
import main.sunrise;
/**
 * List positive nodes from reference corresponding
 * to selected nodes selected in the current compact network
 */
public class CompactContent extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	final static String title="  Contents of Selected Compact Nodes"; 
	public CompactContent(){
		super(title,sunrise.getAdapter().getCyApplicationManager(),"network",sunrise.getAdapter().getCyNetworkViewManager());		
		setPreferredMenu(sunrise.name);
	}
	class C2String implements Comparable<C2String>{
		String ks=null,vs=null;
		public C2String(String k,String v){ks=k;vs=v;}
		public int compareTo(C2String s) {
			return ks.compareTo(s.ks);
		}
	}
	public void actionPerformed(ActionEvent e) {
		CyApplicationManager applicationManager=sunrise.getAdapter().getCyApplicationManager();
		CySwingApplication swingApplication=sunrise.getAdapter().getCySwingApplication();
		CyNetwork compactNet=applicationManager.getCurrentNetwork();
		List<CyNode> selectedInCompact=CyTableUtil.getNodesInState(compactNet,"selected",true);
		CyUtils cu=new CyUtils(compactNet);
		HashSet<String> selInCompactNames=new HashSet<String>(selectedInCompact.size());
		for(CyNode node:selectedInCompact) selInCompactNames.add(cu.getName(node));
		GeneticUtils gu=new GeneticUtils(compactNet);
		CyNetwork refNet=gu.ifRefNet();
		if(refNet==null) return;
		String fateCol=gu.ifFateCol(refNet);
		if(fateCol==null) return;
		CyUtils refCu=new CyUtils(refNet);
		ArrayList<C2String> fateNode=new ArrayList<C2String>();
		for(CyNode node:refNet.getNodeList()){
			String fateNodeName=refCu.getString(fateCol,node);
			if(selInCompactNames.contains(fateNodeName)){
				fateNode.add(new C2String(fateNodeName,refCu.getName(node)));
			}
		}
		Collections.sort(fateNode);
		StringBuffer text=new StringBuffer();
		text.append("inCompact\tinReference\r\n");
		for(C2String c2s:fateNode){
			text.append(c2s.ks);
			text.append("\t");
			text.append(c2s.vs);
			text.append("\r\n");
		}
		new TextBox(swingApplication.getJFrame(),title+" For Score "+cu.getString(gu.scoreIdCol),0.2,0.8,text.toString()).setVisible(true);
	}
}


