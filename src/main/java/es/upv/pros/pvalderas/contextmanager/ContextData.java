package es.upv.pros.pvalderas.contextmanager;

import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.json.JSONArray;
import org.json.JSONObject;
import java.awt.Color;

public class ContextData extends JDialog {
	
	private static String bp;
	public static void setBP(String b){bp=b;};
	
	private static String contextJSON;
	public static void setContext(String context){contextJSON=context;};
	public static boolean isContext(){return contextJSON!=null;};
	
	private static String system;
	public static void setSystem(String sys){system=sys;};
	
	private Hashtable<String, JSONArray> sensorHash=new Hashtable<String, JSONArray>();
	private Hashtable<String, JSONObject> obsHash=new Hashtable<String, JSONObject>();
	
	
	private JList deviceList;
	private JList observationList;
	private JTextArea obsDesc;
	private JLabel resultLbl;
	private JList dataList ;
	private JLabel propertyLbl;
	private JLabel featureLbl;
	private JTextPane queryPane;
	private JButton executeQuery;
	
	public ContextData(JTextPane queryPane, JButton executeQuery ){
		this.queryPane=queryPane;
		this.executeQuery=executeQuery;
		
		getContentPane().setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		setTitle("Low-level contaxt data");
		setAlwaysOnTop(true);
		setResizable(false);
		setModal(true);
		getContentPane().setLayout(null);
		
		
		//IoT Devices
		JLabel lblIotDevices = new JLabel("IoT devices");
		lblIotDevices.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		lblIotDevices.setHorizontalAlignment(SwingConstants.CENTER);
		lblIotDevices.setBounds(19, 37, 223, 32);
		getContentPane().add(lblIotDevices);
		
		deviceList = new JList();
		deviceList.setBounds(19, 68, 223, 365);
		deviceList.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		DefaultListModel model = new DefaultListModel();
		JSONArray sensors=new JSONArray(this.contextJSON);
		for(int i=0;i<sensors.length();i++){
			String sensorName=sensors.getJSONObject(i).getString("name");
			model.addElement(sensorName);
			sensorHash.put(sensorName, sensors.getJSONObject(i).getJSONArray("observations"));
		}
		deviceList.setModel(model);
		deviceList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		getContentPane().add(deviceList);
		
		
		//Obervations
		JLabel lblNewLabel = new JLabel("Obervations");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(254, 37, 279, 32);
		getContentPane().add(lblNewLabel);
		
		observationList = new JList();
	    observationList.setModel(new DefaultListModel());
	    observationList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
	    observationList.setBounds(254, 71, 279, 199);
	    observationList.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		getContentPane().add(observationList);
		
		obsDesc = new JTextArea();
		obsDesc.setWrapStyleWord(true);
		obsDesc.setLineWrap(true);
		obsDesc.setBackground(SystemColor.window);
		obsDesc.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		obsDesc.setEditable(false);
		obsDesc.setBounds(254, 349, 557, 84);
		getContentPane().add(obsDesc);
		
		
		
		//Property
		propertyLbl = new JLabel("Observed Property:");
		propertyLbl.setHorizontalAlignment(SwingConstants.LEFT);
		propertyLbl.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		propertyLbl.setBounds(254, 282, 557, 32);
		getContentPane().add(propertyLbl);
		
		
		//Data
		resultLbl = new JLabel("Result:");
		resultLbl.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		resultLbl.setHorizontalAlignment(SwingConstants.LEFT);
		resultLbl.setBounds(545, 41, 266, 25);
		getContentPane().add(resultLbl);
		
		
		dataList = new JList();
	    dataList.setModel(new DefaultListModel());
	    dataList.setBounds(545, 71, 266, 199);
	    dataList.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
	    dataList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		getContentPane().add(dataList);
		
		
		
		//Buttons
		JButton btnAddPropertyTo = new JButton("Add Data Property to Query");
		btnAddPropertyTo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String obs=observationList.getSelectedValue().toString().replaceAll(" ","").toLowerCase();
				String sensor=deviceList.getSelectedValue().toString().replaceAll(" ","").toLowerCase();
				String resultName=resultLbl.getText().substring(resultLbl.getText().indexOf(":")+2).replaceAll(" ","").toLowerCase();
				String observedProperty=propertyLbl.getText().substring(propertyLbl.getText().indexOf(":")+2).replaceAll(" ","").toLowerCase();
				String dataProp=dataList.getSelectedValue().toString().substring(0, dataList.getSelectedValue().toString().indexOf(":")).replaceAll(" ","").toLowerCase();
				
				String paneText=ContextData.this.queryPane.getText();
		
				String dataPropText="	?result"+observedProperty+" :"+dataProp+" [VALUE] .\n";
				
				String obsText="	?obs"+observedProperty+" a sosa:Observation .\n";
				obsText+="	?obs"+observedProperty+" sosa:madeBySensor :"+sensor+" .\n";
				obsText+="	?obs"+observedProperty+" sosa:observedProperty :"+observedProperty+" .\n";
				obsText+="	?obs"+observedProperty+" sosa:hasResult ?result"+observedProperty+" .\n";
				//obsText+="	?result"+observedProperty+" :name '"+resultName+"' .\n";
				obsText+=dataPropText;
				
				String query="";
				if(paneText.length()==0){		
					query+="PREFIX : <http://pros.upv.es/pvalderas/IoTEnhancedBP/"+system+"#>\n"; //"http://pros.upv.es/pvalderas/IoTEnhancedBP/"+
					query+="PREFIX sosa: <http://www.w3.org/ns/sosa/>\n";
					query+="ASK {\n";
					query+=obsText;
					query+="}";
				}else if(paneText.indexOf("?obs"+observedProperty+" sosa:observedProperty :"+observedProperty)>0){
					query=paneText.replace("}", dataPropText+"}");
				}else{
					query=paneText.replace("}", obsText+"}");
				}
				
				/*
				 	?obs a sosa:Observation .
	?obs sosa:observedProperty ?prop .
	?prop :name "Temperature" .
	?obs sosa:madeBySensor ?sensor .
	?sensor :name "ContainerTemperatureSensor" .
	?obs sosa:featureOfInteres ?feature .
	?feature :type "Container" .
	?obs sosa:hasResult ?result .
	?result :name "Degrees" .
	?result sosa:value 15.0 .
				 */
					
				ContextData.this.queryPane.setText(query);
				ContextData.this.executeQuery.setEnabled(true);
				ContextData.this.dispose();

			}
		});
		btnAddPropertyTo.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		btnAddPropertyTo.setIcon(new ImageIcon(ContextManager.class.getResource("/add.png")));
		btnAddPropertyTo.setBounds(551, 457, 260, 47);
		getContentPane().add(btnAddPropertyTo);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		btnCancel.setIcon(new ImageIcon(ContextManager.class.getResource("/cancel.png")));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ContextData.this.dispose();
			}
		});
		btnCancel.setBounds(68, 457, 140, 47);
		getContentPane().add(btnCancel);
		
		JLabel systemLabel = new JLabel(bp);
		systemLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		systemLabel.setForeground(SystemColor.controlHighlight);
		systemLabel.setHorizontalAlignment(SwingConstants.CENTER);
		systemLabel.setBounds(6, 6, 819, 32);
		getContentPane().add(systemLabel);
		
		featureLbl = new JLabel("Feature of Interest:");
		featureLbl.setHorizontalAlignment(SwingConstants.LEFT);
		featureLbl.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		featureLbl.setBounds(254, 312, 557, 32);
		getContentPane().add(featureLbl);
		
		
		
		deviceList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				String device=(String)deviceList.getSelectedValue();
				JSONArray observations=sensorHash.get(device);
				observationList.clearSelection();
				DefaultListModel model=(DefaultListModel)observationList.getModel();
				model.clear();
				obsHash.clear();
				for(int i=0;i<observations.length();i++){
					String obsName=observations.getJSONObject(i).getString("name");
					model.addElement(obsName);
					obsHash.put(obsName, observations.getJSONObject(i));
				}
				
				obsDesc.setText("");
				propertyLbl.setText("Observed Property:");
				featureLbl.setText("Feature of Interest:");
				resultLbl.setText("Result:");
				((DefaultListModel)dataList.getModel()).clear();
			}
		});
		
		
		observationList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				String obs=(String)observationList.getSelectedValue();
				if(obs!=null){
					JSONObject observation=obsHash.get(obs);
					obsDesc.setText(observation.getString("description"));
					featureLbl.setText("Feature of Interest: "+observation.getString("feature"));
					propertyLbl.setText("Observed Property: "+observation.getString("property"));
					JSONObject result=observation.getJSONObject("result");
					resultLbl.setText("Result: "+result.getString("name"));
					
					
					DefaultListModel model=(DefaultListModel)dataList.getModel();
					model.clear();
					for(int i=0;i<result.getJSONArray("data").length();i++){
						JSONObject prop=result.getJSONArray("data").getJSONObject(i);
						model.addElement(prop.getString("name")+":"+prop.getString("type"));			
					}
				}
			}
		});
		
		this.setSize(831,545);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
}
