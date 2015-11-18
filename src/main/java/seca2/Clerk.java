package seca2;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Scanner;

public class Clerk implements Runnable {

	protected Socket socket;
	public static final long CLERK_DELAY_MS=50;
	public static final Charset encoding = Charset.forName("UTF-8");
	public static final String EndOfLine="\n";

	public Clerk(Socket s) {
		socket=s;
	}

	protected void runEcho()
	{
		Scanner socketScanner = null;
		String newLine;
		try {
			socketScanner=new Scanner(socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(true)
		{
			try {
				Thread.sleep(CLERK_DELAY_MS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(socket.isClosed())
				return ;
			if(socketScanner.hasNextLine())
			{
				newLine=socketScanner.nextLine()+EndOfLine;
				System.out.println("echoing: "+newLine);
				try {
					socket.getOutputStream().write(newLine.getBytes(encoding));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void run() {
		// TODO Auto-generated method stub
		System.out.println("new client connected");
		runEcho();
	}

}