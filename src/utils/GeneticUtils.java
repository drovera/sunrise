// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package utils;
import java.util.BitSet;
import java.util.HashSet;
import javax.swing.JOptionPane;
import main.sunrise;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
/**
* values and functions used in genetic algorithm
*/
public class GeneticUtils {
	public CyNetwork net;
	public final String refNameCol="Reference_Net";
	public final String fatePrefix="Fate_C_";
	public final String scoreIdCol="Score_Id";
	public GeneticUtils(){this.net=null;}
	public GeneticUtils(CyNetwork net){ this.net=net;}
	/**
	 * Classes and functions to manager the parameters used by genetic algorithm
	 */
	public class Param{	
		public int population;
		public int generationNb;
		public int mutationNb;
		public long seed=0L;
		public int salvo=100;
		public boolean oneShot=false;
		public double bestScore=0.0;
		public final String populationCol="Population";
		public final String generationCol="Generation_Nb";
		public final String mutationCol="Mutation_Nb";
		public final String seedCol="Seed";
		public final String bestScoreCol="Best_Score";
		public final String lbSP=" Size Of Population ";
		public final String lbNG=" Number of Generations ";
		public final String lbNM=" Number of Mutation ";
		public final String rb1S="Several Researches, their Number:";
		public final String rb2S="One Research Starting from Seed:";
		public void set(Param p){
			this.population=p.population;
			this.generationNb=p.generationNb;
			this.mutationNb=p.mutationNb;
			this.seed=p.seed;
		}
		public void write(){
			net.getRow(net).set(populationCol,population);
			net.getRow(net).set(generationCol,generationNb);
			net.getRow(net).set(mutationCol,mutationNb);
			net.getRow(net).set(seedCol,seed);
			net.getRow(net).set(bestScoreCol,bestScore);
		}
		public boolean read(){
			try{
				population=net.getRow(net).get(populationCol,Integer.class);
				generationNb=net.getRow(net).get(generationCol,Integer.class);
				mutationNb=net.getRow(net).get(mutationCol,Integer.class);
				seed=net.getRow(net).get(seedCol,Long.class);
				return true;
			}
			catch(Exception e){
				return false;	
			}
		}
		public void setDefault(){
			population=net.getNodeCount()/10;
			generationNb=100*population;
			mutationNb=net.getNodeCount()/10;
			bestScore=0.0;
			write();
		}
	}
	/**
	 * Class to link score and group of nodes for ranking
	 */
	public class Item implements Comparable<Item>{
		public BitSet b;
		public double s;
		public Item(BitSet b,double s){
			this.b=b;
			this.s=s;
		}
		public int compareTo(Item item) {
			if(item.s<s) return 1;else if(s<item.s) return -1;
			return 0;	
		}
	}
	/**
	 * Create and Update reference and score identifier column
	 */
	public boolean createRefScrIdCol(String reference,String scoreId){
		net.getDefaultNetworkTable().createColumn(refNameCol,String.class,false);
		net.getDefaultNetworkTable().createColumn(scoreIdCol,String.class,false);
		net.getRow(net).set(refNameCol,reference);
		net.getRow(net).set(scoreIdCol,scoreId);
		return true;
	}
	/**
	 * Create parameters column and return parameter
	 */	
	public Param createParamCol(){
		GeneticUtils.Param p=(new GeneticUtils(net)).new Param();
		net.getDefaultNetworkTable().createColumn(p.populationCol,Integer.class,false);
		net.getDefaultNetworkTable().createColumn(p.generationCol,Integer.class,false);
		net.getDefaultNetworkTable().createColumn(p.mutationCol,Integer.class,false);
		net.getDefaultNetworkTable().createColumn(p.seedCol,Long.class,false);
		net.getDefaultNetworkTable().createColumn(p.bestScoreCol,Double.class,false);
		return p;
	}
	/**
	 * Message for anomaly in column of compact
	 */
	public void corruptedCol(String colName){
		JOptionPane.showMessageDialog(sunrise.getAdapter().getCySwingApplication().getJFrame(),
				"The Column ("+colName+") Doing the Link between Reference and Compacted is Corrupted/r/nCheck if the Current Network is a Correctly Compacted Network",
				"Warning About Column",JOptionPane.ERROR_MESSAGE);
	}
	/**
	 * Return reference network
	 */
	public CyNetwork ifRefNet(){
		String name=net.getRow(net).get(refNameCol,String.class);
		if(name==null){
			corruptedCol(refNameCol);
			return null;
		}
		CyNetwork refNet=null;
		for(CyNetwork cNet:sunrise.getAdapter().getCyNetworkManager().getNetworkSet()){
			if(name.equals(cNet.getRow(cNet).get(CyNetwork.NAME,String.class))){
				refNet=cNet;
				break;
			}
		}
		if (refNet==null){
			corruptedCol(refNameCol);
			return null;
		}
		return refNet;
	}
	/**
	 * Return fate column title
	 */
	public String ifFateCol(CyNetwork refNet){
		String fateCol=fatePrefix+net.getRow(net).get(scoreIdCol,String.class);
		if(refNet.getDefaultNodeTable().getColumn(fateCol)==null){
			corruptedCol(fateCol);
			return null;
		}
		return fateCol;
	}
	/**
	 * Select in reference network the nodes selected in compact
	 */
	public void selectInRef(HashSet<String> selectedInCompact){
		CyNetwork refNet=ifRefNet();
		if(refNet==null) return;
		String fateCol=ifFateCol(refNet);
		if(fateCol==null) return;
		sunrise.getAdapter().getCyApplicationManager().setCurrentNetwork(refNet);
		for(CyNode node:refNet.getNodeList()){
			String compactNode=refNet.getRow(node).get(fateCol,String.class);
			if(selectedInCompact.contains(compactNode)) refNet.getRow(node).set("selected",true); else refNet.getRow(node).set("selected",false);
		}
	}
	/**
	 * Select the column Name containing the score
	 */
	public boolean rightScoreCol(){
		return (sunrise.scoreCol!=null)&&(net.getDefaultNodeTable().getColumn(sunrise.scoreCol)!=null);
	}
	public boolean selectScoreCol(){
		CyTable nt=sunrise.getAdapter().getCyApplicationManager().getCurrentNetwork().getDefaultNodeTable();
		String[] colNames=new String[nt.getColumns().size()];
		int ni=0;for(CyColumn col:nt.getColumns()){
			if(col.getType()==Double.class)	colNames[ni++]=col.getName();
		}
		String selected=(String)JOptionPane.showInputDialog(sunrise.getAdapter().getCySwingApplication().getJFrame(),
				"Select the Name of The Column where is the Score to be Used\r\n"
						+ "The Present Used Name is "+sunrise.scoreCol+"\r\n The Class must be Double",
						"Score Column Input",JOptionPane.PLAIN_MESSAGE,null,colNames,colNames[0]);
		if(selected==null) return false; 
		else{
			sunrise.scoreCol=selected;
			return true;
		}
	}
}
