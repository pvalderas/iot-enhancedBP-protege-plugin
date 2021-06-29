package es.upv.pros.pvalderas.contextmanager;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.editorkit.EditorKitFactoryPlugin;

public class BPSelection extends JDialog {
	private Hashtable<String, Composition> compoHash=new Hashtable<String, Composition>();
	private JButton next;
	private static String system;
	private static String compo;
	
	private JEditorPane eventDescription;
	
	public BPSelection(String compositionsJSON, JButton next, JComboBox<Event> eventList) {
		this.next=next;
		JSONArray compositions=new JSONArray(compositionsJSON);
		
		setTitle("Selection of IoT-Enhanced BP");
		setAlwaysOnTop(true);
		setResizable(false);
		setModal(true);
		getContentPane().setLayout(null);
	
		//Lo ponemos antes del combo para que existe el textArea y poder mostrar los eventos de la compo seleccionada
		JLabel lblNewLabel_1 = new JLabel("High-level events to be defined:");
		lblNewLabel_1.setBounds(36, 90, 344, 29);
		lblNewLabel_1.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		getContentPane().add(lblNewLabel_1);

		eventDescription = new JEditorPane();
		eventDescription.setContentType("text/html");
		eventDescription.setBounds(36, 123, 512, 243);
		eventDescription.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		getContentPane().add(eventDescription);
		
		JLabel lblNewLabel = new JLabel("Select an IoT-Enhanced BP");
		lblNewLabel.setBounds(36, 21, 401, 29);
		lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		getContentPane().add(lblNewLabel);
		
		JComboBox bpList = new JComboBox();
		bpList.setBounds(32, 49, 516, 29);
		bpList.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		bpList.addItem(new Composition());
		
		
		for(int i=0;i<compositions.length();i++){
			Composition compo=new Composition();
			compo.setId(compositions.getJSONObject(i).getString("id"));
			compo.setUser(compositions.getJSONObject(i).getString("user"));
			compo.setSystem(compositions.getJSONObject(i).getString("system"));
			compo.setEvents(compositions.getJSONObject(i).getJSONArray("events"));
			bpList.addItem(compo);
			if(compo.getId().equals(BPSelection.compo)){
				bpList.setSelectedItem(compo);
				showEvents(compo);
			}
			compoHash.put(compositions.getJSONObject(i).getString("id"), compo);
		}
		getContentPane().add(bpList);

		
		
		bpList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Composition compo=(Composition)bpList.getSelectedItem();
				BPSelection.this.showEvents(compo);
			}
		});

		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		btnCancel.setIcon(new ImageIcon(BPSelection.class.getResource("/cancel.png")));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BPSelection.this.dispose();
			}
		});
		btnCancel.setBounds(320, 379, 140, 47);
		getContentPane().add(btnCancel);
		
		
		JButton btnSelect = new JButton("Select");
		btnSelect.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		btnSelect.setIcon(new ImageIcon(BPSelection.class.getResource("/select.png")));
		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(bpList.getSelectedIndex()>0){
					Composition compo=(Composition)bpList.getSelectedItem();
					
					
					ContextData.setBP(compo.getId());
					ContextManager.setEvents(compo.getEvents());
					ContextManager.setCompo(compo.getId());
					
					for(int i=0;i<compo.getEvents().length();i++){
						Event ev=new Event();
						ev.setName(compo.getEvents().getJSONObject(i).getString("name"));
						ev.setDescription(compo.getEvents().getJSONObject(i).getString("description"));
						eventList.addItem(ev);
					}
					
					BPSelection.system=compo.getSystem();
					BPSelection.compo=compo.getId();
	
					BPSelection.this.next.setEnabled(true);
					BPSelection.this.dispose();
					loadContext();	
					
					URI uri;
					try {
						/*for(int i=0;i<ProtegeManager.getInstance().getEditorKitFactoryPlugins().size();i++){
							EditorKitFactoryPlugin ed=ProtegeManager.getInstance().getEditorKitFactoryPlugins().get(i);
							JOptionPane.showMessageDialog(BPSelection.this, ed.getId()+" "+ed.getLabel());
						}*/
						
						//WorkspaceFrame.this.workspace.getEditorKit()
						//getOWLEditorKit().handleLoadFrom(uri);
						
						uri = new URI("https://pedvalar.webs.upv.es/microservices/ontology/"+compo.getSystem());
						//ProtegeManager.getInstance().loadAndSetupEditorKitFromURI(ProtegeManager.getInstance().getEditorKitFactoryPlugins().get(0), uri);
						ProtegeManager.getInstance().getEditorKitManager().getEditorKits().get(0).handleLoadFrom(uri);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}else{
					JOptionPane.showMessageDialog(BPSelection.this, "You must select an IoT-Enhanced BP");
				}
			}
		});
		btnSelect.setBounds(153, 379, 140, 47);
		getContentPane().add(btnSelect);
		
		
		this.setSize(584,458);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	private void showEvents(Composition compo){
		String desc="";
		if(compo.getId()!=null){
			for(int i=0;i<compo.getEvents().length();i++){
				desc+="<div style='padding:15px 0px 0px 15px;'><b><span style='font-face:Lucida Grande;font-size: 18'>"+compo.getEvents().getJSONObject(i).getString("name")+"</span></b><br><span style='font-face:Lucida Grande;font-size: 20'>"+compo.getEvents().getJSONObject(i).getString("description")+"</span></div>";
			} 
		}
		eventDescription.setText(desc);
	}
	
	private void loadContext(){
		try {
			String contextJSON=HTTPClient.get("https://pedvalar.webs.upv.es/microservices/"+BPSelection.system+"/context");
			ContextData.setContext(contextJSON);
			ContextData.setSystem(BPSelection.system);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
