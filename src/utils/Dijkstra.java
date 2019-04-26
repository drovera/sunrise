// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package utils;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
/*
 * Shortest path using Dijkstra's algorithm
 * by https://www.baeldung.com/java-dijkstra
 * HashSet is used to remove duplicated edges
 * use getDistFromSource(int tgt) after shortestPathFromSource(int source)
 * BitSets got by shortestPathFromSource include sources but not targets
 * CShortestPath and C1int1double are utility classes
 */
public class Dijkstra {
	private ArrayList<HashSet<C1int1double>> adjacents;
	private double[] distance;
	private ArrayList<BitSet> shortestPaths;
	public class C1int1double implements Comparable<C1int1double> {
		public int i1;
		public double val;
		public C1int1double(){i1=-1;val=Double.MIN_VALUE;}
		public C1int1double(int i){i1=i;}
		public C1int1double(int i,double v){i1=i;this.val=v;}
		public boolean equals(Object o){
			return(((C1int1double)o).i1==i1);
		}
		public int hashCode(){return i1;}
		public int compareTo(C1int1double arg) {
			if(arg.val<val) return 1;else if(val<arg.val) return -1;
			return 0;
		}
		public String toString(){
			return i1+":"+val;
		}
	}
	public class CShortestPath implements Comparable<CShortestPath>{
		public int src;
		public int tgt;
		public double dist;
		public BitSet path;
		public CShortestPath(){
			this.src=-1;
			this.tgt=-1;
			this.dist=Double.POSITIVE_INFINITY;
			this.path=null;
		}
		public CShortestPath(int src, int tgt, double dist, BitSet path){
			this.src=src;
			this.tgt=tgt;
			this.dist=dist;
			this.path=path;
		}
		public CShortestPath(int src){
			this.src=src;
			this.tgt=-1;
			this.dist=Double.POSITIVE_INFINITY;
			this.path=null;
		}
		public int compareTo(CShortestPath shortestPath){
			if(shortestPath.dist<dist) return 1;else if(dist<shortestPath.dist) return -1;
			return 0;
		}
	}
	public Dijkstra(){}
	public Dijkstra(ArrayList<HashSet<C1int1double>> adjacency){
		this.adjacents=adjacency;
		distance=new double[adjacents.size()];
		shortestPaths=new ArrayList<BitSet>();
		for(int n=0;n<adjacents.size();n++) shortestPaths.add(new BitSet());
	}
	public double getDistFromSource(int tgt){
		return distance[tgt];
	}
	public ArrayList<BitSet> shortestPathFromSource(int source) {
		for(int n=0;n<adjacents.size();n++) shortestPaths.get(n).clear();
		for(int n=0;n<adjacents.size();n++) distance[n]=Double.MAX_VALUE;
		distance[source]=0;
		HashSet<Integer> settled = new HashSet<Integer>();
		HashSet<Integer> unsettled = new HashSet<Integer>();
		unsettled.add(source);
		while (unsettled.size() != 0) {
			int currentNode = getLowestDistanceNode(unsettled);
			unsettled.remove(currentNode);
			for (C1int1double adjacencyPair:adjacents.get(currentNode)) {
				int adjacentNode = adjacencyPair.i1;
				double length = adjacencyPair.val;
				if (!settled.contains(adjacentNode)) {
					searchMinimumDistance(adjacentNode, length, currentNode);
					unsettled.add(adjacentNode);
				}
			}
			settled.add(currentNode);
		}
		return shortestPaths;
	}
	private void searchMinimumDistance(int evaluationNode, double length, int sourceNode) {
		double sourceDistance = distance[sourceNode];
		if (sourceDistance + length < distance[evaluationNode]) {
			distance[evaluationNode]=sourceDistance + length;
			BitSet shortestPath=(BitSet) shortestPaths.get(sourceNode).clone();
			shortestPath.set(sourceNode);
			shortestPaths.set(evaluationNode, shortestPath);
		}
	}
	private int getLowestDistanceNode(HashSet<Integer> unsettled) {
		int lowestDistanceNode = -1;
		double lowestDistance = Double.MAX_VALUE;
		for (int node : unsettled) {
			double nodeDistance = distance[node];
			if (nodeDistance < lowestDistance) {
				lowestDistance = nodeDistance;
				lowestDistanceNode = node;
			}
		}
		return lowestDistanceNode;
	}
}