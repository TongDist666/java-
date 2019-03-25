//�����ҿͻ��˳���
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;
public class ChatClient extends JFrame implements ActionListener {
  JPanel pnlLogin;
  JButton btnLogin, btnRegister;
  JLabel lblServer, lblPort, lblName, lblPassword, lblLogo;
  JTextField tfName, tfServer, tfPort;
  JPasswordField pwd;
  String strServerIp;
  int port;
  //���ڽ����ڶ�λ
  Dimension scnSize = Toolkit.getDefaultToolkit().getScreenSize();
  public ChatClient() { //���췽��
    super("��¼������");
    pnlLogin = new JPanel();
    this.getContentPane().add(pnlLogin);
    lblServer = new JLabel("������:");
    lblPort = new JLabel("�˿�:");
    lblName = new JLabel("�û���:");
    lblPassword = new JLabel("��  ��:");
    tfServer = new JTextField(15);
    tfServer.setText("127.0.0.1");
    tfPort = new JTextField(6);
    tfPort.setText("8000");
    tfName = new JTextField(20);
    pwd = new JPasswordField(20);
    btnLogin = new JButton("��¼");
    btnRegister = new JButton("ע��");
    pnlLogin.setLayout(null); //������ֶ�����
    pnlLogin.setBackground(new Color(210, 255, 159));
    lblServer.setBounds(20, 15, 50, 30);
    tfServer.setBounds(70, 15, 102, 25);
    lblPort.setBounds(175, 15, 35, 30);
    tfPort.setBounds(210, 15, 55, 25);
    lblName.setBounds(20, 50, 50, 30);
    tfName.setBounds(70, 50, 195, 25);
    lblPassword.setBounds(20, 80, 50, 30);
    pwd.setBounds(70, 80, 195, 25);
    btnLogin.setBounds(50, 120, 70, 25);
    btnRegister.setBounds(170, 120, 70, 25);
    pnlLogin.add(lblServer);
    pnlLogin.add(tfServer);
    pnlLogin.add(lblPort);
    pnlLogin.add(tfPort);
    pnlLogin.add(lblName);
    pnlLogin.add(tfName);
    pnlLogin.add(lblPassword);
    pnlLogin.add(pwd);
    pnlLogin.add(btnLogin);
    pnlLogin.add(btnRegister);
    //���õ�¼����
    setResizable(false);
    setSize(290, 190);
    setVisible(true);
    setLocation((scnSize.width - getWidth())/2,(scnSize.height - getHeight())/2);
    //Ϊ��ťע�����
    btnLogin.addActionListener(this);
    btnRegister.addActionListener(this);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
  }

  //��ť������Ӧ
  public void actionPerformed(ActionEvent ae) {
    Object source = ae.getSource();
    strServerIp = tfServer.getText();
    try {
      port = Integer.valueOf(tfPort.getText());
      if (port <= 1024) {
        JOptionPane.showMessageDialog(null, "��ʹ�ô���1024�Ķ˿ں�");
        return;
      }
    }
    catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(null, "����Ķ˿ںŲ�������");
    }
    if (source.equals(btnLogin)) {      
      if (tfName.getText().equals("") || pwd.getPassword().equals("")) 
        JOptionPane.showMessageDialog(null, "�û��������벻��Ϊ��");
      else
        login();
    }
    if (source.equals(btnRegister)) {
      this.dispose();
      new Register(strServerIp, port);
    }
  }

  public void login() { //��¼
    User data = new User();
    data.name = tfName.getText();
    data.password = new String(pwd.getPassword());
    try {
      Socket sock = new Socket(strServerIp, port);//���ӵ�������
      ObjectOutputStream os = new ObjectOutputStream(sock.getOutputStream());
      os.writeObject( (User) data); //����¼�û���Ϣ�͵�������socket
      //�����Է�����socket�ĵ�¼״̬
      BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
      String status = br.readLine();
      if (status.equals("��¼�ɹ�")) {
        new ChatRoom( (String)data.name, strServerIp, port);
        this.dispose();
        //�ر�������
        os.close();
        br.close();
        sock.close();
      }
      else {
        JOptionPane.showMessageDialog(null, status);
        os.close();
        br.close();
        sock.close();
      }
    }
    catch (ConnectException e1) {
      JOptionPane.showMessageDialog(null, "���ӵ�ָ��������ʧ��!");
    }
    catch (Exception e2) {
      JOptionPane.showMessageDialog(null, "��������!");
    }
  }
  public static void main(String args[]) {
    new ChatClient();
  }
}
