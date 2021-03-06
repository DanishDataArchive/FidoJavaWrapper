package dk.dda.fido.conf.handler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import dk.dda.fido.pojos.FidoConfig;

public class FidoJavaWrapperConfHandler extends DefaultHandler {

	private boolean isInFidoJavaWrapper = false;
	private boolean isInConf = false;
	private boolean isInFido = false;
	private boolean isInPath = false;
	private boolean isInPythonPath = false;

	private FidoConfig conf = new FidoConfig();

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		String tag = (localName == null || localName.length() == 0 ? qName : localName);

		if(tag.equalsIgnoreCase("FidoJavaWrapper"))
			isInFidoJavaWrapper = true;
		else if(tag.equalsIgnoreCase("conf"))
			isInConf = true;
		else if(tag.equalsIgnoreCase("Fido"))
			isInFido = true;
		else if(tag.equalsIgnoreCase("path"))
			isInPath = true;
		else if(tag.equalsIgnoreCase("PythonPath"))
			isInPythonPath = true;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if(isInFidoJavaWrapper) {
			if(isInConf) {
				if(isInFido) {
					if(isInPath) {
						conf.setPath(new String(ch, start, length));

						if(conf.getPath().contains("~"))
							conf.setPath(conf.getPath().replace("~", System.getProperty("user.home")));

					} else if(isInPythonPath) {
						conf.setPythonPath(new String(ch, start, length));

						if(conf.getPythonPath().contains("~"))
							conf.setPythonPath(conf.getPythonPath().replace("~", System.getProperty("user.home")));
					}
				}
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		String tag = (localName == null || localName.length() == 0 ? qName : localName);

		if(tag.equalsIgnoreCase("FidoJavaWrapper"))
			isInFidoJavaWrapper = false;
		else if(tag.equalsIgnoreCase("conf"))
			isInConf = false;
		else if(tag.equalsIgnoreCase("Fido"))
			isInFido = false;
		else if(tag.equalsIgnoreCase("path"))
			isInPath = false;
		else if(tag.equalsIgnoreCase("PythonPath"))
			isInPythonPath = false;
	}
	
	public FidoConfig getFidoConfig() {
		return conf;
	}
}
