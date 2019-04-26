// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package utils;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;
import main.sunrise;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import utils.Dijkstra.C1int1double;
/**
 * root class building the network
 * providing common fields and methods
 * used by several core classes
 */
public class NetUtils{
	private CyNetwork net;
	protected ArrayList<CyNode> nodes;
	protected int nodeNb;
	protected int firstNull;
	protected int firstPos;
	protected Double[] scores;
	protected int[] order;
	protected int[] reverse;
	protected ArrayList<BitSet> adjacentN;
	protected CyUtils cu;
	/**
	* Constructor sorting nodes by score, scores are sorted too
	* reference index = order[after sorting index], after sorting index = reverse[reference index]
	* utility functions to get data with the right index
	*/
	public NetUtils(CyNetwork network){
		net=network;
	}
	protected void buildNetwork(){
		nodes=(ArrayList<CyNode>)net.getNodeList();
		nodeNb=nodes.size();
		ArrayList<CyEdge> edges=(ArrayList<CyEdge>)net.getEdgeList();
		adjacentN=new ArrayList<BitSet>();
		scores=new Double[nodeNb];
		for(int n=0;n<nodeNb;n++){
			adjacentN.add(new BitSet());
			scores[n]=net.getRow(nodes.get(n)).get(sunrise.scoreCol,Double.class);
		}
		order=increaseByIndex(scores);
		firstNull=0;
		while(scores[firstNull]<0.0) firstNull++;
		firstPos=firstNull;
		while(!(scores[firstPos]>0.0)) firstPos++;
		reverse=new int[nodeNb];
		for(int n=0;n<nodeNb;n++) reverse[order[n]]=n;
		for(int e=0;e<edges.size();e++){
			int src=reverse[nodes.indexOf(edges.get(e).getSource())];
			int tgt=reverse[nodes.indexOf(edges.get(e).getTarget())];
			adjacentN.get(src).set(tgt);
			adjacentN.get(tgt).set(src);
		}
		cu=new CyUtils(net);	
	}
	private int[] increaseByIndex(Double[] keys){
		int[] index=new int[keys.length];
		for (int i=0;i<keys.length;i++) index[i]=i;
		for (int i=1;i<keys.length;i++){
			int j=i;
			double tk=keys[i];;
			int tv=index[i];
			while ((j > 0) && (keys[j-1]>tk)){
				keys[j]=keys[j-1];
				index[j]=index[j-1];
				j--;
			}
			keys[j]=tk;
			index[j]=tv;
		}
		return index;
	}
	protected String getName(int node){
		return cu.getName(nodes.get(order[node]));
	}
	public double getScore(BitSet group){
		double s=0.0;
		for(int n=group.nextSetBit(0);n>-1;n=group.nextSetBit(n+1))	s=s+scores[n];
		return s;
	}
	protected ArrayList<String> getNames(){
		ArrayList<String> names=new ArrayList<String>(nodeNb);
		for(int n=0;n<nodeNb;n++) names.add(getName(n));
		return names;
	}
	/**
	* set of function to delete negative nodes while keeping cardinality od positive
	* based on adjusting adjacency matrix
	*/
	private BitSet connected(ArrayList<BitSet> adj,int start){
		BitSet gone=new BitSet();
		Stack<Integer> stack=new Stack<Integer>();
		stack.push(start);
		while(!stack.isEmpty()){
			int node=stack.pop();
			if(!gone.get(node)){
				gone.set(node);
				for(int a=adj.get(node).nextSetBit(0);a>-1;a=adj.get(node).nextSetBit(a+1)) stack.push(a);
			}
		}
		return gone;
	}
	private ArrayList<BitSet> delNode(ArrayList<BitSet> adj,int node){
		ArrayList<BitSet> newAdj=new ArrayList<BitSet>();
		for(int n=0;n<nodeNb;n++){
			BitSet ta=adj.get(n);
			if(ta==null) newAdj.add(null);
			else{
				BitSet a=(BitSet)ta.clone();
				a.clear(node);
				newAdj.add(a);
			}
		}
		newAdj.set(node, null);
		return newAdj;
	}
	private ArrayList<BitSet> adjustAdj(BitSet group){
		ArrayList<BitSet> newAdj=new ArrayList<BitSet>();
		for(int n=0;n<nodeNb;n++){
			if(group.get(n)){
				BitSet a=(BitSet)adjacentN.get(n).clone();
				a.and(group);
				newAdj.add(a);
			}else newAdj.add(null);
		}
		return newAdj;
	}
	public BitSet delNegKeepPos(BitSet group){
		int refCard=group.get(firstPos,nodeNb).cardinality();
		ArrayList<BitSet> adj=adjustAdj(group);
		BitSet noLinkToPos=(BitSet)group.clone();
		noLinkToPos.clear(firstPos,nodeNb);
		for(int n=group.nextSetBit(firstPos);n>-1;n=group.nextSetBit(n+1)){
			BitSet a=adj.get(n);
			if(a.cardinality()==1) noLinkToPos.clear(a.nextSetBit(0));
		}
		int start=group.previousSetBit(nodeNb);
		BitSet keep=(BitSet)group.clone();
		for(int n=noLinkToPos.nextSetBit(0);n>-1;n=noLinkToPos.nextSetBit(n+1)){
			ArrayList<BitSet> adjtmp=delNode(adj,n);
			if(adjtmp.get(start)==null)	continue;
			BitSet bstmp=connected(adjtmp,start);
			int card=bstmp.get(firstPos,nodeNb).cardinality();
			if(card==refCard){
				adj=adjtmp;
				keep=bstmp;
			}
		}
		return keep;
	}
/*
 * Test if a set of nodes is connected and transform selection in BitSet
 */
	protected boolean ifConnect(BitSet set) {
		if (set.cardinality()==0) return false;
		if (set.cardinality()==1) return true;
		BitSet pass = (BitSet) set.clone();
		LinkedList<Integer> nodeQueue = new LinkedList<Integer>();
		int root = set.nextSetBit(0);
		nodeQueue.add(root);
		while (!nodeQueue.isEmpty()) {
			int node = nodeQueue.remove();
			if (pass.get(node)) {
				for (int a = adjacentN.get(node).nextSetBit(0); a>-1; a = adjacentN.get(node).nextSetBit(a + 1)) nodeQueue.add(a);
				pass.clear(node);
			}
		}
		if (pass.isEmpty())	return true; else return false;
	}
	protected BitSet selectToConnectBitSet(){
		BitSet selection=new BitSet();
		for(int n=0;n<nodes.size();n++)	if(net.getRow(nodes.get(n)).get("selected",Boolean.class)) selection.set(reverse[n]);
		if(ifConnect(selection)) return selection; else return null;
	}
/*
 * Build the adjacency based on this network to be used by Dijkstra
 */
	protected ArrayList<HashSet<C1int1double>> forDisjkstra(){
		Dijkstra dk=new Dijkstra();
		ArrayList<HashSet<C1int1double>> adjacentDK=new ArrayList<HashSet<C1int1double>>(nodeNb);
		for(int n=0;n<nodeNb;n++) adjacentDK.add(new HashSet<C1int1double>());
		for(int n=0;n<firstPos;n++){
			for(int a=adjacentN.get(n).nextSetBit(0);(a<firstPos)&&(a>-1);a=adjacentN.get(n).nextSetBit(a+1)) 
				adjacentDK.get(n).add(dk.new C1int1double(a,-scores[a]));
			for(int a=adjacentN.get(n).nextSetBit(firstPos);a>-1;a=adjacentN.get(n).nextSetBit(a+1))
				adjacentDK.get(n).add(dk.new C1int1double(a,0.0));
		}
		for(int n=firstPos;n<nodeNb;n++){
			for(int a=adjacentN.get(n).nextSetBit(0);(a<firstPos)&&(a>-1);a=adjacentN.get(n).nextSetBit(a+1)) 
				adjacentDK.get(n).add(dk.new C1int1double(a,-scores[a]));
			for(int a=adjacentN.get(n).nextSetBit(firstPos);a>-1;a=adjacentN.get(n).nextSetBit(a+1)){
				adjacentDK.get(n).add(dk.new C1int1double(a,0.0));
			}
		}
		return adjacentDK;
	}
}
