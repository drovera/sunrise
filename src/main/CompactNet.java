// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package main;
import java.awt.event.ActionEvent;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import utils.CyUtils;
import utils.GeneticUtils;
/**
 * create the compact network according to the algorithm
 */
public class CompactNet extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	final static String title="  Compact Network";
	public CyNetwork refNet;
	public String cName;
	CompactNet(){
		super(title,sunrise.getAdapter().getCyApplicationManager(),"network",sunrise.getAdapter().getCyNetworkViewManager());
		setPreferredMenu(sunrise.name);
	}
	public class TaskFactory extends AbstractTaskFactory{
		CompactNet action;
		public TaskFactory(CompactNet action){this.action=action;}
		public TaskIterator createTaskIterator() {
			return(new TaskIterator(new CompactNetTask(action)));
		}		
	}
	public void actionPerformed(ActionEvent e) {
		refNet=sunrise.getAdapter().getCyApplicationManager().getCurrentNetwork();
		CyUtils cu=new CyUtils(refNet);
		GeneticUtils gu=new GeneticUtils(refNet);
		if(!gu.rightScoreCol()) if(!gu.selectScoreCol()) return;
		cName=cu.getName()+"_C_"+sunrise.scoreCol;
		if(!cu.noSynonymInNets(cName)) return;
		TaskManager<?,?> taskManager=sunrise.getAdapter().getTaskManager();
		TaskFactory taskFactory=new TaskFactory(this);
		taskManager.execute(taskFactory.createTaskIterator());
	}
}