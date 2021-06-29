package org.protege.editor.owl.rdf;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

import org.protege.editor.core.ui.error.ErrorLogPanel;
import org.protege.editor.owl.rdf.repository.BasicSparqlReasonerFactory;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.protege.editor.owl.ui.table.BasicOWLTable;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

import es.upv.pros.pvalderas.contextmanager.BPSelection;
import es.upv.pros.pvalderas.contextmanager.ContextData;
import es.upv.pros.pvalderas.contextmanager.ContextManager;
import es.upv.pros.pvalderas.contextmanager.Event;
import es.upv.pros.pvalderas.contextmanager.HTTPClient;

public class SparqlQueryView extends AbstractOWLViewComponent {

	private SparqlReasoner reasoner;

	private final JTextPane queryPane = new JTextPane();

	private final SwingResultModel resultModel = new SwingResultModel();
	
	private String compositionJSON;
	
	private JButton contextManager, contextData, executeQuery, selectBP;
	
	private JComboBox<Event> eventList;
	private JTextArea eventDesc;

	@Override
	protected void initialiseOWLView() {
		initializeReasoner();
		setLayout(new BorderLayout());
		add(createCenterComponent(), BorderLayout.CENTER);
		add(createBottomComponent(), BorderLayout.SOUTH);
		
		
		new Thread(new Runnable() {
		    public void run() {
		    	SparqlQueryView.this.loadCompositions();
		    }
		}).start();
		
		
	}
	
	private void loadCompositions(){
		try {
			compositionJSON=HTTPClient.get("https://pedvalar.webs.upv.es/microservices/compositions/events");
			selectBP.setEnabled(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void initializeReasoner() {
		try {
			List<SparqlInferenceFactory> plugins = Collections.singletonList((SparqlInferenceFactory) new BasicSparqlReasonerFactory());
			reasoner = plugins.iterator().next().createReasoner(getOWLModelManager().getOWLOntologyManager());
			reasoner.precalculate();
		}
		catch (SparqlReasonerException e) {
			ErrorLogPanel.showErrorDialog(e);
		}
	}
	
	private JComponent createCenterComponent() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,1));
	
		//queryPane.setText(reasoner.getSampleQuery());
		panel.add(new JScrollPane(queryPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
		BasicOWLTable results = new BasicOWLTable(resultModel) {
			@Override
			protected boolean isHeaderVisible() {
				return true;
			}
		};
		OWLCellRenderer renderer = new OWLCellRenderer(getOWLEditorKit());
		renderer.setWrap(false);
		results.setDefaultRenderer(Object.class, renderer);
		JScrollPane scrollableResults = new JScrollPane(results);
		panel.add(scrollableResults);
		return panel;
	}
	
	private JComponent createBottomComponent() {
		JPanel panelButtons = new JPanel();
		panelButtons.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		JPanel panelEvents = new JPanel();
		panelEvents.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		JPanel container = new JPanel(new BorderLayout());
		
		
		eventDesc = new JTextArea();
		eventDesc.setWrapStyleWord(true);
		eventDesc.setLineWrap(true);
		eventDesc.setBackground(SystemColor.window);
		eventDesc.setSize(350, 100);
		
		eventList = new JComboBox();
		eventList.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		eventList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Event ev=(Event)eventList.getSelectedItem();
				eventDesc.setText(ev.getDescription());
			}
		});
		
		
		
		selectBP = new JButton("Select IoT-Enhanced BP");
		selectBP.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		selectBP.setIcon(new ImageIcon(ContextData.class.getResource("/context.png")));
		selectBP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					new BPSelection(compositionJSON,SparqlQueryView.this.contextData, SparqlQueryView.this.eventList);
			}
		});
		selectBP.setEnabled(false);
		
		
		
		contextData = new JButton("Published Context Data");
		contextData.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		contextData.setIcon(new ImageIcon(ContextData.class.getResource("/context.png")));
		contextData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					new ContextData(queryPane, executeQuery);
			}
		});
		if(!ContextData.isContext()) contextData.setEnabled(false);
		
		
		
		executeQuery = new JButton("Test Rule");
		executeQuery.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		executeQuery.setIcon(new ImageIcon(ContextManager.class.getResource("/play.png")));
		executeQuery.addActionListener(e -> {
			try {
				String query = queryPane.getText();
				SparqlResultSet result = reasoner.executeQuery(query);
				resultModel.setResults(result);
				contextManager.setEnabled(true);
			}
			catch (SparqlReasonerException ex) {
				ErrorLogPanel.showErrorDialog(ex);
				JOptionPane.showMessageDialog(getOWLWorkspace(), ex.getMessage() + "\nSee the logs for more information.");
			}
		});
		executeQuery.setEnabled(false);
		
		
		contextManager = new JButton("Map Rule with Event");
		contextManager.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		contextManager.setIcon(new ImageIcon(ContextManager.class.getResource("/send.png")));
		contextManager.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					try{
						new ContextManager(queryPane.getText(), (Event)eventList.getSelectedItem());
					}catch(Exception ex){
						ex.printStackTrace();
					}
			}
		});
		contextManager.setEnabled(false);
		
		panelEvents.add(new JLabel("Event:"));
		panelEvents.add(eventList);
		panelEvents.add(eventDesc);
		
		panelButtons.add(selectBP);
		panelButtons.add(contextData);
		panelButtons.add(executeQuery);
		panelButtons.add(contextManager);
		
		container.add(panelEvents, BorderLayout.LINE_START);
		container.add(panelButtons, BorderLayout.LINE_END);

		return container;
	}

	@Override
	protected void disposeOWLView() {
		if (reasoner != null) {
			reasoner.dispose();
			reasoner = null;
		}
	}
	
	

}
