package seca2;

public abstract class DepositException extends Exception {

	private static final long serialVersionUID = -749907814905750257L;
	protected String depositId;
	public DepositException(String id)
	{
		depositId=id;
	}
	public abstract String declaration();

	@Override
	public String toString()
	{
		return declaration();
	}

	public static class NegativeBalance extends DepositException
	{
		private static final long serialVersionUID = 5667265053896121611L;
		public NegativeBalance(String id)
		{
			super(id);
		}
		@Override
		public String declaration()
		{
			return "the deposit with id: "+depositId+" cannot have negative balance.";
		}
	}
	public static class LimitExceedBalance extends DepositException
	{
		private static final long serialVersionUID = -4543366999402316015L;
		public LimitExceedBalance(String id){super(id);}
		@Override
		public String declaration()
		{
			return "the deposit with id: "+depositId+" cannot exceed its upper bound.";
		}
	}
	public static class DepositNotFound extends DepositException
	{
		private static final long serialVersionUID = -3853617914429269991L;
		public DepositNotFound(String id){super(id);}
		@Override
		public String declaration() {
			return "no deposit with id: "+depositId+" exist.";
		}
	}
	protected static class UnkownTransactionType extends DepositException
	{
		private static final long serialVersionUID = 8561325184988417212L;
		protected String transactionId;
		public UnkownTransactionType(String did, String tid)
		{
			super(did);
			transactionId=tid;
		}
		@Override
		public String declaration() {
			return "unknown transaction type for deposit: "+depositId+" in transaction: "+transactionId;
		}
		
	}

}