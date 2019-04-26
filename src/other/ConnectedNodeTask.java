// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package other;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Stack;
import main.sunrise;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
/**
 * Connected Nodes in table by BFS
 */
public class ConnectedNodeTask  implements Task{
	ConnectedNodes a;
	final String nodeColumnName="Connected";
	CyNetwork network;
	private ArrayList<CyNode> nodes;
	private ArrayList<BitSet> adjacentN;
	TaskMonitor monitor;
	boolean stop=false;
	public ConnectedNodeTask(ConnectedNodes action){
		a=action;
	}
	private void initAdjacent(){
		adjacentN=new ArrayList<BitSet>();
		nodes=(ArrayList<CyNode>) network.getNodeList();
		for(int n=0;n<nodes.size();n++) adjacentN.add(new BitSet());
		ArrayList<CyEdge> edges=(ArrayList<CyEdge>) network.getEdgeList();
		for(int e=0;e<edges.size();e++){
			int src=nodes.indexOf(edges.get(e).getSource());
			int tgt=nodes.indexOf(edges.get(e).getTarget());
			adjacentN.get(src).set(tgt);
			adjacentN.get(tgt).set(src);
			if(stop) return;
		}	
	}
	ArrayList<BitSet> groupConnect(){
		ArrayList<BitSet> groups=new ArrayList<BitSet>();
		Stack<Integer> stack=new Stack<Integer>();
		BitSet nodeSet=new BitSet();
		nodeSet.set(0,nodes.size());
		int start=0;
		while(start>-1){
			BitSet gone=new BitSet();
			stack.push(start);
			while(!stack.isEmpty()){
				int node=stack.pop();
				if(!gone.get(node)){
					gone.set(node);
					nodeSet.clear(node);
					for(int a=adjacentN.get(node).nextSetBit(0);a>-1;a=adjacentN.get(node).nextSetBit(a+1)) if(nodeSet.get(a)) stack.push(a);
				}
				if(stop) return null;
			}
			if(gone.cardinality()>1) groups.add(gone);
			start=nodeSet.nextSetBit(0);
		}
		return groups;
	}
	public void run(TaskMonitor taskMonitor) throws Exception {
		monitor=taskMonitor;
		monitor.setTitle(ConnectedNodes.title);
		network=sunrise.getAdapter().getCyApplicationManager().getCurrentNetwork();
		monitor.setStatusMessage("Build Network");
		initAdjacent();if(stop) return;
		monitor.setStatusMessage("Search Connected Groups");
		ArrayList<BitSet> groups=groupConnect();if(stop) return;
		monitor.setStatusMessage("Write Groups in Table");
		if(network.getDefaultNodeTable().getColumn(nodeColumnName)==null) 
			network.getDefaultNodeTable().createColumn(nodeColumnName,String.class,false);
		BitSet all=new BitSet();
		all.set(0,nodes.size());
		for(int g=0;g<groups.size();g++){
			for(int n=groups.get(g).nextSetBit(0);n>-1;n=groups.get(g).nextSetBit(n+1)){
				network.getRow(nodes.get(n)).set(nodeColumnName,"CN"+(g+1));
				all.clear(n);
			}
		}
		for(int n=all.nextSetBit(0);n>-1;n=all.nextSetBit(n+1)){
			network.getRow(nodes.get(n)).set(nodeColumnName,"_NC");
		}
	}
	public void cancel() {
		stop=true;
	}
}
