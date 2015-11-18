package seca2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DepositFacade {

	public static final DepositFacade instance = new DepositFacade();
	protected ArrayList<Deposit> deposits;

	protected DepositFacade() {
		deposits=new ArrayList<DepositFacade.Deposit>();
	}
	public void initDeposits(List<CoreJson.DepositJson> depositDataList)
	{
		CoreJson.DepositJson tempDepData;
		for(int i=0; i<depositDataList.size(); ++i)
		{
			tempDepData=depositDataList.get(i);
			deposits.add(new Deposit(tempDepData.customer, tempDepData.id,
					Clerk.fromComafulStringToBigDecimal(tempDepData.upperBound),
					Clerk.fromComafulStringToBigDecimal(tempDepData.initialBalance)
					));
		}
	}
	public void syncYourDeposits(CoreJson core)
	{
		core.deposits.clear();
		for(int i=0; i<deposits.size(); ++i)
			core.deposits.add(deposits.get(i).toDepositJson());
	}

	protected Deposit findDepositById(String id)
	{
		for(int i=0; i<deposits.size(); ++i)
			if(id.equalsIgnoreCase(deposits.get(i).id))
				return deposits.get(i);
		return null;
	}
	public ResponseXML.Response performTransaction(TerminalXML.Transaction t)
	{
		ResponseXML.Response result=new ResponseXML.Response();
		result.id=t.id;
		result.depositId=t.depositId;
		Deposit subject=findDepositById(t.depositId);
		if(subject==null)
		{
			result.error=new DepositException.DepositNotFound(t.depositId).declaration();
			result.result=ResponseXML.Response.RESULT_FAILURE;
			return result;
		}
		result.result=ResponseXML.Response.RESULT_FAILURE;
		try {
			if(t.type.equalsIgnoreCase(TerminalXML.Transaction.ADD_TYPE))
				subject.addToBalance(Clerk.fromComafulStringToBigDecimal(t.amount));
			else if(t.type.equalsIgnoreCase(TerminalXML.Transaction.SUB_TYPE))
				subject.addToBalance(Clerk.fromComafulStringToBigDecimal(t.amount).negate());
			else
			{
				result.error=new DepositException.UnkownTransactionType(t.depositId, t.id).declaration();
				return result;
			}
		} catch (DepositException e) {
			result.error=e.declaration();
			return result;
		}
		result.error="";
		result.result=ResponseXML.Response.RESULT_SUCCESS;
		return result;
	}
	protected class Deposit
	{
		protected String customer;
		protected String id;
		protected BigDecimal upperBound;
		protected BigDecimal initialBalance;
		public Deposit(String _customer, String _id, BigDecimal upper, BigDecimal balance)
		{
			customer=_customer;
			id=_id;
			upperBound=upper;
			initialBalance=balance;
		}
		public synchronized void addToBalance(BigDecimal addition) throws DepositException
		{
			BigDecimal tempResult = initialBalance.add(addition);
			if(tempResult.compareTo(BigDecimal.ZERO)==-1)
				throw new DepositException.NegativeBalance(id);
			if(upperBound.compareTo(tempResult)==-1)
				throw new DepositException.LimitExceedBalance(id);
			initialBalance=tempResult;
		}
		public CoreJson.DepositJson toDepositJson()
		{
			CoreJson.DepositJson result=new CoreJson.DepositJson();
			result.customer=this.customer;
			result.id=this.id;
			result.initialBalance=Clerk.bigDecimalToComafulString(this.initialBalance);
			result.upperBound=Clerk.bigDecimalToComafulString(this.upperBound);
			return result;
		}
	}
}