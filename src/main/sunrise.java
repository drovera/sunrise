// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package main;
import org.cytoscape.app.swing.AbstractCySwingApp;
import org.cytoscape.app.swing.CySwingAppAdapter;
import dialog.*;
import other.*;
import utils.CySection;
/*
 * Main Application
 * menu: actions and sections
 * scoreCol name of column which contains the score
 */
public class sunrise extends AbstractCySwingApp{
	public static String name="sunrise";
	private static CySwingAppAdapter adapter;
	public static CySwingAppAdapter getAdapter(){return adapter;}
	public static String scoreCol=null;
	public sunrise(CySwingAppAdapter adapter){
		super(adapter);
		sunrise.adapter=adapter;
		
		adapter.getCySwingApplication().addAction(new CySection(0));
		adapter.getCySwingApplication().addAction(new ChangeScoreCol());
		adapter.getCySwingApplication().addAction(new CreateCompactTable());
		adapter.getCySwingApplication().addAction(new CopyCompactTable());
		
		adapter.getCySwingApplication().addAction(new CySection(1));
		adapter.getCySwingApplication().addAction(new CompactNet());
		adapter.getCySwingApplication().addAction(new GeneticSearch());
		adapter.getCySwingApplication().addAction(new IterateToPositive());
		adapter.getCySwingApplication().addAction(new ExtendToPositive());
		
		adapter.getCySwingApplication().addAction(new CySection(2));
		adapter.getCySwingApplication().addAction(new SelectInRef());
		adapter.getCySwingApplication().addAction(new CompactContent());
		
		adapter.getCySwingApplication().addAction(new CySection(3));
		adapter.getCySwingApplication().addAction(new SelectedNodeScore());
		adapter.getCySwingApplication().addAction(new ListSelectedNodes());
		adapter.getCySwingApplication().addAction(new SelectNodesByName());
		adapter.getCySwingApplication().addAction(new SelectEdgesByName());
		
		adapter.getCySwingApplication().addAction(new CySection(4));
		adapter.getCySwingApplication().addAction(new ConnectedNodes());
		adapter.getCySwingApplication().addAction(new InterNetNodes());
		adapter.getCySwingApplication().addAction(new ListNodes());
		adapter.getCySwingApplication().addAction(new TransferCoordinates());
	}
}