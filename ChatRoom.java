//聊天室房间程序
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;
public class ChatRoom extends Thread implements ActionListener {
  static JFrame frmChat;
  JPanel pnlChat;
  JButton btnCls, btnExit, btnSend, btnClear, btnSave;
  JLabel lblUserList, lblMsg, lblSend, lblYou,lblNum, lblCount;
  JTextField tfMsg;
  java.awt.List userList;
  TextArea taMsg;
  JComboBox cmbUser;
  JCheckBox chPrivate;
  String strIp, myName;
  int port;
  Thread thread;

  public ChatRoom(String name, String ip, int p) {
    strIp = ip;
    myName = name;
    port = p;
    frmChat = new JFrame("聊天室" + "[用户:" + name + "]");
    pnlChat = new JPanel();
    frmChat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmChat.getContentPane().add(pnlChat);
    String list[] = {"所有人"};
    btnCls = new JButton("清屏");
    btnExit = new JButton("退出");
    btnSend = new JButton("发送");
    lblUserList = new JLabel("在线用户");
    lblMsg = new JLabel("聊天信息");
    lblSend = new JLabel("聊天内容:");
    lblYou = new JLabel("你对:");
    lblNum = new JLabel("人数:");
    lblCount = new JLabel("0");
    userList = new java.awt.List();
    tfMsg = new JTextField(170);
    cmbUser = new JComboBox(list);
    chPrivate = new JCheckBox("私聊");
    taMsg = new TextArea("", 300, 200, TextArea.SCROLLBARS_VERTICAL_ONLY); //只能向下滚动
    taMsg.setEditable(false); //不可编辑
    taMsg.setBackground(Color.white);

    //布局组件
    pnlChat.setLayout(null);
    pnlChat.setBackground(new Color(210, 255, 159));
    btnCls.setBounds(390, 320, 65, 25);
    btnExit.setBounds(470, 320, 65, 25);
    btnSend.setBounds(470, 290, 65, 25);
    lblUserList.setBounds(5, 0, 80, 30);
    lblNum.setBounds(90, 0, 40, 30);
    lblCount.setBounds(130, 0, 60, 30);
    lblMsg.setBounds(170, 0, 180, 30);
    lblYou.setBounds(5, 290, 40, 30);
    lblSend.setBounds(190, 282, 60, 40);
    userList.setBounds(5, 30, 150, 255);
    taMsg.setBounds(160, 30, 375, 255);
    tfMsg.setBounds(255, 290, 200, 25);
    cmbUser.setBounds(45, 290, 80, 25);
    chPrivate.setBounds(130, 292, 60, 20);
    chPrivate.setOpaque(false);

    pnlChat.add(btnCls);
    pnlChat.add(btnExit);
    pnlChat.add(btnSend);
    pnlChat.add(lblUserList);
    pnlChat.add(lblMsg);
    pnlChat.add(lblSend);
    pnlChat.add(lblYou);
    pnlChat.add(lblNum);
    pnlChat.add(lblCount);
    pnlChat.add(userList);
    pnlChat.add(taMsg);
    pnlChat.add(tfMsg);
    pnlChat.add(cmbUser);
    pnlChat.add(chPrivate);
    btnCls.addActionListener(this);
    btnExit.addActionListener(this);
    btnSend.addActionListener(this);
    userList.addActionListener(this);
    tfMsg.addActionListener(this);

    //启动聊天页面信息刷新线程
    Thread thread = new Thread(this);
    thread.start();
    frmChat.setSize(560, 390);
    frmChat.setVisible(true);
    frmChat.setResizable(false);
    frmChat.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
  }

