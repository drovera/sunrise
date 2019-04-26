// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package other;
import java.awt.event.ActionEvent;
import main.sunrise;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
/**
 * Match nodes and number connected components by column in table
 */
public class ConnectedNodes  extends AbstractCyAction {
	private static final long serialVersionUID = 1L;
	final static String title="  Connected Nodes in Table";
	public ConnectedNodes(){
		super(title,sunrise.getAdapter().getCyApplicationManager(),"network",sunrise.getAdapter().getCyNetworkViewManager());		
		setPreferredMenu(sunrise.name);
	}
	public class TaskFactory extends AbstractTaskFactory{
		ConnectedNodes action;
		public TaskFactory(ConnectedNodes action){this.action=action;}
		public TaskIterator createTaskIterator() {
			return(new TaskIterator(new ConnectedNodeTask(action)));
		}		
	}
	public void actionPerformed(ActionEvent e){
		TaskManager<?,?> taskManager=sunrise.getAdapter().getTaskManager();
		TaskFactory taskFactory=new TaskFactory(this);
		taskManager.execute(taskFactory.createTaskIterator());
	}
}