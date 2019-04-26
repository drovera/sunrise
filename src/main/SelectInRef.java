// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package main;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.List;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import utils.CyUtils;
import utils.GeneticUtils;
/**
 * Selected nodes in reference network which are selected in compact network, groups included
 * Use fateCol to do the link between reference network and compacted network
 */
public class SelectInRef extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	final static String title="  Select in Reference Selected in Compact";
	public SelectInRef(){
		super(title,sunrise.getAdapter().getCyApplicationManager(),"network",sunrise.getAdapter().getCyNetworkViewManager());
		setPreferredMenu(sunrise.name);
	}
	public void actionPerformed(ActionEvent e) {
		CyNetwork compactNet=sunrise.getAdapter().getCyApplicationManager().getCurrentNetwork();
		CyUtils cu=new CyUtils(compactNet);
		GeneticUtils gu=new GeneticUtils(compactNet);
		List<CyNode> selectInComp=CyTableUtil.getNodesInState(compactNet,"selected",true);
		HashSet<String> selectedInCompact=new HashSet<String>(selectInComp.size());
		for(CyNode node:selectInComp) selectedInCompact.add(cu.getName(node));
		gu.selectInRef(selectedInCompact);
	}	
}