  public void run() {
    int intMessageCounter = 0;
    int intUserTotal = 0;
    boolean isFirstLogin = true; //判断是否刚登陆
    boolean isFound; //判断是否找到用户
    Vector uExit = new Vector();
    try {
      while (true) {
        Socket sock = new Socket(strIp, port);        
        Info msg = new Info();
        ObjectOutputStream os = new ObjectOutputStream(sock.getOutputStream());
        os.writeObject( (Info) msg);//将信息发送到服务器        
        ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
        msg = (Info) ois.readObject();//接收服务器端信息
        //刷新聊天信息列表
        if (isFirstLogin) { //如果刚登陆
          intMessageCounter = msg.chat.size(); //屏蔽该用户登陆前的聊天内容
          isFirstLogin = false;
        }
        for (int i = intMessageCounter; i < msg.chat.size(); i++) {
          Chat toHim = (Chat) msg.chat.elementAt(i);
          String strInfo;
          if (toHim.fromUser.equals(myName)) {
            if (toHim.toUser.equals(myName)) 
              strInfo = "\n系统提示：请不要自言自语！";
            else {
              if (!toHim.whisper) //不是悄悄话
                strInfo = "\n【你】对【" + toHim.toUser + "】说：" + toHim.msg;
              else 
                strInfo = "\n【你】悄悄对【" + toHim.toUser + "】说：" + toHim.msg;
            }
          }
          else {
            if (toHim.toUser.equals(myName)) {
              if (!toHim.whisper)  //不是悄悄话
                strInfo = "\n【" + toHim.fromUser + "】对【你】说：" + toHim.msg;
              else 
                strInfo = "\n【" + toHim.fromUser + "】悄悄对【你】说：" + toHim.msg;
            }
            else {
              if (!toHim.fromUser.equals(toHim.toUser)) { //对方没有自言自语
                if (!toHim.whisper)  //不是悄悄话
                  strInfo = "\n【" + toHim.fromUser + "】对【" + toHim.toUser + "】说：" + toHim.msg;
                else 
                  strInfo = "";
              }
              else 
                strInfo = "";
            }
          }
          taMsg.append(strInfo);
          intMessageCounter++;
        }
        userList.removeAll(); //刷新在线用户
        for (int i = 0; i < msg.userOnLine.size(); i++) {
          String User = (String) msg.userOnLine.elementAt(i);
          userList.add(User);
        }
        lblCount.setText(new Integer(msg.userOnLine.size()).toString());
        //显示用户进入聊天室的信息
        if (msg.userOnLine.size() > intUserTotal) {
          String str = msg.userOnLine.elementAt(msg.userOnLine.size() - 1).
              toString();
          if (!str.equals(myName)) {
            taMsg.append("\n【" + str + "】来了");
          }
        }
        //显示用户离开聊天室的信息
        if (msg.userOnLine.size() < intUserTotal) {
          for (int b = 0; b < uExit.size(); b++) {
            isFound = false;
            for (int c = 0; c < msg.userOnLine.size(); c++) {
              if (uExit.elementAt(b).equals(msg.userOnLine.elementAt(c))) {
                isFound = true;
                break;
              }
            }
            if (!isFound) { //没有发现该用户
              if (!uExit.elementAt(b).equals(myName)) {
                taMsg.append("\n【" + uExit.elementAt(b) + "】走了");
              }
            }
          }
        }
        uExit = msg.userOnLine;
        intUserTotal = msg.userOnLine.size();
        os.close();
        ois.close();
        sock.close();
        thread.sleep(1000);
      }
    }
    catch (Exception e) {
      JOptionPane.showMessageDialog(null, "不能连接服务器！");
    }
  }

  //处理组件的事件
  public void actionPerformed(ActionEvent ae) {
    Object source = (Object) ae.getSource();
    if (source.equals(btnCls)) {
      clearAll();
    }
    if (source.equals(btnExit)) {
      exit();
    }
    if (source.equals(btnSend)) {
      sendMsg();
    }
    if (source.equals(userList)) { //双击列表框
      changeUser();
    }
  }

  //清屏
  public void clearAll() {
    taMsg.setText("");
  }

  //退出程序
  public void exit() {
    ExitUser exit = new ExitUser();
    exit.name = myName;
    try {
      Socket sock = new Socket(strIp, port);      
      ObjectOutputStream os = new ObjectOutputStream(sock.getOutputStream());
      os.writeObject(exit);//告诉服务器用户退出
      os.close();
      sock.close();
      frmChat.dispose();
      System.exit(0);
    }
    catch (Exception e) {
      System.exit(0);
    }
  } 

  //发送信息
  public void sendMsg() {
    Chat chatobj = new Chat();
    chatobj.fromUser = myName;
    chatobj.msg = tfMsg.getText();
    chatobj.toUser = String.valueOf(cmbUser.getSelectedItem());
    chatobj.whisper = chPrivate.isSelected() ? true : false;    
    try {
      Socket sock = new Socket(strIp, port);
      ObjectOutputStream os = new ObjectOutputStream(sock.getOutputStream());
      os.writeObject(chatobj);//向服务器发送信息
      tfMsg.setText(""); //清空文本框
      os.close();
      sock.close();
    }
    catch (Exception e) {
    	JOptionPane.showMessageDialog(null,"连接已中断,发送信息失败");
    }
  }

  //将所选用户添加到cmbUser中
  public void changeUser() {
    boolean bHave = true;//是否已经存在于类表中
    String selected = userList.getSelectedItem();
    for (int i = 0; i < cmbUser.getItemCount(); i++) {
      if (selected.equals(cmbUser.getItemAt(i))) {
        bHave = false;
        break;
      }
    }
    if (bHave ) 
      cmbUser.insertItemAt(selected, 0);
    cmbUser.setSelectedItem(selected);
  }
}