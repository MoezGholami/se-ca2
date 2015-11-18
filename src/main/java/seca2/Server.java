package seca2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.*;

import com.google.gson.Gson;

public class Server {

	protected static int port;
	protected static Scanner stdinScanner;
	protected static SocketDaemon socketDaemon;
	protected static Thread socketThread;
	protected static String coreFineName="core.json";
	public static CoreJson core;

	public static Logger logger;

	protected static void loadCore()
	{
		String s;
		try {
			Scanner sc=new Scanner(new File(coreFineName));
			s = sc.useDelimiter("\\Z").next();
			Gson gson=new Gson();
			core=(CoreJson)gson.fromJson(s, CoreJson.class);
			sc.close();
		} catch (Exception e) {
			logger.severe("could not load core, exiting...");
			System.exit(1);
		}
	}
	protected static void configureLogger()
	{
		FileHandler fileHandler;
		try {
			fileHandler = new FileHandler(core.outLog, true);
			fileHandler.setFormatter(new SimpleFormatter());
			logger.addHandler(fileHandler);
		} catch (Exception e) {
			logger.log(Level.CONFIG, "could not create log file");
		}
	}
	protected static void configure(String[] args)
	{
		loadCore();
		DepositFacade.instance.initDeposits(core.deposits);
		port=core.port;
		stdinScanner=new Scanner(System.in);
		socketDaemon=new SocketDaemon(port);
		socketThread=new Thread(socketDaemon);
		configureLogger();
	}

	public static void main(String[] args)
	{
		logger=Logger.getAnonymousLogger();
		configure(args);
		String stdinLine="";
		socketThread.start();
		while(true)
		{
			stdinLine=stdinScanner.nextLine();
			processStdCommand(stdinLine);
		}
	}

	protected static void performSync()
	{
		logger.info("sync in progress");
		DepositFacade.instance.syncYourDeposits(core);
		Gson gson = new Gson();
		String newCoreStr;
		newCoreStr=gson.toJson(core);
		try {
			new PrintWriter(coreFineName).write(newCoreStr);
		} catch (FileNotFoundException e) {
			logger.severe("could not save json, sync failed.");
			return ;
		}
		logger.info("successfully performed sync");
	}

	protected static void processStdCommand(String command)
	{
		if(command.equalsIgnoreCase("sync"))
			performSync();
		else
			logger.info("invalid command got: "+command);
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
				logger.severe("could not create server.");
				return ; //abort
			}
			logger.info("server started.");
			while(true)
			{
				try {
					newClientSocket=serverMainSocket.accept();
				} catch (IOException e) {
					logger.severe("could not accept new connection.");
					continue;
				}
				new Thread(new Clerk(newClientSocket)).start();
			}
		}
	}
}