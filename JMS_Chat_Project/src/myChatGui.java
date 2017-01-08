import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;


public class myChatGui extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	TopicSubscriber subscriber;
	TopicPublisher publisher;
	TopicSession session;

	String userName = "";
	String user;

	private JTextArea recievedMessages;
	private JTextField outGoingMsg;
	private JButton sendBtn;
	private JButton clearBtn;
	private JButton bkUpBtn;
	private JButton closeBtn;
	private JLabel timeLbl;
	private JLabel dateLbl;
	private JLabel imageLbl;
	public static JTextArea usersOnlineTxtArea;
	private JLabel jcomp10;

	Border border = BorderFactory.createLineBorder(Color.BLACK);

	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	public myChatGui() throws Exception {

		Hashtable<String, String> properties = new Hashtable<String, String>();
		properties.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.exolab.jms.jndi.InitialContextFactory");
		properties.put(Context.PROVIDER_URL, "rmi://localhost:1099/");
		Context ctx;
		ctx = new InitialContext(properties);
		TopicConnectionFactory factory = (TopicConnectionFactory) ctx
				.lookup("JmsTopicConnectionFactory");
		TopicConnection conn = factory.createTopicConnection();
		session = conn.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		final javax.jms.Topic topic = (javax.jms.Topic) ctx.lookup("topic1");

		// create publisher and subscriber
		publisher = session.createPublisher(topic);
		subscriber = session.createSubscriber(topic);

		// construct GUI components
		recievedMessages = new JTextArea();
		outGoingMsg = new JTextField(5);
		sendBtn = new JButton("Send");
		clearBtn = new JButton("Clear  Chat");
		bkUpBtn = new JButton("Backup Chat");
		closeBtn = new JButton("Close");
		timeLbl = new JLabel("");
		dateLbl = new JLabel("");
		usersOnlineTxtArea = new JTextArea(5, 5);
		jcomp10 = new JLabel("Users Online");

		ImageIcon image = new ImageIcon("src\\java1.png");
		imageLbl = new JLabel("", image, JLabel.CENTER);

		recievedMessages.setFont(new Font("Arial", Font.PLAIN, 14));
		usersOnlineTxtArea.setFont(new Font("Arial", Font.PLAIN, 14));

		recievedMessages.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		outGoingMsg.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		usersOnlineTxtArea.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(1, 1, 1, 1)));

		// adjust size and set layout
		setSize(681, 365);
		setLayout(null);

		// add components
		add(recievedMessages);
		add(outGoingMsg);
		add(sendBtn);
		add(clearBtn);
		add(bkUpBtn);
		add(closeBtn);
		add(timeLbl);
		add(dateLbl);
		// add(imageLbl);
		add(usersOnlineTxtArea);
		add(jcomp10);

		setMessageListener();
		conn.start();

		// set component bounds (only needed by Absolute Positioning)
		recievedMessages.setBounds(10, 10, 450, 280);
		outGoingMsg.setBounds(9, 291, 390, 25);
		sendBtn.setBounds(400, 291, 69, 25);
		clearBtn.setBounds(515, 225, 115, 20);
		bkUpBtn.setBounds(515, 250, 115, 20);
		closeBtn.setBounds(520, 280, 100, 25);
		imageLbl.setBounds(505, 45, 130, 155);
		usersOnlineTxtArea.setBounds(510, 35, 125, 185);
		jcomp10.setBounds(530, 10, 100, 25);

		usersOnlineTxtArea.setEditable(false);
		recievedMessages.setEditable(false);

		// Add ScrollPane to TextArea
		JScrollPane scroll = new JScrollPane(recievedMessages);
		scroll.setBounds(10, 10, 460, 280);
		add(scroll);

		// Set textarea to scroll when new text is received
		DefaultCaret caret = (DefaultCaret) recievedMessages.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		// add actionisteners to button
		sendBtn.addActionListener(this);
		closeBtn.addActionListener(this);
		clearBtn.addActionListener(this);
		bkUpBtn.addActionListener(this);

		// Display Date and Time
		SimpleDateFormat myFormat = new SimpleDateFormat("EEE, MMM d, ''yy");
		String dateString = myFormat.format(new Date());
		dateLbl.setText("" + dateString);
		
		getContentPane().setBackground(Color.white);
		setLocationRelativeTo(null); // center Jframe on screen
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		Object target = e.getSource();
		
		try {
			readOnlineUsers();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (target == sendBtn) {
			String msg = outGoingMsg.getText();
			if (msg.length() > 0) {
				try {
					String timeStamp = sdf.format(new Date());
					Details d1 = new Details(userName, timeStamp, msg, true);
					ObjectMessage m1 = session.createObjectMessage(d1);
					publisher.publish(m1);
					outGoingMsg.setText("");
					outGoingMsg.requestFocusInWindow();
				} catch (JMSException ex) {
					ex.printStackTrace();
				}
			}
		}

		if (target == clearBtn) {
			recievedMessages.setText(" ");
		}

		if (target == closeBtn) {
			int confirm = JOptionPane.showConfirmDialog(null,
					"Are you sure you wish to EXIT Chat?", "Exit Chat",
					JOptionPane.YES_NO_OPTION);

			if (confirm == JOptionPane.YES_OPTION) {
				try {
					Details d1 = new Details(userName, " System Message ",
							"has left the Chat", false);
					ObjectMessage m1 = session.createObjectMessage(d1);
					publisher.publish(m1);
					dispose();
					System.exit(0);
				} catch (Exception ex) {
					System.out.println("Exit Button Error " + ex);
				}
			}
		}

		if (target == bkUpBtn) {
			int confirm = JOptionPane.showConfirmDialog(null,
					"Are you sure you wish to Backup the chat text??",
					"Exit Chat", JOptionPane.YES_NO_OPTION);

			if (confirm == JOptionPane.YES_OPTION) {
				try {

					FileWriter fWriter = new FileWriter("D:\\myChatBackup.txt",
							true);
					PrintWriter outputFile = new PrintWriter(fWriter);
					recievedMessages.write(outputFile);

					JOptionPane.showMessageDialog(null,"Chat Backup Successful! File can be found at D:\\myChatBackup.txt");
				} catch (Exception ex) {
					System.out.println(ex);
				}
			}
		}
	}

	public void setMessageListener() throws JMSException {  // add asynchronous message listener
		subscriber.setMessageListener(new MessageListener() {
			public void onMessage(Message message) {
				try {
					Details d1 = null;
					if (message instanceof ObjectMessage) {
						d1 = (Details) ((ObjectMessage) message).getObject();						
			      
					      recievedMessages.append("[" + d1.getTimeStamp() + "] "
									+ d1.getUserName() + " : " + d1.getMessage()
									+ "\n");

						if(d1.online == false) {
							removeUser(d1.getUserName());
						}
					} else {
						System.out.println("Error - not an Object message");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		outGoingMsg.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent evt) {
				if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
					String msg = outGoingMsg.getText();
					if (msg.length() > 0) {
						try {
							String timeStamp = sdf.format(new Date());
							Details d1 = new Details(userName, timeStamp, msg,
									true);
							ObjectMessage m1 = session.createObjectMessage(d1);
							publisher.publish(m1);
							outGoingMsg.setText("");
							outGoingMsg.requestFocusInWindow();
						} catch (JMSException ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		});
	}

	public void readOnlineUsers() throws IOException {
		try {
			File file = new File("D:\\usersOnline.txt");
			FileReader fileReader;
			fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			usersOnlineTxtArea.setText("");
			while ((line = bufferedReader.readLine()) != null) 
			{
				usersOnlineTxtArea.append(line+"\n");				
			}
			fileReader.close();			
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void removeUser(String user) throws IOException
	{
		try {
			BufferedReader in = new BufferedReader(new FileReader(
					"D:\\usersOnline.txt")); // read in file containing current online Users
			String str = null;
			ArrayList<String> lines = new ArrayList<String>();
			while ((str = in.readLine()) != null) {
				lines.add(str);
			}
			
			lines.remove(user); // remove userName from the ArrayList

			PrintWriter pw = new PrintWriter(new FileOutputStream( 
					"D:\\usersOnline.txt")); // write ArrayList back to file
			for (String line : lines)
				pw.println(line);
			pw.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
	}

	public static void main(String[] args) throws Exception {
		myChatGui myGui = new myChatGui();
		myGui.outGoingMsg.requestFocusInWindow(); // set focus to textfield

	}
}
