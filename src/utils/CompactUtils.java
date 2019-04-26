// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package utils;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import org.cytoscape.model.CyNetwork;
import utils.Dijkstra.C1int1double;
/**
 * Complete NetUtils by fields and methods used in compacting reference network
 * based on position in groups
 */
public class CompactUtils extends NetUtils{
	protected int[] posGroup;
	protected int cptLastNeg;
	protected String[] cptName;
	protected double[] cptScore;
	protected int groupNb;
	protected ArrayList<HashSet<C1int1double>> adjacentDK;
	public CompactUtils(CyNetwork network){
		super(network);
	}
	/**
	 * Prepare grouping positive nodes
	 */
	protected void connectPosGroup(){
		posGroup=new int[nodeNb];
		for(int n=0;n<nodeNb;n++) posGroup[n]=-1;
		Stack<Integer> stack=new Stack<Integer>();
		BitSet nodeSet=new BitSet();
		nodeSet.set(firstNull,nodeNb);
		groupNb=0;
		int start=firstPos;
		while(start>-1){
			BitSet gone=new BitSet();
			int node=-1;
			stack.push(start);
			while(!stack.isEmpty()){
				node=stack.pop();
				if(!gone.get(node)){
					gone.set(node);
					nodeSet.clear(node);
					for(int a=adjacentN.get(node).nextSetBit(firstNull);a>-1;a=adjacentN.get(node).nextSetBit(a+1)){
						if(nodeSet.get(a)) stack.push(a);
					}
				}
			}
			for(int n=gone.nextSetBit(firstNull);n>-1;n=gone.nextSetBit(n+1)) posGroup[n]=groupNb;
			groupNb++;
			start=nodeSet.nextSetBit(firstPos);
		}
	}
	/**
	 * include not grouped null score nodes in nearest positive nodes
	 */
	protected void groupNullToNeg(){
		for(int n=firstNull;n<firstPos;n++){
			if(posGroup[n]==-1){
				int to=adjacentN.get(n).nextSetBit(0);
				if(posGroup[to]==-1){
					posGroup[to]=groupNb;
					posGroup[n]=groupNb;
					groupNb++;
				}else posGroup[n]=posGroup[to];
			}
		}
	}
	/**
	 * renumber groups going through a tree
	 * compute features of groups to do as with simple nodes
	 */
	protected void setCompactTree(){
		ArrayList<ArrayList<Integer>> cptTree=new ArrayList<ArrayList<Integer>>();
		HashMap<Integer,Integer> groups=new HashMap<Integer,Integer>();
		for(int n=0;n<nodeNb;n++){
			if(posGroup[n]==-1){
				ArrayList<Integer> list=new ArrayList<Integer>();
				list.add(n);
				cptTree.add(list);
			}else{
				if(groups.keySet().contains(posGroup[n])){
					cptTree.get(groups.get(posGroup[n])).add(n);
				}else{
					ArrayList<Integer> list=new ArrayList<Integer>();
					list.add(n);
					cptTree.add(list);
					groups.put(posGroup[n],cptTree.size()-1);
				}
			}
		}
		for(int i=0;i<cptTree.size();i++) for(int j=0;j<cptTree.get(i).size();j++) posGroup[cptTree.get(i).get(j)]=i;
		cptName=new String[cptTree.size()];
		cptScore=new double[cptTree.size()];
		for(int i=0;i<cptTree.size();i++){
			ArrayList<Integer> list=cptTree.get(i);
			if(list.get(0)<firstNull){
				cptName[i]=getName(list.get(0));
				cptScore[i]=scores[list.get(0)];
			}else{
				cptName[i]="p_"+getName(list.get(list.size()-1));
				cptScore[i]=0.0;
				for(int j=0;j<list.size();j++) cptScore[i]=cptScore[i]+scores[list.get(j)]; 
			}
		}
		groupNb=cptTree.size();
		cptLastNeg=0;
		while(cptScore[cptLastNeg]<0.0) cptLastNeg++;
	}
	/**
	 * build the adjacency matrix to be used by Disjkstra
	 * duplicate edges as oriented edges
	 * weights are the reverse score of the end for positive and 0.0 for negative
	 * loops are deleted
	 */
	protected ArrayList<HashSet<C1int1double>> forDisjkstra(){
		Dijkstra dk=new Dijkstra();
		adjacentDK=new ArrayList<HashSet<C1int1double>>(groupNb);
		for(int n=0;n<groupNb;n++) adjacentDK.add(new HashSet<C1int1double>());
		for(int n=0;n<nodeNb;n++){
			for(int a=adjacentN.get(n).nextSetBit(0);a>-1;a=adjacentN.get(n).nextSetBit(a+1)){
				if(cptScore[posGroup[a]]>0) adjacentDK.get(posGroup[n]).add(dk.new C1int1double(posGroup[a],0.0));
				else adjacentDK.get(posGroup[n]).add(dk.new C1int1double(posGroup[a],-cptScore[posGroup[a]]));
			}
		}
		for(int n=0;n<adjacentDK.size();n++){
			C1int1double loop=dk.new C1int1double(n);
			if(adjacentDK.get(n).contains(loop)) adjacentDK.get(n).remove(loop);
		}
		return adjacentDK;
	}
}
