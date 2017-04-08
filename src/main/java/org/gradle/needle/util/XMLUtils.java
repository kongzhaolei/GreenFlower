package org.gradle.needle.util;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;


public class XMLUtils {
	private static Logger logger = Logger.getLogger(XMLUtils.class
			.getName());

	public XMLUtils() {

	}

	public static void SendXMLFile(String XmlDataFile) {
		//TODO
	}

	// ��XML�ĵ�ת��ΪString
	private static String getStringFromXML(Document doc) {
		return doc.asXML();
	}

	// ��Stringת��ΪXML document
	public static Document string2xmldoc(String str) {
		try {
			return DocumentHelper.parseText(str);
			
		} catch (DocumentException e) {
			e.printStackTrace();
			return null;
		}
	}
}
