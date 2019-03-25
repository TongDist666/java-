//聊天室注册窗口
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
  //用于将窗口用于定位
  Dimension srnSize = Toolkit.getDefaultToolkit().getScreenSize();
  public Register(String ip,int p) {
    super("注册新用户");
    strIp = ip;
    port = p;
    panel1 = new JPanel();
    this.getContentPane().add(panel1);
    lblName = new JLabel("用 户 名:");
    lblGender = new JLabel("性    别:");
    lblPwd = new JLabel("口    令:");
    lblConfirmPwd = new JLabel("确认口令:");
    lblEmail = new JLabel("电子邮件:");
    tfName = new JTextField(30);
    tfEmail = new JTextField(30);
    pwd = new JPasswordField(30);
    confirmPwd = new JPasswordField(30);
    rbMale = new JRadioButton("男", true);
    rbFemale = new JRadioButton("女");
    btngGender = new ButtonGroup();
    btnOk = new JButton("确定");
    btnCancel = new JButton("返回");
    btnClear = new JButton("清空");

    //布局组件
    panel1.setLayout(null); //组件用手动布局
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
    //将窗口定位在屏幕中央
    this.setLocation((srnSize.width - getWidth())/2,(srnSize.height - getHeight())/2);
    //为三个按钮注册监听
    btnOk.addActionListener(this);
    btnCancel.addActionListener(this);
    btnClear.addActionListener(this);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
  }
  //按钮响应事件处理
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

  //注册
  public void doRegister() {
    NewUser data = new NewUser();
    data.name = tfName.getText();
    data.password = new String(pwd.getPassword());
    data.sex = rbMale.isSelected() ? "男" : "女";
    data.email = tfEmail.getText();
    //检查输入信息
    if (data.name.length() == 0) {
      JOptionPane.showMessageDialog(null, "用户名不能为空");
      return;
    }
    if (data.password.length() == 0) {
      JOptionPane.showMessageDialog(null, "密码不能为空");
      return;
    }
    if (!data.password.equals(new String(confirmPwd.getPassword()))) {
      JOptionPane.showMessageDialog(null, "密码两次输入不一致，请重新输入");
      return;
    }
    int Found_flag = 0; //判断email中的@标识
    for (int i = 0; i < data.email.length(); i++) {
      if (data.email.charAt(i) == '@') {
        Found_flag++;
      }
    }
    if (Found_flag != 1) {
      JOptionPane.showMessageDialog(null, "电子邮箱格式不正确，请重新输入");
      return;
    }
    try {      
      Socket sock = new Socket(strIp, port);//连接到服务器
      ObjectOutputStream os = new ObjectOutputStream(sock.getOutputStream());      
      os.writeObject( (NewUser) data);//写客户资料到服务器socket      
      BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
      String status = br.readLine();//读来自服务器socket的登陆状态
      JOptionPane.showMessageDialog(null, status);
      if (status.equals(data.name + "注册成功")) {
        tfName.setText("");
        pwd.setText("");
        confirmPwd.setText("");
        tfEmail.setText("");
      }
      //关闭流对象
      os.close();
      br.close();
    }
    catch (InvalidClassException e1) {
      JOptionPane.showMessageDialog(null, "类错误!");
    }
    catch (NotSerializableException e2) {
      JOptionPane.showMessageDialog(null, "对象未序列化!");
    }
    catch (IOException e3) {
      JOptionPane.showMessageDialog(null, "不能写入到指定服务器!");
    }
  }
}
