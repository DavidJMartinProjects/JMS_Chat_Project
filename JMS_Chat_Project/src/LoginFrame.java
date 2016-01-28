import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.jms.ObjectMessage;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class LoginFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JLabel jcomp1;
	private JLabel jcomp2;
	private JTextField userNameTf;
	private JButton enterChatBtn;

	public LoginFrame() {

		super("Login Screen");
		// construct components
		jcomp1 = new JLabel("Welcome to JMS Chat Application");
		jcomp2 = new JLabel("Enter your Username : ");
		userNameTf = new JTextField(5);
		enterChatBtn = new JButton("Enter Chat");

		// adjust size and set layout
		setSize(510, 170);
		setLayout(null);

		// add components
		add(jcomp1);
		add(jcomp2);
		add(userNameTf);
		add(enterChatBtn);

		// set component bounds (only needed by Absolute Positioning)
		jcomp1.setBounds(155, 20, 195, 25);
		jcomp2.setBounds(55, 80, 145, 25);
		userNameTf.setBounds(190, 80, 125, 25);
		enterChatBtn.setBounds(320, 80, 100, 25);

		enterChatBtn.addActionListener(this); // add actionistener to button

		getContentPane().setBackground(Color.WHITE);
		setLocationRelativeTo(null); // center Jframe on screen
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {

		Object target = e.getSource();

		if (target == enterChatBtn) {
			login();
		}
	}

	public void login() {
		try 
		{
			this.setVisible(false);
			myChatGui myGui = new myChatGui();
			new Timer(1000, myGui).start(); // create timer
			String userName = userNameTf.getText().trim();
			myGui.userName = userNameTf.getText();
			myGui.setTitle(userName + "'s Client");

			Details d1 = new Details(myGui.userName, " System Message ",
					"has joined the Chat ", true);

			ObjectMessage m1 = myGui.session.createObjectMessage(d1);
			myGui.publisher.publish(m1);

			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("D:\\usersOnline.txt", true)));
				out.println(userName);
				out.close();
			} 
			catch (IOException ex) {
				System.out.println("Error Writting username to file");
			}
		} 
		catch (Exception e1) {
			e1.printStackTrace();
		}
	}	

	public static void main(String[] args) {
		LoginFrame myLoginFrame = new LoginFrame();
	}
}