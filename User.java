import java.io.*;
import java.util.*;
//��¼ʱ���û���Ϣ
class User implements Serializable {
  String name;
  String password;
}

//ע���û���Ϣ
class NewUser implements Serializable {
  String name;
  String password;
  String sex;
  String email;
}

//���ڷ�������������û�����Ϣ
class Info implements Serializable {
  Vector userOnLine;
  Vector chat;
}

//������Ϣ���л�
class Chat implements Serializable {
  String fromUser;
  String msg;
  String toUser;
  boolean whisper;
}

//�˳��û���Ϣ���л�
class ExitUser implements Serializable {
  String name;
}