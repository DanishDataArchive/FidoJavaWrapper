package dk.dda.fido;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dk.dda.fido.callbacks.IFidoResultCallback;
import dk.dda.fido.conf.parser.FidoJavaWrapperConfParser;
import dk.dda.fido.pojos.FidoResult;

public class Fido implements IFidoResultCallback {

	private String fido;
	private String fidoVersion;
	private String[] sigFiles;
	private FidoWorkThread thread;

	private ArrayList<IFidoResultCallback> listenersToCall;

	private String pathToInspect;
	private boolean recurse = false;
	private boolean zip = false;
	private boolean nocontainer = false;
	private int bufsize = 0;
	private int container_bufsize = 0;
	private String nouseformats = null;
	private String useformats = null;
	
	public Fido() throws Exception {
		listenersToCall = new ArrayList<IFidoResultCallback>();
		if(System.getProperty("FIDO") == null) {
			fido = FidoJavaWrapperConfParser.getInstance().parse("./resources/FidoJavaWrapper.conf.xml").getPath();
		} else {
			fido = System.getProperty("FIDO");
		}

		if(fido == null || fido.length() == 0)
			throw new Exception("Fido path not supplied");

		Process p = Runtime.getRuntime().exec(fido + " -v");
		p.waitFor();

		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

		String line = null;

		while((line = br.readLine()) != null) {
			if(line.startsWith("FIDO")) {
				String[] versionLine = line.split(" ");
				fidoVersion = versionLine[1];

				sigFiles = line.substring(line.indexOf('(') + 1, line.indexOf(')')).replace(" ", "").split(",");
			}
		}
	}
	
	public String getVersion() {
		return fidoVersion;
	}

	public String[] getSigFiles() {
		return sigFiles;
	}

	public void setPathToInspect(String path) {
		this.pathToInspect = path;
	}
	
	public Fido(boolean recurse) {
		this.recurse = recurse;
	}
	
	public Fido(boolean recurse, boolean zip) {
		this.recurse = recurse;
		this.zip = zip;
	}

	public Fido(boolean recurse, boolean zip, boolean nocontainer) {
		this.recurse = recurse;
		this.zip = zip;
		this.nocontainer = nocontainer;
	}

	public Fido(boolean recurse, boolean zip, boolean nocontainer, int bufsize) {
		this.recurse = recurse;
		this.zip = zip;
		this.nocontainer = nocontainer;
		this.bufsize = bufsize;
	}

	public Fido(boolean recurse, boolean zip, boolean nocontainer, int bufsize, int container_bufsize) {
		this.recurse = recurse;
		this.zip = zip;
		this.nocontainer = nocontainer;
		this.bufsize = bufsize;
		this.container_bufsize = container_bufsize;
	}

	public Fido(boolean recurse, boolean zip, boolean nocontainer, int bufsize, int container_bufsize, String nouseformats) {
		this.recurse = recurse;
		this.zip = zip;
		this.nocontainer = nocontainer;
		this.bufsize = bufsize;
		this.container_bufsize = container_bufsize;
		this.nouseformats = nouseformats;
	}

	public Fido(boolean recurse, boolean zip, boolean nocontainer, int bufsize, int container_bufsize, String nouseformats, String useformats) {
		this.recurse = recurse;
		this.zip = zip;
		this.nocontainer = nocontainer;
		this.bufsize = bufsize;
		this.container_bufsize = container_bufsize;
		this.nouseformats = nouseformats;
		this.useformats = useformats;
	}

	public void setRecurse(boolean recurse) {
		this.recurse = recurse;
	}
	
	public void setZip(boolean zip) {
		this.zip = zip;
	}

	public void setNocontainer(boolean nocontainer) {
		this.nocontainer = nocontainer;
	}

	public void setBufsize(int bufsize) {
		this.bufsize = bufsize;
	}

	public void setContainer_bufsize(int container_bufsize) {
		this.container_bufsize = container_bufsize;
	}

	public void setNouseformats(String nouseformats) {
		this.nouseformats = nouseformats;
	}

	public void setUseformats(String useformats) {
		this.useformats = useformats;
	}

	public void inspect() {
		ArrayList<String> options = new ArrayList<String>();

		if(recurse)
			options.add("-recurse");
		
		if(zip)
			options.add("-zip");

		if(nocontainer)
			options.add("-nocontainer");

		if(bufsize != 0)
			options.add("-bufsize " + bufsize);

		if(container_bufsize != 0)
			options.add("-container_bufsize " + container_bufsize);

		if(nouseformats != null)
			options.add("-nouseformats " + nouseformats);

		if(useformats != null)
			options.add("-useformats " + useformats);

		String[] optionsArray = options.toArray(new String[options.size()]);
		
		this.thread = new FidoWorkThread(fido, this, optionsArray, pathToInspect);
		this.thread.start();
	}
	
	public void join() throws InterruptedException {
		thread.join();
	}

	public synchronized void addCallback(IFidoResultCallback callback) {
		synchronized (listenersToCall) {
			if(!listenersToCall.contains(callback))
				listenersToCall.add(callback);
		}
	}

	public synchronized void removeCallback(IFidoResultCallback callback) {
		synchronized (listenersToCall) {
			if(listenersToCall.contains(callback))
				listenersToCall.remove(callback);
		}
	}

	@Override
	public void fidoResults(HashMap<String, List<FidoResult>> recognizedFiles, String[] unRecognizedFiles, String path) {
		for(IFidoResultCallback callback : listenersToCall)
			callback.fidoResults(recognizedFiles, unRecognizedFiles, path);
	}
}
