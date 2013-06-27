package dk.dda.fido.conf.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import dk.dda.fido.conf.handler.FidoJavaWrapperConfHandler;
import dk.dda.fido.pojos.FidoConfig;

public class FidoJavaWrapperConfParser {

	private static FidoJavaWrapperConfParser instance = null;

	private SAXParser parser;
	private FidoJavaWrapperConfHandler handler;

	public FidoJavaWrapperConfParser() throws ParserConfigurationException, SAXException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		parser = factory.newSAXParser();
		handler = new FidoJavaWrapperConfHandler();
	}

	public static FidoJavaWrapperConfParser getInstance() throws ParserConfigurationException, SAXException {
		if(instance == null)
			instance = new FidoJavaWrapperConfParser();

		return instance;
	}

	public FidoConfig parse(String conf) throws SAXException, IOException {
		File xml = new File(conf);

		if(!xml.exists())
			throw new FileNotFoundException(conf + " not found");

		parser.parse(xml, handler);

		return handler.getFidoConfig();
	}
}
