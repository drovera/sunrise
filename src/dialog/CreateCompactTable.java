// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package dialog;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import main.sunrise;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNetwork;
import utils.*;
import utils.GeneticUtils.Param;
/**
 * Create a void compact table
 */
public class CreateCompactTable  extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	final static String title="  Create Compact Network Table";
	public CreateCompactTable(){
		super(title,sunrise.getAdapter().getCyApplicationManager(),"network",sunrise.getAdapter().getCyNetworkViewManager());
		setPreferredMenu(sunrise.name);
	}
	public void actionPerformed(ActionEvent e) {
		JFrame frame=sunrise.getAdapter().getCySwingApplication().getJFrame();
		CyNetwork net=sunrise.getAdapter().getCyApplicationManager().getCurrentNetwork();
		GeneticUtils gu=new GeneticUtils(net);
		try{
			int option=JOptionPane.showConfirmDialog(frame,"Ensure there is No Column Except about the Current Network",
					title+" in "+(new CyUtils(net)).getName(),JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
			if(option==JOptionPane.OK_OPTION){
				gu.createRefScrIdCol("To Fill Out!", "To Fill Out!");
				Param p=gu.createParamCol();
				p.setDefault();
			}
		}catch(Exception ex){
			gu.corruptedCol("");
		}
	}
}
