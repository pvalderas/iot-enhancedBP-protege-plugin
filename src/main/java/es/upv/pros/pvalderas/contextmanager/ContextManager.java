package es.upv.pros.pvalderas.contextmanager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.protege.editor.owl.rdf.SparqlQueryView;


public class ContextManager extends JDialog {

	private static JSONArray events;
	public static void setEvents(JSONArray e){events=e;};
	
	private static String compo;
	public static void setCompo(String c){compo=c;};
	
	private JComboBox<Event> eventList;
	private String rule;
	
	public ContextManager(String rule, Event event) {
		this.rule=rule;
		
		setTitle("High-level events");
		setAlwaysOnTop(true);
		setResizable(false);
		setModal(true);
		getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Map SPARQL RULE to BPMN High-level Event");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(6, 16, 688, 20);
		lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		getContentPane().add(lblNewLabel);
		
		JTextPane textPane = new JTextPane();
		textPane.setBounds(27, 58, 644, 275);
		textPane.setForeground(Color.GRAY);
		textPane.setText(rule);
		textPane.setEditable(false);
		getContentPane().add(textPane);
		
		JLabel lblContextManagerUrl = new JLabel("High Level Event:");
		lblContextManagerUrl.setBounds(27, 365, 117, 16);
		getContentPane().add(lblContextManagerUrl);
		
		
		eventList = new JComboBox<Event>();
		eventList.setBounds(146, 356, 525, 37);
		

		for(int i=0;i<events.length();i++){
			eventList.addItem(new Event(events.getJSONObject(i).getString("name")));
		}

		eventList.setRenderer(new MyComboBoxRenderer("Select one..."));
		eventList.setSelectedItem(event);
		getContentPane().add(eventList);
		
		JButton btncancel = new JButton("Cancel");
		btncancel.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		btncancel.setIcon(new ImageIcon(ContextManager.class.getResource("/cancel.png")));
		btncancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ContextManager.this.dispose();
			}
		});
		btncancel.setBounds(156, 405, 117, 47);
		getContentPane().add(btncancel);
		
		JButton btnAccept = new JButton("Send to CEP Controller");
		btnAccept.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		btnAccept.setIcon(new ImageIcon(ContextManager.class.getResource("/send.png")));
		btnAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(eventList.getSelectedIndex()==-1){
					JOptionPane.showMessageDialog(ContextManager.this, "You must select a High Level Event");
				}else{
					JSONObject data=new JSONObject();
					data.put("rule", ContextManager.this.rule.replaceAll("'", "\\\\'"));
					data.put("event", eventList.getSelectedItem().toString());
					data.put("composition", compo);
					
					try {
						String result=HTTPClient.post("http://pedvalar.webs.upv.es/microservices/cepmanager", data.toString(), true, "text/plain");
						if(result.equals("OK")){
							JOptionPane.showMessageDialog(ContextManager.this, "CEP Rule deployed into the CEP Manager successfully");
						}else{
							JOptionPane.showMessageDialog(ContextManager.this, result);
						}
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(ContextManager.this, e1.getMessage());
						e1.printStackTrace();
					}
					ContextManager.this.dispose();
				}
			}
		});
		btnAccept.setBounds(410, 405, 200, 47);
		getContentPane().add(btnAccept);


		
		this.setSize(700,480);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
}

class MyComboBoxRenderer extends JLabel implements ListCellRenderer
{
    private String _title;

    public MyComboBoxRenderer(String title)
    {
        _title = title;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean hasFocus)
    {
        if (index == -1 && value == null) {
        	this.setForeground(Color.LIGHT_GRAY);
        	setText(_title);
        }
        else{
        	this.setForeground(Color.BLACK);
        	setText(value.toString());
        }
        return this;
    }
}
