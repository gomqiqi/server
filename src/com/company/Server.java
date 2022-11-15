
package com.company;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class Server
{

    public static ArrayList<Socket> list = new ArrayList();         //存所有的socket
    public static ArrayList<String>user_name=new ArrayList();       //存用户名
    public static void main(String[] args)
    {
        new Serverthread();
        new Control();
    }
}


class Serverthread extends  Thread
{
    ServerSocket serverSocket=null;
    Socket socket;
    public Serverthread()
    {
        try {
            serverSocket=new ServerSocket(8888);
            new Thread(this).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public  void run()
    {
        String address=null;
        int port=0;
        int samename=0;
        while(true)
        {
            samename=0;
            try {
                socket=serverSocket.accept();
                Server.list.add(socket);
                address=socket.getInetAddress().getHostAddress();
                port=socket.getPort();
                InputStream is = null;
                BufferedReader br = null;
                is=socket.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                String name=br.readLine();
                for(String s:Server.user_name)
                {
                    if(s.equals(name))
                        samename=1;
                }
                if(samename==0)
                {
                    Date date=new Date();
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                    String dateStr = format.format(date);
                    Server.user_name.add(name);
                    for(int i=0;i<Server.list.size();i++)
                    {
                        Date str_date=new Date();
                        OutputStream os=Server.list.get(i).getOutputStream();
                        BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(os));
                        bw.write(dateStr+" 用户："+name+"  登录");
                        bw.newLine();
                        bw.flush();
                    }
                    System.out.println("用户"+address+":"+name+"上线了！");
                    new sendThread(socket,address,port,name).start();

                }
                else
                {
                    OutputStream os=socket.getOutputStream();
                    BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(os));

                    bw.write("[重复]");
                    bw.newLine();
                    bw.flush();
                    Server.list.remove(socket);
                }
//                String str = br.readLine();
//                System.out.println("名字"+str);
//                System.out.println(str);
//                OutputStream os=null;
//                BufferedWriter bw=null;
//
//                for(int i=0;i<Server.list.size();i++) {
//                    os = Server.list.get(i).getOutputStream();
//                    bw = new BufferedWriter(new OutputStreamWriter(os));
//                    bw.write(str);
//                    bw.newLine();
//                    bw.flush();
//                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
 }


}






class sendThread extends Thread {
    private Socket socket;
    String address=null;
    String name=null;
    int port;
    public sendThread(Socket socket,String address,int port,String name) {
        super();
        this.socket = socket;
        this.address=address;
        this.name=name;
        this.port=port;
    }

    @Override
    public void run() {
        InputStream is = null;
        BufferedReader br = null;
        String str = null;
        OutputStream os = null;
        BufferedWriter bw = null;
        int flag=1;
        while (true) {
            try {
                is = socket.getInputStream();

                br = new BufferedReader(new InputStreamReader(is));
                str = br.readLine();

                for(int i=0;i<Server.list.size();i++)
                {
                    os = Server.list.get(i).getOutputStream();

                    if(str.length()>=4) {
                        if (str.charAt(0) == '[' && str.charAt(1) == '退' && str.charAt(2) == '出' && str.charAt(3) == ']'&&flag==1)
                        {
                            Server.list.remove(socket);
                            Server.user_name.remove(name);
;
                            i--;
                            flag=0;
                        }
                    }


//                    String nameStr="";
//                    String fakestr="";
//                    int namenum=0;
//                    int signal=0;
//                    if(str.length()>=3)
//                    {
//
//                        for(int k=0;k<3;k++)
//                        nameStr+=str.charAt(k);
//
//                        if(nameStr.equals("用户名"))
//                        {
//                            nameStr="";
//                            for(int w=3;w<str.length();w++)
//                            {
//                                if(str.charAt(w)=='%')
//                                {
//                                    signal=1;
//                                    continue;
//                                }
//                                if(signal==0)
//                                {
//                                    nameStr+=str.charAt(w);
//
//                                }else
//                                {
//                                    fakestr+=str.charAt(w);
//                                }
//                            }
//                            str=fakestr;
//                            int samename=0;
//                            for(int k=0;k<Server.user_name.size();k++)
//                            {
//
//                                if(Server.user_name.get(k).equals(nameStr))
//                                    samename=1;
//                            }
//                            if (samename==0) {
//                                Server.user_name.add(nameStr);
//
//                            }
//                                else
//                            {
//                                str="有相同名字！！";
//                                flag=0;
//                                Server.list.remove(socket);
//                                i--;
//                            }
//                        }
//                    }




                    bw = new BufferedWriter(new OutputStreamWriter(os));
                    bw.write(str);
                    bw.newLine();
                    bw.flush();


                }

            } catch (IOException e) {
                // 如果断开连接则移除对于的socket
                Server.list.remove(socket);
            }
            if(flag==0)
                break;
        }
    }
}


class Control extends  Thread
{
    String In;
    public  Control()
    {


        new Thread(this).start();
    }
    @Override
    public void run()
    {
        while(true) {
            System.out.println("end--结束聊天室，count--输出用户个数，chaters--输出请所有用户名，kickout+空格+用户名--踢出用户，请输入命令：");
            Scanner in=new Scanner(System.in);
            In=in.nextLine();
            if(In.equals("end"))
            {
                for(int i=0;i<Server.list.size();i++)
                {

                    try {
                        OutputStream os = Server.list.get(i).getOutputStream();
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                        bw.write("聊天结束，散了散了");
                        bw.newLine();
                        bw.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                System.exit(1);
            }else if(In.equals("count"))
            {
                System.out.println("聊天者数量"+Server.list.size());
            }else if(In.equals("chaters"))
            {
                System.out.println("聊天者名字："+Server.user_name);
            }else if(In.substring(0,7).equals("kickout"))
            {
                String name=In.substring(8);
//                int i=0;
//                for(i=0;i<name.charAt(i);i++)
//                {
//                    if(name.charAt(i)=='-')
//                    {
//                        break;
//                    }
//                }
//                name=name.substring(0,i);
                int j=0;
                int signal=0;
                for( j=0;j<Server.user_name.size();j++)
                {
                    if(Server.user_name.get(j).equals(name))
                    {
                        System.out.println(name+"被踢出"+j);
                        signal=1;
                        break;
                    }
                }
                if(signal==1) {
                    try {
                        OutputStream os = Server.list.get(j).getOutputStream();
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                        bw.write("管理员踢出!");
                        bw.newLine();
                        bw.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else
                {
                    System.out.println("抱歉，不存在该用户");
                }
            }


        }
        }

}

