package seca2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

	protected static int port;
	protected static Scanner stdinScanner;
	protected static SocketDaemon socketDaemon;
	protected static Thread socketThread;

	protected static void configure(String[] args)
	{
		port=1234;
		stdinScanner=new Scanner(System.in);
		socketDaemon=new SocketDaemon(port);
		socketThread=new Thread(socketDaemon);
	}

	public static void main(String[] args)
	{
		configure(args);
		String stdinLine="";
		socketThread.start();
		while(true)
		{
			stdinLine=stdinScanner.nextLine();
			processStdCommand(stdinLine);
		}
	}

	protected static void processStdCommand(String command)
	{
		//TODO: semi-auto generated method.
		System.out.println("some input:)");
	}

	protected static class SocketDaemon implements Runnable
	{
		protected int port;
		public SocketDaemon(int p)
		{
			port = p;
		}

		@SuppressWarnings("resource")
		public void run()
		{
			ServerSocket serverMainSocket=null;
			Socket newClientSocket=null;
			try {
				serverMainSocket=new ServerSocket(port);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ; //abort
			}
			while(true)
			{
				try {
					newClientSocket=serverMainSocket.accept();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
				new Thread(new Clerk(newClientSocket)).start();
			}
		}
	}
}