package seca2;

import java.util.List;

public class CoreJson {
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public List<DepositJson> getDeposits() {
		return deposits;
	}
	public void setDeposits(List<DepositJson> deposits) {
		this.deposits = deposits;
	}
	public String getOutLog() {
		return outLog;
	}
	public void setOutLog(String outLog) {
		this.outLog = outLog;
	}
	public Integer port;
	public List<DepositJson> deposits;
	public String outLog;

	public static class DepositJson
	{
		public String getCustomer() {
			return customer;
		}
		public void setCustomer(String customer) {
			this.customer = customer;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getInitialBalance() {
			return initialBalance;
		}
		public void setInitialBalance(String initialBalance) {
			this.initialBalance = initialBalance;
		}
		public String getUpperBound() {
			return upperBound;
		}
		public void setUpperBound(String upperBound) {
			this.upperBound = upperBound;
		}
		public String customer;
		public String id;
		public String initialBalance;
		public String upperBound;
	}
}
