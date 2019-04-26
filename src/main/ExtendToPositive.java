//Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
//Cytoscape Plugin under GNU GPL by Institut Curie
package main;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import dialog.ComboText;
import utils.CyUtils;
import utils.GeneticUtils;
/**
* Select a positive node which extend the selection
* the choice is enlighten by the new score when the positive node is connected by the highest score path
*/
public class ExtendToPositive extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	final static String title="  Extend Selection to Positive Nodes";
	CyNetwork net;
	ComboText cb;
	boolean isConnect;
	public ExtendToPositive(){
		super(title,sunrise.getAdapter().getCyApplicationManager(),"network",sunrise.getAdapter().getCyNetworkViewManager());		
		setPreferredMenu(sunrise.name);
	}
	public class TaskFactory extends AbstractTaskFactory{
		ExtendToPositive action;
		public TaskFactory(ExtendToPositive action){this.action=action;}
		public TaskIterator createTaskIterator() {
			return(new TaskIterator(new ExtendToPositiveTask(action)));
		}		
	}
	public void actionPerformed(ActionEvent e){
		net=sunrise.getAdapter().getCyApplicationManager().getCurrentNetwork();
		if(sunrise.scoreCol==null) if(!(new GeneticUtils(net)).selectScoreCol()) return;
		JFrame frame=sunrise.getAdapter().getCySwingApplication().getJFrame();
		GeneticUtils gu=new GeneticUtils(net);
		if(!gu.rightScoreCol()) if(!gu.selectScoreCol()) return;
		cb=new ComboText(frame,ExtendToPositive.title+" on "+(new CyUtils(net).getName()),"Score of Selection and of Selection Plus the Positive Node",
				"Choice a Positive Node to Include It in Selection");
		TaskManager<?,?> taskManager=sunrise.getAdapter().getTaskManager();
		TaskFactory taskFactory=new TaskFactory(this);
		taskManager.execute(taskFactory.createTaskIterator());
		cb.setVisible(true);
		if(!isConnect){
			cb.setVisible(false);
			cb.dispose();
			JOptionPane.showMessageDialog(sunrise.getAdapter().getCySwingApplication().getJFrame(),
					"The Selected Nodes are Not Connected and so The Got Nodes do Not do a SubNetwork",
					"Warning About Selected Nodes",JOptionPane.ERROR_MESSAGE);
		}
	}
}
