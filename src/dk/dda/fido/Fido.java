package dk.dda.fido;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dk.dda.fido.callbacks.IFidoResultCallback;
import dk.dda.fido.conf.parser.FidoJavaWrapperConfParser;
import dk.dda.fido.pojos.FidoResult;

public class Fido implements IFidoResultCallback {

	private String fido;
	private FidoWorkThread thread;

	private ArrayList<IFidoResultCallback> listenersToCall;

	private String pathToInspect;
	private boolean recurse = false;
	private boolean zip = false;
	
	public Fido() throws Exception {
		listenersToCall = new ArrayList<IFidoResultCallback>();
		if(System.getProperty("FIDO") == null) {
			fido = FidoJavaWrapperConfParser.getInstance().parse("./resources/FidoJavaWrapper.conf.xml").getPath();
		} else {
			fido = System.getProperty("FIDO");
		}

		if(fido == null || fido.length() == 0)
			throw new Exception("Fido path not supplied");
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

	public void setRecurse(boolean recurse) {
		this.recurse = recurse;
	}
	
	public void setZip(boolean zip) {
		this.zip = zip;
	}
	
	public void inspect() {
		ArrayList<String> options = new ArrayList<String>();

		if(recurse)
			options.add("-recurse");
		
		if(zip)
			options.add("-zip");

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
