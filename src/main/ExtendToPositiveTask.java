// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package main;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import utils.Dijkstra;
import utils.Dijkstra.CShortestPath;
import utils.NetUtils;
/*
  * Task class corresponding to ExtendToPositive
  * Result in a window ComboText to select node added in selection
 */
public class ExtendToPositiveTask extends NetUtils implements Task{
	private TaskMonitor monitor;
	boolean stop=false;
	ExtendToPositive a;
	BitSet selection;
	double selectionScore;
	ArrayList<CShortestPath> shortestPaths;
	public ExtendToPositiveTask(ExtendToPositive action){
		super(action.net);
		a=action;
	}
	private void nodesFromShortest(){
		selectionScore=getScore(selection);
		Dijkstra dk=new Dijkstra(forDisjkstra());
		shortestPaths=new ArrayList<CShortestPath>();
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
	}
	private void updateScore(){
		for(CShortestPath searchSP:shortestPaths) searchSP.dist=selectionScore+getScore(searchSP.path);
		Collections.sort(shortestPaths,Collections.reverseOrder());
	}
	public void run(TaskMonitor taskMonitor) throws Exception {
		monitor=taskMonitor;
		monitor.setTitle(ExtendToPositive.title);
		monitor.setStatusMessage("Build Network");
		buildNetwork();if(stop) return;
		selection=selectToConnectBitSet();
		if(selection==null){
			a.isConnect=false;
			return;
		}else a.isConnect=true;
		monitor.setStatusMessage("Search Path Connecting Positive Nodes");
		nodesFromShortest();if(stop) return;
		updateScore();
		ArrayList<String> orderNames=getNames();
		StringBuffer text=new StringBuffer();
		text.append("Selection (Score Name:");text.append(sunrise.scoreCol);text.append(")\t");
		text.append(selectionScore);text.append("\r\n");
		a.cb.fill(text,orderNames,shortestPaths,a.net);
	}
	public void cancel() {
		stop=true;
	}
}

