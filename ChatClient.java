//聊天室客户端程序
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
  //用于将窗口定位
  Dimension scnSize = Toolkit.getDefaultToolkit().getScreenSize();
  public ChatClient() { //构造方法
    super("登录聊天室");
    pnlLogin = new JPanel();
    this.getContentPane().add(pnlLogin);
    lblServer = new JLabel("服务器:");
    lblPort = new JLabel("端口:");
    lblName = new JLabel("用户名:");
    lblPassword = new JLabel("口  令:");
    tfServer = new JTextField(15);
    tfServer.setText("127.0.0.1");
    tfPort = new JTextField(6);
    tfPort.setText("8000");
    tfName = new JTextField(20);
    pwd = new JPasswordField(20);
    btnLogin = new JButton("登录");
    btnRegister = new JButton("注册");
    pnlLogin.setLayout(null); //组件用手动布局
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
    //设置登录窗口
    setResizable(false);
    setSize(290, 190);
    setVisible(true);
    setLocation((scnSize.width - getWidth())/2,(scnSize.height - getHeight())/2);
    //为按钮注册监听
    btnLogin.addActionListener(this);
    btnRegister.addActionListener(this);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
  }

  //按钮监听响应
  public void actionPerformed(ActionEvent ae) {
    Object source = ae.getSource();
    strServerIp = tfServer.getText();
    try {
      port = Integer.valueOf(tfPort.getText());
      if (port <= 1024) {
        JOptionPane.showMessageDialog(null, "请使用大于1024的端口号");
        return;
      }
    }
    catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(null, "输入的端口号不是整数");
    }
    if (source.equals(btnLogin)) {      
      if (tfName.getText().equals("") || pwd.getPassword().equals("")) 
        JOptionPane.showMessageDialog(null, "用户名或密码不能为空");
      else
        login();
    }
    if (source.equals(btnRegister)) {
      this.dispose();
      new Register(strServerIp, port);
    }
  }

  public void login() { //登录
    User data = new User();
    data.name = tfName.getText();
    data.password = new String(pwd.getPassword());
    try {
      Socket sock = new Socket(strServerIp, port);//连接到服务器
      ObjectOutputStream os = new ObjectOutputStream(sock.getOutputStream());
      os.writeObject( (User) data); //将登录用户信息送到服务器socket
      //读来自服务器socket的登录状态
      BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
      String status = br.readLine();
      if (status.equals("登录成功")) {
        new ChatRoom( (String)data.name, strServerIp, port);
        this.dispose();
        //关闭流对象
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
      JOptionPane.showMessageDialog(null, "连接到指定服务器失败!");
    }
    catch (Exception e2) {
      JOptionPane.showMessageDialog(null, "发生错误!");
    }
  }
  public static void main(String args[]) {
    new ChatClient();
  }
}
