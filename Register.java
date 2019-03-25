//������ע�ᴰ��
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;
public class Register extends JFrame implements ActionListener {
  JPanel panel1;
  JLabel lblName,lblGender,lblPwd,lblConfirmPwd,lblEmail;
  JTextField tfName, tfEmail;
  JPasswordField pwd, confirmPwd;
  JRadioButton rbMale, rbFemale;
  ButtonGroup btngGender;
  JButton btnOk, btnCancel, btnClear;
  String strIp;
  int port;
  //���ڽ��������ڶ�λ
  Dimension srnSize = Toolkit.getDefaultToolkit().getScreenSize();
  public Register(String ip,int p) {
    super("ע�����û�");
    strIp = ip;
    port = p;
    panel1 = new JPanel();
    this.getContentPane().add(panel1);
    lblName = new JLabel("�� �� ��:");
    lblGender = new JLabel("��    ��:");
    lblPwd = new JLabel("��    ��:");
    lblConfirmPwd = new JLabel("ȷ�Ͽ���:");
    lblEmail = new JLabel("�����ʼ�:");
    tfName = new JTextField(30);
    tfEmail = new JTextField(30);
    pwd = new JPasswordField(30);
    confirmPwd = new JPasswordField(30);
    rbMale = new JRadioButton("��", true);
    rbFemale = new JRadioButton("Ů");
    btngGender = new ButtonGroup();
    btnOk = new JButton("ȷ��");
    btnCancel = new JButton("����");
    btnClear = new JButton("���");

    //�������
    panel1.setLayout(null); //������ֶ�����
    panel1.setBackground(new Color(210, 255, 159));
    lblName.setBounds(20, 15, 60, 30);
    tfName.setBounds(80, 20, 190, 20);
    lblPwd.setBounds(20, 40, 60, 30);
    pwd.setBounds(80, 45, 190, 20);
    lblConfirmPwd.setBounds(20, 65, 60, 30);
    confirmPwd.setBounds(80, 70, 190, 20);
    lblGender.setBounds(20, 90, 60, 30);
    rbMale.setBounds(100, 95, 60, 20);
    rbFemale.setBounds(190, 95, 60, 20);
    lblEmail.setBounds(20, 115, 60, 30);
    tfEmail.setBounds(80, 120, 190, 20);
    btnOk.setBounds(20, 150, 60, 25);
    btnClear.setBounds(105, 150, 60, 25);
    btnCancel.setBounds(190, 150, 60, 25);

    rbMale.setOpaque(false);
    rbFemale.setOpaque(false);
    panel1.add(lblName);
    panel1.add(lblGender);
    panel1.add(lblPwd);
    panel1.add(lblConfirmPwd);
    panel1.add(lblEmail);
    panel1.add(tfName);
    panel1.add(tfEmail);
    panel1.add(pwd);
    panel1.add(confirmPwd);
    panel1.add(btnOk);
    panel1.add(btnCancel);
    panel1.add(btnClear);
    panel1.add(rbMale);
    panel1.add(rbFemale);
    btngGender.add(rbMale);
    btngGender.add(rbFemale);

    this.setSize(290, 220);
    this.setVisible(true);
    this.setResizable(false);
    //�����ڶ�λ����Ļ����
    this.setLocation((srnSize.width - getWidth())/2,(srnSize.height - getHeight())/2);
    //Ϊ������ťע�����
    btnOk.addActionListener(this);
    btnCancel.addActionListener(this);
    btnClear.addActionListener(this);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
  }
  //��ť��Ӧ�¼�����
  public void actionPerformed(ActionEvent ae) {
    Object source = new Object();
    source = ae.getSource();
    if (source.equals(btnOk)) 
      doRegister();
    if (source.equals(btnCancel)) { 
      this.dispose();
      new ChatClient();      
    }
    if (source.equals(btnClear)) { 
      tfName.setText("");
      pwd.setText("");
      confirmPwd.setText("");
      tfEmail.setText("");
    }
  } 

  //ע��
  public void doRegister() {
    NewUser data = new NewUser();
    data.name = tfName.getText();
    data.password = new String(pwd.getPassword());
    data.sex = rbMale.isSelected() ? "��" : "Ů";
    data.email = tfEmail.getText();
    //���������Ϣ
    if (data.name.length() == 0) {
      JOptionPane.showMessageDialog(null, "�û�������Ϊ��");
      return;
    }
    if (data.password.length() == 0) {
      JOptionPane.showMessageDialog(null, "���벻��Ϊ��");
      return;
    }
    if (!data.password.equals(new String(confirmPwd.getPassword()))) {
      JOptionPane.showMessageDialog(null, "�����������벻һ�£�����������");
      return;
    }
    int Found_flag = 0; //�ж�email�е�@��ʶ
    for (int i = 0; i < data.email.length(); i++) {
      if (data.email.charAt(i) == '@') {
        Found_flag++;
      }
    }
    if (Found_flag != 1) {
      JOptionPane.showMessageDialog(null, "���������ʽ����ȷ������������");
      return;
    }
    try {      
      Socket sock = new Socket(strIp, port);//���ӵ�������
      ObjectOutputStream os = new ObjectOutputStream(sock.getOutputStream());      
      os.writeObject( (NewUser) data);//д�ͻ����ϵ�������socket      
      BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
      String status = br.readLine();//�����Է�����socket�ĵ�½״̬
      JOptionPane.showMessageDialog(null, status);
      if (status.equals(data.name + "ע��ɹ�")) {
        tfName.setText("");
        pwd.setText("");
        confirmPwd.setText("");
        tfEmail.setText("");
      }
      //�ر�������
      os.close();
      br.close();
    }
    catch (InvalidClassException e1) {
      JOptionPane.showMessageDialog(null, "�����!");
    }
    catch (NotSerializableException e2) {
      JOptionPane.showMessageDialog(null, "����δ���л�!");
    }
    catch (IOException e3) {
      JOptionPane.showMessageDialog(null, "����д�뵽ָ��������!");
    }
  }
}
