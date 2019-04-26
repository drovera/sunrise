// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package utils;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import javax.swing.JOptionPane;
import main.sunrise;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
/**
 * convenient functions to interface Cytoscape
 * get simply values in tables, the set function are in classes
 * They are not used to classes which are independent from genetic algorithm
 * Return network with name
 * To avoid same name for network, same name disturbs network manager
 * Return first node with name, return a CyNode
 * Select node by name list
 */
public class CyUtils{
	public CyNetwork net;
	boolean ifEdge;
	final String taskExceptMess="anomaly! Cannot Do";
	public CyUtils(){this.net=null;}
	public CyUtils(CyNetwork net){ this.net=net;}
	public CyUtils(CyNetwork net, boolean ifEdge){this.net=net;this.ifEdge=ifEdge;}
	public String getName(){return net.getRow(net).get(CyNetwork.NAME,String.class);}
	public String getString(String colName){return net.getRow(net).get(colName,String.class);}
	public Integer getInteger(String colName){return net.getRow(net).get(colName,Integer.class);}
	public Long getLong(String colName){return net.getRow(net).get(colName,Long.class);}
	public String getName(CyNode node){return net.getRow(node).get(CyNetwork.NAME, String.class);}
	public void select(CyNode node,boolean yes){net.getRow(node).set("selected",yes);}
	public String getString(String colName, CyNode node){return net.getRow(node).get(colName, String.class);}
	public Double getDouble(String colName, CyNode node){return net.getRow(node).get(colName, Double.class);}
	public String getName(CyEdge edge){return net.getRow(edge).get(CyNetwork.NAME, String.class);}
	public TreeMap<String,CyNetwork> getNameToNet(){
		CyNetworkManager networkManager=sunrise.getAdapter().getCyNetworkManager();
		TreeMap<String,CyNetwork> nameToNet=new TreeMap<String,CyNetwork>();
		for(CyNetwork net:networkManager.getNetworkSet()) nameToNet.put(net.getRow(net).get(CyNetwork.NAME,String.class),net);
		return nameToNet;
	}
	public HashSet<String> netNameList(){
		HashSet<String> netNames=new HashSet<String>();
		for(CyNetwork net:sunrise.getAdapter().getCyNetworkManager().getNetworkSet()) netNames.add(net.getRow(net).get(CyNetwork.NAME,String.class));
		return netNames;
	}
	public boolean noSynonymInNets(String name){
		HashSet<String> netNames=netNameList();
		if (netNames.contains(name)){
			JOptionPane.showMessageDialog(sunrise.getAdapter().getCySwingApplication().getJFrame(),
					"The Created Network Name is already used, Rename or Delete "+name,"Warning About Network Name",JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}
	public CyNode getCyNode(String name){
		Iterator<CyNode> i = net.getNodeList().iterator();
		while(i.hasNext()){
			CyNode node=i.next();
			if(net.getRow(node).get(CyNetwork.NAME, String.class).equals(name)) return node;
		}		
		return null;
	}
	public void selectList(Collection<String> list){
		if(ifEdge){
			Iterator<CyEdge> i = net.getEdgeList().iterator();
			while(i.hasNext()){
				CyEdge edge=i.next();
				if(list.contains(getName(edge))) net.getRow(edge).set("selected",true); else net.getRow(edge).set("selected",false);
			}	
		}else{
			Iterator<CyNode> i = net.getNodeList().iterator();
			while(i.hasNext()){
				CyNode node=i.next();
				if(list.contains(getName(node))) net.getRow(node).set("selected",true); else net.getRow(node).set("selected",false);
			}
		}
	}
}
