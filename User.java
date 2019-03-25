import java.io.*;
import java.util.*;
//登录时的用户信息
class User implements Serializable {
  String name;
  String password;
}

//注册用户信息
class NewUser implements Serializable {
  String name;
  String password;
  String sex;
  String email;
}

//用于发送聊天和在线用户的信息
class Info implements Serializable {
  Vector userOnLine;
  Vector chat;
}

//聊天信息序列化
class Chat implements Serializable {
  String fromUser;
  String msg;
  String toUser;
  boolean whisper;
}

//退出用户信息序列化
class ExitUser implements Serializable {
  String name;
}