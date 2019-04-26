// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package main;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import utils.CyUtils;
import utils.Dijkstra;
import utils.Dijkstra.CShortestPath;
import utils.NetUtils;
/*
 * Task class corresponding to IterateToPositive
 * Searching stops when score does not increase
 * Result in a window TextMenu
 */
public class IterateToPositiveTask extends NetUtils implements Task{
	private TaskMonitor monitor;
	boolean stop=false;
	IterateToPositive a;
	BitSet selection;
	double selectionScore;
	ArrayList<CShortestPath> shortestPaths;
	public IterateToPositiveTask(IterateToPositive action){
		super(action.net);
		a=action;
	}
	private void iterNodesFromShortest(){
		selectionScore=getScore(selection);
		Dijkstra dk=new Dijkstra(forDisjkstra());
		shortestPaths=new ArrayList<CShortestPath>();
		while(true){
			for(int n=firstPos;n<nodes.size();n++) if(!selection.get(n)) shortestPaths.add(dk.new CShortestPath(n));
			for(CShortestPath searchSP:shortestPaths){
				ArrayList<BitSet> sp=dk.shortestPathFromSource(searchSP.src);
				for(int t=selection.nextSetBit(0);t>-1;t=selection.nextSetBit(t+1)){
					if(stop) return;
					double dist=dk.getDistFromSource(t);
					if(dist<searchSP.dist){
						searchSP.dist=dist;
						searchSP.tgt=t;
						searchSP.path=(BitSet)sp.get(t).clone();
					}
				}
			}
			for(CShortestPath searchSP:shortestPaths) searchSP.dist=selectionScore+getScore(searchSP.path);
			CShortestPath csp=Collections.max(shortestPaths);
			if(!(csp.dist>selectionScore)||stop) break;
			selection.or(csp.path);
			selectionScore=csp.dist;
			monitor.setStatusMessage("Score: "+selectionScore);
			shortestPaths.clear();
		}
	}
	private void result(String line){
		a.tm.appendEnd(line+" Score: "+selectionScore+" of "+selection.cardinality()+" nodes\r\n");
	}
	public void run(TaskMonitor taskMonitor) throws Exception {
		monitor=taskMonitor;
		monitor.setTitle(IterateToPositive.title);
		monitor.setStatusMessage("Build Network");
		buildNetwork();if(stop) return;
		selection=selectToConnectBitSet();
		if(selection==null){
			a.isConnect=false;
			return;
		}else a.isConnect=true;
		selectionScore=getScore(selection);
		result("Start Selection");
		iterNodesFromShortest();
		result("Final Selection");
		a.tm.toFront();
		a.tm.appendEnd("List Of Nodes:\r\n");
		for(int n=selection.nextSetBit(0);n>-1;n=selection.nextSetBit(n+1)){
			a.tm.appendEnd(getName(n)+"\r\n");
			a.net.getRow((new CyUtils(a.net)).getCyNode(getName(n))).set("selected",true);
		}
	}
	public void cancel() {
		stop=true;
	}
}
