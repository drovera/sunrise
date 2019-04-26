// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package main;
import java.util.ArrayList;
import java.util.BitSet;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import utils.*;
import utils.Dijkstra.C1int1double;
/**
 * Group connected positive and null score nodes in one node, connect left null score nodes to nearest negative nodes
 * Create edges linking positive groups and negative nodes as an adjacency matrix avoiding duplicated edges,
 * opposite of scores are transferred to 2 directed parallel reverse edges
 * Extract nodes from the shortest paths of negative nodes by Dijkstra
 * Update the correspondence between reference and compact networks as fate column of every nodes in reference network
 * Create the compacted network, nodes are located as in the initial network and group at the mean of coordinates
 */
public class CompactNetTask extends CompactUtils implements Task{
	private TaskMonitor monitor;
	BitSet keptNodes;
	private CyUtils refCu;
	GeneticUtils compGu;
	private double[] groupX;
	private double[] groupY;
	private CompactNet a;
	final String notInCompact="_NotInCompact";
	private CyNetworkView view;
	private boolean stop=false;
	public CompactNetTask(CompactNet action){
		super(action.refNet);
		a=action;
		refCu=new CyUtils(a.refNet);
	}
	private void nodesFromShortest(){
		int w=adjacentDK.size()-firstPos;
		int p=0;
		keptNodes=new BitSet();
		Dijkstra dk=new Dijkstra(adjacentDK);
		for(int s=cptLastNeg+1;s<adjacentDK.size();s++){
			if(stop) return;
			monitor.setProgress(1.0*p++/w);
			keptNodes.set(s);
			ArrayList<BitSet> shortestPaths=dk.shortestPathFromSource(s);
			for(int t=cptLastNeg+1;t<adjacentDK.size();t++) if(t>cptLastNeg) keptNodes.or(shortestPaths.get(t));
		}
	}
	private void groupCoord(){
		view=sunrise.getAdapter().getCyApplicationManager().getCurrentNetworkView();
		if(view==null) return;
		int[] groupSize=new int[groupNb];
		groupX=new double[groupNb];
		groupY=new double[groupNb];
		for(int g=0;g<groupNb;g++){
			groupSize[g]=0;
			groupX[g]=0.0;
			groupY[g]=0.0;
		}		
		for(int n=0;n<nodeNb;n++){
			int g=posGroup[n];
			groupSize[g]++;
			groupX[g]=groupX[g]+view.getNodeView(nodes.get(order[n])).getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
			groupY[g]=groupY[g]+view.getNodeView(nodes.get(order[n])).getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
		}
		for(int g=0;g<groupNb;g++){
			groupX[g]=groupX[g]/groupSize[g];
			groupY[g]=groupY[g]/groupSize[g];
		}
	}
	private void updateFate(){
		compGu=new GeneticUtils(sunrise.getAdapter().getCyNetworkFactory().createNetwork());
		String fateName=compGu.fatePrefix+sunrise.scoreCol;
		if(a.refNet.getDefaultNodeTable().getColumn(fateName)==null) a.refNet.getDefaultNodeTable().createColumn(fateName,String.class,false);
		for(int n=0;n<nodeNb;n++){
			CyRow row=refCu.net.getRow(nodes.get(order[n]));
			if(keptNodes.get(posGroup[n])) row.set(fateName,cptName[posGroup[n]]); else	row.set(fateName,notInCompact);
		}
	}
	private void createCompactNet(){
		CyNetworkManager networkManager=sunrise.getAdapter().getCyNetworkManager();
		CyNetworkViewFactory viewFactory=sunrise.getAdapter().getCyNetworkViewFactory();
		CyNetworkViewManager viewManager=sunrise.getAdapter().getCyNetworkViewManager();
		CyNetwork compactNet=compGu.net;
		compactNet.getRow(compactNet).set(CyNetwork.NAME,a.cName);
		networkManager.addNetwork(compactNet);
		compGu.createRefScrIdCol(refCu.getName(),sunrise.scoreCol);
		compactNet.getDefaultNodeTable().createColumn(sunrise.scoreCol,Double.class,false);
		monitor.setStatusMessage("Create Nodes of the Compact Network");
		CyNode[] newNodes=new CyNode[groupNb];
		for(int i=0;i<adjacentDK.size();i++){
			newNodes[i]=null;
			if(keptNodes.get(i)){
				CyNode newNode=compactNet.addNode();
				newNodes[i]=newNode;
				compactNet.getRow(newNode).set(CyNetwork.NAME,cptName[i]);
				compactNet.getRow(newNode).set(sunrise.scoreCol,cptScore[i]);
			}
		}
		monitor.setStatusMessage("Create Edges of the Compact Network");
		for(int i=0;i<adjacentDK.size();i++){
			if(keptNodes.get(i)){
				for(C1int1double a:adjacentDK.get(i)){
					if(keptNodes.get(a.i1)&&(i<a.i1)){
						CyEdge edge=compactNet.addEdge(newNodes[i],newNodes[a.i1],false);
						compactNet.getRow(edge).set(CyNetwork.NAME,cptName[i]+"(pp)"+cptName[a.i1]);
					}
				}
			}
		}
		GeneticUtils.Param param=compGu.createParamCol();
		param.setDefault();
		param.write();
		CyNetworkView newView=viewFactory.createNetworkView(compactNet);
		viewManager.addNetworkView(newView);
		sunrise.getAdapter().getCyApplicationManager().setCurrentNetworkView(newView);
		if(view==null) return;
		monitor.setStatusMessage("Set Coordinates of the Compact Network");
		for(int i=0;i<groupNb;i++){
			if(keptNodes.get(i)){
				newView.getNodeView(newNodes[i]).setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION,groupX[i]);
				newView.getNodeView(newNodes[i]).setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION,groupY[i]);
				keptNodes.clear(i);
			}	
		}
		newView.updateView();
	}
	public void run(TaskMonitor taskMonitor) throws Exception {
		monitor=taskMonitor;
		monitor.setTitle(CompactNet.title+": "+refCu.getName()+" with as Score: "+sunrise.scoreCol);
		monitor.setStatusMessage("Build Network");
		buildNetwork();if(stop) return;
		monitor.setStatusMessage("Build Groups of Nodes");
		connectPosGroup();if(stop) return;
		groupNullToNeg();if(stop) return;
		setCompactTree();if(stop) return;
		groupCoord();if(stop) return;
		monitor.setStatusMessage("Link Positive Nodes By Highest Score Paths");
		forDisjkstra();if(stop) return;	
		nodesFromShortest();
		if(stop) return;
		monitor.setStatusMessage("Update The Fate Column of The Reference Network");
		updateFate();
		createCompactNet();
	}
	public void cancel() {
		stop=true;
	}
}