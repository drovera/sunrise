// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package main;
import java.awt.event.ActionEvent;
import java.util.Iterator;
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
/**
 * Display selected nodes, their scores and the sum
 */
public class SelectedNodeScore extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	final static String title="  Score of Selected Nodes";
	public SelectedNodeScore(){
		super(title,sunrise.getAdapter().getCyApplicationManager(),"network",sunrise.getAdapter().getCyNetworkViewManager());
		setPreferredMenu(sunrise.name);
	}
	public void actionPerformed(ActionEvent e) {
		CyApplicationManager applicationManager=sunrise.getAdapter().getCyApplicationManager();
		CySwingApplication swingApplication=sunrise.getAdapter().getCySwingApplication();
		CyNetwork network=applicationManager.getCurrentNetwork();
		List<CyNode> selected=CyTableUtil.getNodesInState(network,"selected",true);
		CyUtils cu=new CyUtils(network);
		GeneticUtils gu=new GeneticUtils(network);
		if(!gu.rightScoreCol()) if(!gu.selectScoreCol()) return;
		StringBuffer text=new StringBuffer("Nodes\t"+sunrise.scoreCol+"\r\n");
		double score=0.0;
		Iterator<CyNode> iter=selected.iterator();
		while(iter.hasNext()){
			CyNode node=iter.next();
			double scr=cu.getDouble(sunrise.scoreCol,node);
			score=score+scr;;
			text.append(cu.getName(node));
			text.append("\t");
			text.append(scr);
			text.append("\r\n");
		}	
		text.append(sunrise.scoreCol+" Sum");
		text.append("\t");
		text.append(score);
		new TextBox(swingApplication.getJFrame(),title+" of "+cu.getName(),0.2,0.8,text.toString()).setVisible(true);
	}
}
