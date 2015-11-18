package seca2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Terminal {

	public static final Charset encoding = Charset.forName("UTF-8");
	public static final String END_OF_LINE="\n";
	public static final int BUFFER_SIZE=16*1024;

	protected static String serverHostName;
	protected static int serverPort;
	protected static FileInputStream fileIn=null;
	protected static FileOutputStream fileOut=null;
	protected static byte[] buffer=new byte[BUFFER_SIZE];
	protected static Socket socket = null;
	protected static Logger logger = null;


	public static final String inputFileName="Terminal.xml";
	public static final String outputFileName="Response.xml";
	public static String logFileName="";

	protected static void configure(String[] args)
	{
		serverHostName="127.0.0.1";
		serverPort=1234;
		if(args.length>0)
			serverHostName=args[0];
		if(args.length>1)
			serverPort=new Integer(args[1]);
		logger.log(Level.INFO, "successfully configured.");
	}

	protected static void initLogger()
	{
		logger=Logger.getGlobal();
		FileHandler fileHandler;
		try {
			fileHandler = new FileHandler(logFileName, true);
			fileHandler.setFormatter(new SimpleFormatter());
			logger.addHandler(fileHandler);
		} catch (Exception e) {
			logger.log(Level.CONFIG, "could not create log file");
		}
	}

	protected static void initAllConnections()
	{
		try {
			fileIn=new FileInputStream(inputFileName);
		} catch (FileNotFoundException e1) {
			logger.log(Level.SEVERE, "could not find input file");
			System.exit(1);
		}
		try {
			fileOut=new FileOutputStream(outputFileName, true);
		} catch (FileNotFoundException e1) {
			logger.log(Level.SEVERE, "could not open output file");
			System.exit(1);
		}
        try {
            socket = new Socket(serverHostName, serverPort);
        } catch (UnknownHostException e) {
            logger.log(Level.SEVERE, "Unknown host: " + serverHostName);
            System.exit(2);
        } catch (IOException e) {
        	logger.log(Level.SEVERE, "Could not connect to server");
            System.exit(3);
        }
        logger.log(Level.INFO, "all connections established.");
	}

	protected static void communicateWithServer()
	{
		int readCount=0;
		try {
			while((readCount=fileIn.read(buffer))>0)
				socket.getOutputStream().write(buffer, 0, readCount);
		} catch (IOException e) {
			logger.log(Level.SEVERE,"could not send file.");
			return ;
		}
		logger.log(Level.INFO, "request sent to server.");
		try {
			while((readCount=socket.getInputStream().read(buffer))>0)
				fileOut.write(buffer, 0, readCount);
		} catch (IOException e) {
			logger.log(Level.SEVERE,"could not receive response");
		}
		logger.log(Level.INFO, "server's respond received and saved.");
	}
	protected static void closeConnections()
	{
		int closingError=0;
        try {
			fileIn.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "could not close input file");
			closingError=5;
		}
        try {
			fileOut.close();
		} catch (IOException e1) {
			logger.log(Level.SEVERE, "could not close output file");
			closingError=5;
		}
        try {
			socket.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "could not close socket");
			closingError=5;
		}
        if(closingError!=0)
        	System.exit(closingError);
        logger.log(Level.INFO, "all connections closed.");
	}

    public static void main(String[] args)
    {
    	initLogger();
    	configure(args);
    	initAllConnections();
    	communicateWithServer();
    	closeConnections();
    }

	protected static void echoTest()
	{
		String args[]={};
		configure(args);

        Socket socket = null;
        BufferedReader stdinReader = new BufferedReader(new InputStreamReader(System.in));
        Scanner socketScanner = null;
        String command = null, response=null;
        try
        {
        	socket = new Socket(serverHostName, serverPort);
        	socketScanner=new Scanner(socket.getInputStream());
        	while((command=stdinReader.readLine()) != null && !socket.isClosed())
        	{
        		socket.getOutputStream().write((command+END_OF_LINE).getBytes(encoding));
        		response=socketScanner.nextLine();
        		System.out.println(response);
        	}
        	stdinReader.close();
        	socketScanner.close();
        	socket.close();
        } catch (Exception e)
        {
        	e.printStackTrace();
        }
	}
}
