package com.adtec.tcp;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import com.adtec.starring.exception.BaseException;

public class readMsgBody {

	private static Document doc;
	private static String szSettDate = "";

	public readMsgBody() {
	}

	private static void init(String file) throws DocumentException,Exception {
		doc = getDocument(file);
	}

	private static Document getDocument(String sfileName) throws DocumentException {
		Document document = null;
		SAXReader reader = new SAXReader();
		document = reader.read(new File(sfileName));
		return document;
	}

	/**
	 *@throws BaseException
	 * @throws ServletException
	 * 
	 * */
	public static String RtMsg(String svcName) throws Exception{
		String file = "";
		init("d:\\tcpServer.xml");
		Element root = doc.getRootElement();
		List rootList = root.elements();
		String msg = "";
		for (Iterator it = rootList.iterator(); it.hasNext();) {
			Element root1 = (Element) it.next();
			String tagName = root1.attributeValue("dec");
			if( svcName.contentEquals(tagName) )
			{
				msg = root1.getStringValue();
				return msg;
			}
		}
		return msg;
	}

	public static void main(String[] s) throws Exception {
		readMsgBody dao = new readMsgBody();
		System.out.println(dao.RtMsg("818888"));
		try {
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
