// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package dialog;
import java.awt.event.ActionEvent;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import main.sunrise;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNetwork;
import utils.*;
/**
 * Copy network table values from a compact network to another
 * Copy Only the parameters of genetic algorithm
 */
public class CopyCompactTable  extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	final static String title="  Copy Compact Network Table";
	public CopyCompactTable(){
		super(title,sunrise.getAdapter().getCyApplicationManager(),"network",sunrise.getAdapter().getCyNetworkViewManager());
		setPreferredMenu(sunrise.name);
	}
	public void actionPerformed(ActionEvent e) {
		JFrame frame=sunrise.getAdapter().getCySwingApplication().getJFrame();
		Set<CyNetwork> nets=sunrise.getAdapter().getCyNetworkManager().getNetworkSet();
		CyNetwork fromNet=sunrise.getAdapter().getCyApplicationManager().getCurrentNetwork();
		CyUtils fromCu=new CyUtils(fromNet);
		String[] netNames=new String[nets.size()];
		int ni=0;for(CyNetwork net:nets) netNames[ni++]=net.getRow(net).get(CyNetwork.NAME,String.class);
		String selected=(String)JOptionPane.showInputDialog(frame,
				"Select the Name of The Network where Compact Table of "+fromCu.getName()+" is copied to",
				title,JOptionPane.PLAIN_MESSAGE,null,netNames,netNames[0]);
		if(selected==null) return;
		CyNetwork toNet=null;
		for(CyNetwork cNet:nets){
			if(selected.equals(cNet.getRow(cNet).get(CyNetwork.NAME,String.class))){
				toNet=cNet;
				break;
			}
		}
		GeneticUtils fromGu=new GeneticUtils(fromNet);
		GeneticUtils toGu=new GeneticUtils(toNet);
		try{
			String fromRef=fromCu.getString(fromGu.refNameCol);
			String fromScoreId=fromCu.getString(fromGu.scoreIdCol);
			GeneticUtils.Param fromP=fromGu.new Param();
			fromP.read();
			int option=JOptionPane.showConfirmDialog(frame,"Ensure that all titles of column are created in network\r\nThe simplest method is by File > New",
					title+" to "+(new CyUtils(toNet)).getName(),JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
			if(option==JOptionPane.OK_OPTION){
				toNet.getRow(toNet).set(toGu.refNameCol,fromRef);
				toNet.getRow(toNet).set(toGu.scoreIdCol,fromScoreId);
				GeneticUtils.Param toP=toGu.new Param();
				toP.set(fromP);
				toP.bestScore=0.0;
				toP.write();
			}			
		}catch(Exception ex){
			toGu.corruptedCol("");
		}		
	}
}
