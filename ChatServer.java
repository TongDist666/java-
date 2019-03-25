//聊天室服务器程序
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
public class ChatServer extends JFrame {
  ServerSocket sSocket;
  JPanel panel1, pnlInfo, pnlLeft;
  JLabel lblSts, lblUserOnLine, lblPort, lblLog, lblUser;
  JTextField tfSts, tfUserOnLine, tfPort;
  JButton btnStart, btnStop;
  TextArea taLog;
  JList listUser;
  JScrollPane spUser;
  boolean bStart = false;
  Thread sThread;
  static Vector u = new Vector(1, 1);
  static Vector v = new Vector(1, 1);
  static Vector vList = new Vector();
  public ChatServer() {
    super("聊天室服务器程序");
    setSize(550, 490);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);
    panel1 = new JPanel();
    panel1.setLayout(null);
    panel1.setBackground(new Color(234, 66, 123));
    pnlInfo = new JPanel(new GridLayout(7, 1));
    pnlInfo.setBackground(new Color(66, 99, 222));
    pnlInfo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
        createTitledBorder(""), BorderFactory.createEmptyBorder(1, 1, 1, 1)));

    lblSts = new JLabel("当前状态:");
    lblSts.setForeground(Color.YELLOW);
    tfSts = new JTextField(10);
    tfSts.setEditable(false);
    lblUserOnLine = new JLabel("当前在线人数:");
    lblUserOnLine.setForeground(Color.YELLOW);
    tfUserOnLine = new JTextField("0 人", 10);
    tfUserOnLine.setEditable(false);
    lblPort = new JLabel("服务器端口:");
    lblPort.setForeground(Color.YELLOW);
    tfPort = new JTextField("8000", 10);
    lblUser = new JLabel("在线用户列表");
    lblUser.setForeground(Color.YELLOW);

    listUser = new JList();
    listUser.setVisibleRowCount(17);
    spUser = new JScrollPane();
    
    int i = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;
    spUser.setVerticalScrollBarPolicy(i);

    spUser.getViewport().setView(listUser);
    btnStart = new JButton("启动服务");
    btnStop = new JButton("停止服务");
    btnStop.setEnabled(false);

    lblLog = new JLabel("[服务器日志]");
    lblLog.setForeground(Color.YELLOW);
    taLog = new TextArea(20, 50);
    pnlInfo.add(lblSts);
    pnlInfo.add(tfSts);
    pnlInfo.add(lblUserOnLine);
    pnlInfo.add(tfUserOnLine);
    pnlInfo.add(lblPort);
    pnlInfo.add(tfPort);
    pnlInfo.add(lblUser);

    pnlLeft = new JPanel(new GridLayout(2, 1));
    pnlLeft.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
        createTitledBorder(""), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    pnlLeft.add(pnlInfo);
    pnlLeft.add(spUser);

    pnlLeft.setBounds(5, 5, 130, 400);
    lblLog.setBounds(140, 5, 100, 30);
    taLog.setBounds(140, 35, 400, 370);
    btnStart.setBounds(150, 420, 90, 25);
    btnStop.setBounds(270, 420, 90, 25);
    panel1.add(pnlLeft);
    panel1.add(lblLog);
    panel1.add(taLog);
    panel1.add(btnStart);
    panel1.add(btnStop);
    this.getContentPane().add(panel1);
    setVisible(true);
    BtnAction act = new BtnAction();
    btnStart.addActionListener(act);
    btnStop.addActionListener(act);
  }

  //按钮监听响应处理类
  private class BtnAction implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      Object source = ae.getSource();
      if (source.equals(btnStart)) {
        try {
          int port = Integer.valueOf(tfPort.getText());
          if (port <= 1024) {
            fail("请使用大于1024的端口号");
            return;
          }
          sSocket = new ServerSocket(port);
          btnStop.setEnabled(true);
          btnStart.setEnabled(false);
          bStart = true;
          tfPort.setEditable(false);
          sThread = new mThread();
          sThread.start();
          tfSts.setText("服务已启动");
          taLog.append("服务启动时间:" + (new Date()).toLocaleString() + "\n");
        }
        catch (IOException e) {
          fail("不能启动服务,可能端口被占用！");
        }
        catch (NumberFormatException e) {
          fail("输入的端口号不是整数");
        }
        catch (Exception e) {
          fail("创建线程失败");
        }
      }
      if (source.equals(btnStop)) {
        try {
          sSocket.close();
          btnStop.setEnabled(false);
          btnStart.setEnabled(true);
          bStart = false;
          tfPort.setEditable(true);
          sThread.stop();
          tfSts.setText("服务已停止");
          taLog.append("服务停止时间:" + (new Date()).toLocaleString() + "\n");
        }
        catch (IOException e) {
          fail("不能启动服务！");
        }
        catch (SecurityException e) {
          fail("终止服务失败！");
        }
      }
    }
  }

  public static void fail(String str) {
    JOptionPane.showMessageDialog(null, str, "错误", JOptionPane.ERROR_MESSAGE);
  }

  public static void main(String args[]) {
    new ChatServer();
  }

  //用一个线程来启动监听
  private class mThread extends Thread {
    public void run() {
      try {
        while (true) {
          if (bStart) {            
            Socket client = sSocket.accept();//监听并接受客户的请求
            Connection con = new Connection(client, u, v); //每个客户端为一个线程
          }
          else
            Thread.sleep(100);
        }
      }
      catch (IOException e) {
        fail("不能监听！");
      }
      catch (InterruptedException e) {
      }
    }
  }

