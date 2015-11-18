package seca2;

import java.util.List;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="terminal")
@XmlAccessorType(XmlAccessType.FIELD)
public class TerminalXML {
	@XmlAttribute
	public String id;
	@XmlAttribute
	public String type;
	@XmlElement(name="server")
	ServerXML server;
	@XmlElement(name="outLog")
	public OutLog outlog;
	@XmlElement(name="transactions")
	public Transactions transactions;

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ServerXML {
		@XmlAttribute
		public String ip;
		@XmlAttribute
		public int port;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class OutLog
	{
		@XmlAttribute
		public String path;
	}


	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Transaction
	{
		public static String ADD_TYPE="deposit";
		public static String SUB_TYPE="withdraw";
		@XmlAttribute
		public String id;
		@XmlAttribute
		public String type;
		@XmlAttribute
		public String amount;
		@XmlAttribute
		public String depositId;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Transactions
	{
		@XmlElement(name="transaction")
		public List<Transaction> transactionList;
	}
}
