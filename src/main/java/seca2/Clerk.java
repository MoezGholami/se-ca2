package seca2;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.Socket;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.logging.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class Clerk implements Runnable {

	protected Socket socket;
	public static final long CLERK_DELAY_MS=50;
	public static final Charset encoding = Charset.forName("UTF-8");
	public static final String END_OF_LINE="\n";
	public static final int BUFFER_SIZE=1*1024*1024;

	protected byte[] buffer=new byte[BUFFER_SIZE];
	protected String gottenRequestXML=null;
	protected TerminalXML terminalXML=null;
	protected ResponseXML responseXML=new ResponseXML();
	protected String responseString=null;

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
				newLine=socketScanner.nextLine()+END_OF_LINE;
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

	protected void getRequestXML()
	{
		int readCount=0;
		try {
			readCount=socket.getInputStream().read(buffer);
			gottenRequestXML=new String(buffer, 0, readCount);
		} catch (IOException e) {
			Server.logger.log(Level.SEVERE,"could not receive response");
		}
		Server.logger.log(Level.FINEST, "this xml request got: "+gottenRequestXML);
	}

	protected void parseTerminalXML()
	{
		JAXBContext jc;
        Unmarshaller unmarshaller;
        Marshaller marshaller;

		try {
			jc = JAXBContext.newInstance(TerminalXML.class);
			unmarshaller = jc.createUnmarshaller();
			terminalXML = ((TerminalXML) unmarshaller.unmarshal(new StringReader(gottenRequestXML)));
			marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		} catch (JAXBException e) {
			Server.logger.log(Level.SEVERE, "for one connection, could not parse Terminal.xml");
		}
		Server.logger.log(Level.INFO, "for terminal "+terminalXML.id+" successfully parsed request");
	}

	protected void sendResponse()
	{
		try {
			socket.getOutputStream().write(responseString.getBytes(encoding));
		} catch (IOException e) {
			Server.logger.log(Level.SEVERE, "could not send response for terminal: "+terminalXML.id);
		}
		Server.logger.info("for terminal: "+terminalXML.id+"respond sent successfully.");
	}

	public void run()
	{
		Server.logger.log(Level.INFO, "new connection");
		getRequestXML();
		if(gottenRequestXML==null)
			return ;
		parseTerminalXML();
		processTerminalXML();
		sendResponse();
		try {
			socket.close();
		} catch (IOException e) {
			Server.logger.log(Level.SEVERE, "for terminal: "+terminalXML.id+" could not close socket");
		}
	}

	protected void processTerminalXML()
	{
		responseXML.terminalId=terminalXML.id;
		ResponseXML.Response tempResponse;
		for(int i=0; i<terminalXML.transactions.transactionList.size(); ++i)
		{
			tempResponse=DepositFacade.instance.performTransaction(terminalXML.transactions.transactionList.get(i));
			responseXML.responseList.add(tempResponse);
			Server.logger.info("for terminal: "+terminalXML.id+" , transaction: "+tempResponse.toString());
		}
		marshalResponse();
	}

	public void marshalResponse()
	{
		JAXBContext jc;
        Marshaller marshaller;

        try {
        	java.io.StringWriter sw=new StringWriter();
			jc=JAXBContext.newInstance(ResponseXML.class);
			marshaller = jc.createMarshaller();
		    marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
		    marshaller.marshal(responseXML, sw);
		    responseString=sw.toString();
		} catch (JAXBException e) {
			Server.logger.severe("INTERNAL ERROR: could not marshalize responseXML");
			responseString="INTERNAL SERVER ERROR";
		}
	}

	public static BigDecimal fromComafulStringToBigDecimal(String s)
	{
		return new BigDecimal(s.replace(",",""));
	}
	public static String bigDecimalToComafulString(BigDecimal b)
	{
		String pattern = "###,###";
	    DecimalFormat decimalFormat = new DecimalFormat(pattern);
	    String format;
	    format = decimalFormat.format(b.toString());
		return format;
	}
}