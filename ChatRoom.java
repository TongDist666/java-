//�����ҷ������
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
    frmChat = new JFrame("������" + "[�û�:" + name + "]");
    pnlChat = new JPanel();
    frmChat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmChat.getContentPane().add(pnlChat);
    String list[] = {"������"};
    btnCls = new JButton("����");
    btnExit = new JButton("�˳�");
    btnSend = new JButton("����");
    lblUserList = new JLabel("�����û�");
    lblMsg = new JLabel("������Ϣ");
    lblSend = new JLabel("��������:");
    lblYou = new JLabel("���:");
    lblNum = new JLabel("����:");
    lblCount = new JLabel("0");
    userList = new java.awt.List();
    tfMsg = new JTextField(170);
    cmbUser = new JComboBox(list);
    chPrivate = new JCheckBox("˽��");
    taMsg = new TextArea("", 300, 200, TextArea.SCROLLBARS_VERTICAL_ONLY); //ֻ�����¹���
    taMsg.setEditable(false); //���ɱ༭
    taMsg.setBackground(Color.white);

    //�������
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

    //��������ҳ����Ϣˢ���߳�
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
    boolean isFirstLogin = true; //�ж��Ƿ�յ�½
    boolean isFound; //�ж��Ƿ��ҵ��û�
    Vector uExit = new Vector();
    try {
      while (true) {
        Socket sock = new Socket(strIp, port);        
        Info msg = new Info();
        ObjectOutputStream os = new ObjectOutputStream(sock.getOutputStream());
        os.writeObject( (Info) msg);//����Ϣ���͵�������        
        ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
        msg = (Info) ois.readObject();//���շ���������Ϣ
        //ˢ��������Ϣ�б�
        if (isFirstLogin) { //����յ�½
          intMessageCounter = msg.chat.size(); //���θ��û���½ǰ����������
          isFirstLogin = false;
        }
        for (int i = intMessageCounter; i < msg.chat.size(); i++) {
          Chat toHim = (Chat) msg.chat.elementAt(i);
          String strInfo;
          if (toHim.fromUser.equals(myName)) {
            if (toHim.toUser.equals(myName)) 
              strInfo = "\nϵͳ��ʾ���벻Ҫ�������";
            else {
              if (!toHim.whisper) //�������Ļ�
                strInfo = "\n���㡿�ԡ�" + toHim.toUser + "��˵��" + toHim.msg;
              else 
                strInfo = "\n���㡿���Ķԡ�" + toHim.toUser + "��˵��" + toHim.msg;
            }
          }
          else {
            if (toHim.toUser.equals(myName)) {
              if (!toHim.whisper)  //�������Ļ�
                strInfo = "\n��" + toHim.fromUser + "���ԡ��㡿˵��" + toHim.msg;
              else 
                strInfo = "\n��" + toHim.fromUser + "�����Ķԡ��㡿˵��" + toHim.msg;
            }
            else {
              if (!toHim.fromUser.equals(toHim.toUser)) { //�Է�û����������
                if (!toHim.whisper)  //�������Ļ�
                  strInfo = "\n��" + toHim.fromUser + "���ԡ�" + toHim.toUser + "��˵��" + toHim.msg;
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
        userList.removeAll(); //ˢ�������û�
        for (int i = 0; i < msg.userOnLine.size(); i++) {
          String User = (String) msg.userOnLine.elementAt(i);
          userList.add(User);
        }
        lblCount.setText(new Integer(msg.userOnLine.size()).toString());
        //��ʾ�û����������ҵ���Ϣ
        if (msg.userOnLine.size() > intUserTotal) {
          String str = msg.userOnLine.elementAt(msg.userOnLine.size() - 1).
              toString();
          if (!str.equals(myName)) {
            taMsg.append("\n��" + str + "������");
          }
        }
        //��ʾ�û��뿪�����ҵ���Ϣ
        if (msg.userOnLine.size() < intUserTotal) {
          for (int b = 0; b < uExit.size(); b++) {
            isFound = false;
            for (int c = 0; c < msg.userOnLine.size(); c++) {
              if (uExit.elementAt(b).equals(msg.userOnLine.elementAt(c))) {
                isFound = true;
                break;
              }
            }
            if (!isFound) { //û�з��ָ��û�
              if (!uExit.elementAt(b).equals(myName)) {
                taMsg.append("\n��" + uExit.elementAt(b) + "������");
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
      JOptionPane.showMessageDialog(null, "�������ӷ�������");
    }
  }

  //����������¼�
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
    if (source.equals(userList)) { //˫���б��
      changeUser();
    }
  }

  //����
  public void clearAll() {
    taMsg.setText("");
  }

  //�˳�����
  public void exit() {
    ExitUser exit = new ExitUser();
    exit.name = myName;
    try {
      Socket sock = new Socket(strIp, port);      
      ObjectOutputStream os = new ObjectOutputStream(sock.getOutputStream());
      os.writeObject(exit);//���߷������û��˳�
      os.close();
      sock.close();
      frmChat.dispose();
      System.exit(0);
    }
    catch (Exception e) {
      System.exit(0);
    }
  } 

  //������Ϣ
  public void sendMsg() {
    Chat chatobj = new Chat();
    chatobj.fromUser = myName;
    chatobj.msg = tfMsg.getText();
    chatobj.toUser = String.valueOf(cmbUser.getSelectedItem());
    chatobj.whisper = chPrivate.isSelected() ? true : false;    
    try {
      Socket sock = new Socket(strIp, port);
      ObjectOutputStream os = new ObjectOutputStream(sock.getOutputStream());
      os.writeObject(chatobj);//�������������Ϣ
      tfMsg.setText(""); //����ı���
      os.close();
      sock.close();
    }
    catch (Exception e) {
    	JOptionPane.showMessageDialog(null,"�������ж�,������Ϣʧ��");
    }
  }

  //����ѡ�û���ӵ�cmbUser��
  public void changeUser() {
    boolean bHave = true;//�Ƿ��Ѿ������������
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