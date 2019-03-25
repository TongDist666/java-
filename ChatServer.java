//�����ҷ���������
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
    super("�����ҷ���������");
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

    lblSts = new JLabel("��ǰ״̬:");
    lblSts.setForeground(Color.YELLOW);
    tfSts = new JTextField(10);
    tfSts.setEditable(false);
    lblUserOnLine = new JLabel("��ǰ��������:");
    lblUserOnLine.setForeground(Color.YELLOW);
    tfUserOnLine = new JTextField("0 ��", 10);
    tfUserOnLine.setEditable(false);
    lblPort = new JLabel("�������˿�:");
    lblPort.setForeground(Color.YELLOW);
    tfPort = new JTextField("8000", 10);
    lblUser = new JLabel("�����û��б�");
    lblUser.setForeground(Color.YELLOW);

    listUser = new JList();
    listUser.setVisibleRowCount(17);
    spUser = new JScrollPane();
    
    int i = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;
    spUser.setVerticalScrollBarPolicy(i);

    spUser.getViewport().setView(listUser);
    btnStart = new JButton("��������");
    btnStop = new JButton("ֹͣ����");
    btnStop.setEnabled(false);

    lblLog = new JLabel("[��������־]");
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

  //��ť������Ӧ������
  private class BtnAction implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      Object source = ae.getSource();
      if (source.equals(btnStart)) {
        try {
          int port = Integer.valueOf(tfPort.getText());
          if (port <= 1024) {
            fail("��ʹ�ô���1024�Ķ˿ں�");
            return;
          }
          sSocket = new ServerSocket(port);
          btnStop.setEnabled(true);
          btnStart.setEnabled(false);
          bStart = true;
          tfPort.setEditable(false);
          sThread = new mThread();
          sThread.start();
          tfSts.setText("����������");
          taLog.append("��������ʱ��:" + (new Date()).toLocaleString() + "\n");
        }
        catch (IOException e) {
          fail("������������,���ܶ˿ڱ�ռ�ã�");
        }
        catch (NumberFormatException e) {
          fail("����Ķ˿ںŲ�������");
        }
        catch (Exception e) {
          fail("�����߳�ʧ��");
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
          tfSts.setText("������ֹͣ");
          taLog.append("����ֹͣʱ��:" + (new Date()).toLocaleString() + "\n");
        }
        catch (IOException e) {
          fail("������������");
        }
        catch (SecurityException e) {
          fail("��ֹ����ʧ�ܣ�");
        }
      }
    }
  }

  public static void fail(String str) {
    JOptionPane.showMessageDialog(null, str, "����", JOptionPane.ERROR_MESSAGE);
  }

  public static void main(String args[]) {
    new ChatServer();
  }

  //��һ���߳�����������
  private class mThread extends Thread {
    public void run() {
      try {
        while (true) {
          if (bStart) {            
            Socket client = sSocket.accept();//���������ܿͻ�������
            Connection con = new Connection(client, u, v); //ÿ���ͻ���Ϊһ���߳�
          }
          else
            Thread.sleep(100);
        }
      }
      catch (IOException e) {
        fail("���ܼ�����");
      }
      catch (InterruptedException e) {
      }
    }
  }

