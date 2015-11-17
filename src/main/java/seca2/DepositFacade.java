package seca2;

import java.math.BigDecimal;

public class DepositFacade {

	public DepositFacade() {
		// TODO Auto-generated constructor stub
	}
	protected class Deposit
	{
		protected String customer;
		protected String id;
		protected BigDecimal upperBound;
		protected BigDecimal initialBalance;
		public synchronized void addToBalance(BigDecimal addition) throws DepositException
		{
			BigDecimal tempResult = initialBalance.add(addition);
			if(tempResult.compareTo(BigDecimal.ZERO)==-1)
				throw new DepositException.NegativeBalance(id);
			if(upperBound.compareTo(tempResult)==-1)
				throw new DepositException.LimitExceedBalance(id);
			initialBalance=tempResult;
		}
	}
}