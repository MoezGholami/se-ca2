package seca2;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;


@XmlRootElement(name="responses")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResponseXML
{
	public ResponseXML()
	{
		responseList=new ArrayList<ResponseXML.Response>();
	}
	@XmlAttribute
	public String terminalId;
	@XmlElement(name="response")
	public List<Response> responseList;

	public static class Response
	{
		public static String RESULT_SUCCESS="success";
		public static String RESULT_FAILURE="faiulre";


		@XmlAttribute
		public String id;
		@XmlAttribute
		public String depositId;
		@XmlAttribute
		public String result;
		@XmlAttribute
		public String error;

		@Override
		public String toString()
		{
			return "transaction id: "+id+" for deposit: "+depositId+" result: "+result+" errors: "+error;
		}
	}
}
