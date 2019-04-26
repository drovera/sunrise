// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package other;
import java.awt.event.ActionEvent;
import java.util.TreeMap;
import main.sunrise;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import utils.CyUtils;
import dialog.TwoComboBoxes;
/**
 * Transfer coordinates from a network to to another network
 * using name to match nodes
 */
public class TransferCoordinates extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	CyNetwork fromNet;
	CyNetwork toNet;
	final static String title="  Transfer Network Coordinates";
	public TransferCoordinates(){
		super(title,sunrise.getAdapter().getCyApplicationManager(),"network",sunrise.getAdapter().getCyNetworkViewManager());		
		setPreferredMenu(sunrise.name);
	}
	public class TaskFactory extends AbstractTaskFactory{
		TransferCoordinates action;
		public TaskFactory(TransferCoordinates action){this.action=action;}
		public TaskIterator createTaskIterator() {
			return(new TaskIterator(new TransferCoordinatesTask(action)));
		}		
	}
	String[] dummy={"$"};
	public void actionPerformed(ActionEvent e){
		TreeMap<String,CyNetwork> nameToNet=(new CyUtils()).getNameToNet();
		String[] netNames=nameToNet.keySet().toArray(dummy);
		StringBuffer fromName=new StringBuffer();
		StringBuffer toName=new StringBuffer();
		TwoComboBoxes dialog=new TwoComboBoxes(sunrise.getAdapter().getCySwingApplication().getJFrame(),title,"Select From Network ","Select To Network ",netNames,netNames);
		if(!dialog.launchDialog(fromName,toName)) return;
		fromNet=nameToNet.get(fromName.toString());
		toNet=nameToNet.get(toName.toString());	
		TaskManager<?,?> taskManager=sunrise.getAdapter().getTaskManager();
		TaskFactory taskFactory=new TaskFactory(this);
		taskManager.execute(taskFactory.createTaskIterator());
	}
}