//内部类,处理线程
  class Connection extends Thread {
    protected Socket client;
    Vector userOnline;
    Vector userChat;
    protected ObjectInputStream fromClient; //从客户到服务器
    protected PrintStream toClient; //返回信息到客户端
    Object obj;
    public Connection(Socket sock, Vector u, Vector c) {
      client = sock;
      userOnline = u;
      userChat = c;
      try {//双向通信        
        fromClient = new ObjectInputStream(client.getInputStream());
        toClient = new PrintStream(client.getOutputStream());
      }
      catch (IOException e) {
        try {
          client.close();
        }
        catch (IOException e1) {
          fail("不能建立流" + e1);
          return;
        }
      }
      this.start();
    }

    public void run() {
      try { //obj是Object类的对象
        obj = (Object) fromClient.readObject();
        if (obj.getClass().getName().equals("User")) {
          doLogin();
        }
        if (obj.getClass().getName().equals("NewUser")) {
          doRegiste();
        }
        if (obj.getClass().getName().equals("Info")) {
          doInfo();
        }
        if (obj.getClass().getName().equals("Chat")) {
          doChat();
        }
        if (obj.getClass().getName().equals("ExitUser")) {
          doExit();
        }
      }
      catch (IOException e) {
        fail(e.toString());
      }
      catch (ClassNotFoundException e1) {
        fail("读对象发生错误！" + e1);
      }
      finally {
        try {
          client.close();
        }
        catch (IOException e) {
          fail(e.toString());
        }
      }
    }

    //登录
    public void doLogin() {
      try {
        User u1 = (User) obj;  //创建对象,从文件中查找是否存在      
        FileInputStream fin = new FileInputStream("user.dat");
        ObjectInputStream objInput1 = new ObjectInputStream(fin);
        vList = (Vector) objInput1.readObject();
        boolean bHave = false; //查找判断标志用户是否存在
        for (int i = 0; i < vList.size(); i++) {
          NewUser reg = (NewUser) vList.elementAt(i);
          if (reg.name.equals(u1.name)) {
            bHave = true;
            if (!reg.password.equals(u1.password)) {
              toClient.println("用户密码错误");
              break;
            }
            else {//判断是否已经登录              
              boolean bHaveLogin = false;
              for (int a = 0; a < userOnline.size(); a++) {
                if (u1.name.equals(userOnline.elementAt(a))) {
                  bHaveLogin = true;
                  break;
                }
              }
              if (!bHaveLogin) {
                userOnline.addElement(u1.name);
                toClient.println("登录成功");
                taLog.append("用户" + u1.name + "登录成功，" +
                             "登录时间:" + (new Date()).toLocaleString() + "\n");
                listUser.setListData(userOnline);
                tfUserOnLine.setText(userOnline.size() + "人");
                break;
              }
              else 
                toClient.println("该用户已登录");
            }
          }
        }
        if (!bHave) 
          toClient.println("用户名不存在，请先注册");
        fin.close();
        objInput1.close();
        fromClient.close();
      }
      catch (ClassNotFoundException e) {
        fail(e.toString());
      }
      catch (IOException e) {
        fail("数据文件user.dat错误");
      }
    }    
    public void doRegiste() {//注册
      try {
        boolean flag = true; 
        NewUser new1 = (NewUser) obj;
        File fileU = new File("user.dat");
        if (fileU.length() != 0) { //判断是否是第一个注册用户
          ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileU));
          vList = (Vector) in.readObject();
          //判断是否有重名
          for (int i = 0; i < vList.size(); i++) {
            NewUser reg = (NewUser) vList.elementAt(i);
            if (reg.name.equals(new1.name)) {
              toClient.println("用户名已经存在,请使用其他用户名");
              flag = false;
              break;
            }
          }
        }
        if (flag) {          
          vList.addElement(new1);//注册新用户
          FileOutputStream fos = new FileOutputStream(fileU);
          ObjectOutputStream objout = new ObjectOutputStream(fos);
          objout.writeObject(vList);//保存到文件
          //发送注册成功信息
          toClient.println(new1.name + "注册成功");
          taLog.append("用户" + new1.name + "注册成功, " +
                       "注册时间:" + (new Date()).toLocaleString() + "\n");
          fos.close();
          objout.close();
          fromClient.close();
        }
      }
      catch (ClassNotFoundException e) {
        fail(e.toString());
      }
      catch (IOException e) {
        fail(e.toString());
      }
    }

    //发送信息
    public void doInfo() {
      try {
        Info mess = new Info();
        mess.userOnLine = userOnline;
        mess.chat = userChat;
        ObjectOutputStream objos = new ObjectOutputStream(client.
            getOutputStream());
        objos.writeObject( (Info) mess);
        client.close();
        objos.close();
      }
      catch (IOException e) {
        fail(e.toString());
      }
    }

    public void doChat() {
      //将接收到的对象值赋给聊天信息的序列化对象
      Chat cObj = new Chat();
      cObj = (Chat) obj;
      //将聊天信息的序列化对象填加到保存聊天信息的矢量中
      userChat.addElement( (Chat) cObj);
      return;
    }

    //退出
    public void doExit() {
      ExitUser exit1 = new ExitUser();
      exit1 = (ExitUser) obj;
      userOnline.removeElement(exit1.name); 
      listUser.setListData(userOnline);
      tfUserOnLine.setText(userOnline.size() + "人");
      taLog.append("用户" + exit1.name + "已经退出, " +
                   "退出时间:" + (new Date()).toLocaleString() + "\n");
    }
  }
}