//�ڲ���,�����߳�
  class Connection extends Thread {
    protected Socket client;
    Vector userOnline;
    Vector userChat;
    protected ObjectInputStream fromClient; //�ӿͻ���������
    protected PrintStream toClient; //������Ϣ���ͻ���
    Object obj;
    public Connection(Socket sock, Vector u, Vector c) {
      client = sock;
      userOnline = u;
      userChat = c;
      try {//˫��ͨ��        
        fromClient = new ObjectInputStream(client.getInputStream());
        toClient = new PrintStream(client.getOutputStream());
      }
      catch (IOException e) {
        try {
          client.close();
        }
        catch (IOException e1) {
          fail("���ܽ�����" + e1);
          return;
        }
      }
      this.start();
    }

    public void run() {
      try { //obj��Object��Ķ���
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
        fail("������������" + e1);
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

    //��¼
    public void doLogin() {
      try {
        User u1 = (User) obj;  //��������,���ļ��в����Ƿ����      
        FileInputStream fin = new FileInputStream("user.dat");
        ObjectInputStream objInput1 = new ObjectInputStream(fin);
        vList = (Vector) objInput1.readObject();
        boolean bHave = false; //�����жϱ�־�û��Ƿ����
        for (int i = 0; i < vList.size(); i++) {
          NewUser reg = (NewUser) vList.elementAt(i);
          if (reg.name.equals(u1.name)) {
            bHave = true;
            if (!reg.password.equals(u1.password)) {
              toClient.println("�û��������");
              break;
            }
            else {//�ж��Ƿ��Ѿ���¼              
              boolean bHaveLogin = false;
              for (int a = 0; a < userOnline.size(); a++) {
                if (u1.name.equals(userOnline.elementAt(a))) {
                  bHaveLogin = true;
                  break;
                }
              }
              if (!bHaveLogin) {
                userOnline.addElement(u1.name);
                toClient.println("��¼�ɹ�");
                taLog.append("�û�" + u1.name + "��¼�ɹ���" +
                             "��¼ʱ��:" + (new Date()).toLocaleString() + "\n");
                listUser.setListData(userOnline);
                tfUserOnLine.setText(userOnline.size() + "��");
                break;
              }
              else 
                toClient.println("���û��ѵ�¼");
            }
          }
        }
        if (!bHave) 
          toClient.println("�û��������ڣ�����ע��");
        fin.close();
        objInput1.close();
        fromClient.close();
      }
      catch (ClassNotFoundException e) {
        fail(e.toString());
      }
      catch (IOException e) {
        fail("�����ļ�user.dat����");
      }
    }    
    public void doRegiste() {//ע��
      try {
        boolean flag = true; 
        NewUser new1 = (NewUser) obj;
        File fileU = new File("user.dat");
        if (fileU.length() != 0) { //�ж��Ƿ��ǵ�һ��ע���û�
          ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileU));
          vList = (Vector) in.readObject();
          //�ж��Ƿ�������
          for (int i = 0; i < vList.size(); i++) {
            NewUser reg = (NewUser) vList.elementAt(i);
            if (reg.name.equals(new1.name)) {
              toClient.println("�û����Ѿ�����,��ʹ�������û���");
              flag = false;
              break;
            }
          }
        }
        if (flag) {          
          vList.addElement(new1);//ע�����û�
          FileOutputStream fos = new FileOutputStream(fileU);
          ObjectOutputStream objout = new ObjectOutputStream(fos);
          objout.writeObject(vList);//���浽�ļ�
          //����ע��ɹ���Ϣ
          toClient.println(new1.name + "ע��ɹ�");
          taLog.append("�û�" + new1.name + "ע��ɹ�, " +
                       "ע��ʱ��:" + (new Date()).toLocaleString() + "\n");
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

    //������Ϣ
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
      //�����յ��Ķ���ֵ����������Ϣ�����л�����
      Chat cObj = new Chat();
      cObj = (Chat) obj;
      //��������Ϣ�����л�������ӵ�����������Ϣ��ʸ����
      userChat.addElement( (Chat) cObj);
      return;
    }

    //�˳�
    public void doExit() {
      ExitUser exit1 = new ExitUser();
      exit1 = (ExitUser) obj;
      userOnline.removeElement(exit1.name); 
      listUser.setListData(userOnline);
      tfUserOnLine.setText(userOnline.size() + "��");
      taLog.append("�û�" + exit1.name + "�Ѿ��˳�, " +
                   "�˳�ʱ��:" + (new Date()).toLocaleString() + "\n");
    }
  }
}