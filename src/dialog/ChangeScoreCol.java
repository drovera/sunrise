// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package dialog;
import java.awt.event.ActionEvent;
import main.sunrise;
import org.cytoscape.application.swing.AbstractCyAction;
import utils.GeneticUtils;
/**
 * Input/Change Name of Score Column
 */
public class ChangeScoreCol extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	final static String title="  Input/Change Name of Score Column";
	public ChangeScoreCol(){
		super(title,sunrise.getAdapter().getCyApplicationManager(),"network",sunrise.getAdapter().getCyNetworkViewManager());
		setPreferredMenu(sunrise.name);
	}
	public void actionPerformed(ActionEvent e) {
		GeneticUtils gu=new GeneticUtils();
		gu.selectScoreCol();
	}
}