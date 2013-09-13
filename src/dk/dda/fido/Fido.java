package dk.dda.fido;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dk.dda.fido.callbacks.IFidoResultCallback;
import dk.dda.fido.conf.parser.FidoJavaWrapperConfParser;
import dk.dda.fido.pojos.FidoConfig;
import dk.dda.fido.pojos.FidoResult;

/**
 * The FIDO Wrapper
 * @author Martin Jensby
 *
 */
public class Fido implements IFidoResultCallback {

	private FidoConfig fidoConf;
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
	
	/**
	 * Default constructor
	 * @throws Exception
	 */
	public Fido() throws Exception {
		listenersToCall = new ArrayList<IFidoResultCallback>();
		fidoConf = FidoJavaWrapperConfParser.getInstance().parse("./resources/FidoJavaWrapper.conf.xml");

		if(System.getProperty("FIDO") != null) {
			fidoConf.setPath(System.getProperty("FIDO"));
		}

		if(fidoConf.getPath() == null || fidoConf.getPath().length() == 0)
			throw new Exception("FIDO path not supplied");

		if(!new File(fidoConf.getPath()).exists())
			throw new Exception(fidoConf.getPath() + " doesn't exists");

		if(fidoConf.getPythonPath() != null && fidoConf.getPythonPath().length() > 0) {
			if(!new File(fidoConf.getPythonPath()).exists())
				throw new Exception(fidoConf.getPythonPath() + " doesn't exists");
		}

		Process p = Runtime.getRuntime().exec((fidoConf.getPythonPath() != null ? fidoConf.getPythonPath() + " " : "") + fidoConf.getPath() + " -v");
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
	
	/**
	 * Get the FIDO version
	 * @return FIDO Version
	 */
	public String getVersion() {
		return fidoVersion;
	}

	/**
	 * Get the signature files used by FIDO
	 * @return List of signature files
	 */
	public String[] getSigFiles() {
		return sigFiles;
	}

	/**
	 * Set the path to inspect
	 * @param path The path to inspect
	 */
	public void setPathToInspect(String path) {
		this.pathToInspect = path;
	}
	
	/**
	 * Alternative constructor
	 * @param recurse recurse into subdirectories
	 */
	public Fido(boolean recurse) {
		this.recurse = recurse;
	}
	
	/**
	 * Alternative constructor
	 * @param recurse recurse into subdirectories
	 * @param zip recurse into zip and tar files
	 */
	public Fido(boolean recurse, boolean zip) {
		this.recurse = recurse;
		this.zip = zip;
	}

	/**
	 * Alternative constructor
	 * @param recurse recurse into subdirectories
	 * @param zip recurse into zip and tar files
	 * @param nocontainer disable deep scan of container documents
	 */
	public Fido(boolean recurse, boolean zip, boolean nocontainer) {
		this.recurse = recurse;
		this.zip = zip;
		this.nocontainer = nocontainer;
	}

	/**
	 * Alternative constructor
	 * @param recurse recurse into subdirectories
	 * @param zip recurse into zip and tar files
	 * @param nocontainer disable deep scan of container documents
	 * @param bufsize size (in bytes) of the buffer to match against
	 */
	public Fido(boolean recurse, boolean zip, boolean nocontainer, int bufsize) {
		this.recurse = recurse;
		this.zip = zip;
		this.nocontainer = nocontainer;
		this.bufsize = bufsize;
	}

	/**
	 * Alternative constructor
	 * @param recurse recurse into subdirectories
	 * @param zip recurse into zip and tar files
	 * @param nocontainer disable deep scan of container documents
	 * @param bufsize size (in bytes) of the buffer to match against
	 * @param container_bufsize size (in bytes) of the buffer to match against
	 */
	public Fido(boolean recurse, boolean zip, boolean nocontainer, int bufsize, int container_bufsize) {
		this.recurse = recurse;
		this.zip = zip;
		this.nocontainer = nocontainer;
		this.bufsize = bufsize;
		this.container_bufsize = container_bufsize;
	}

	/**
	 * Alternative constructor
	 * @param recurse recurse into subdirectories
	 * @param zip recurse into zip and tar files
	 * @param nocontainer disable deep scan of container documents
	 * @param bufsize size (in bytes) of the buffer to match against
	 * @param container_bufsize size (in bytes) of the buffer to match against
	 * @param nouseformats comma separated string of formats not to use
	 */
	public Fido(boolean recurse, boolean zip, boolean nocontainer, int bufsize, int container_bufsize, String nouseformats) {
		this.recurse = recurse;
		this.zip = zip;
		this.nocontainer = nocontainer;
		this.bufsize = bufsize;
		this.container_bufsize = container_bufsize;
		this.nouseformats = nouseformats;
	}

	/**
	 * Alternative constructor
	 * @param recurse recurse into subdirectories
	 * @param zip recurse into zip and tar files
	 * @param nocontainer disable deep scan of container documents
	 * @param bufsize size (in bytes) of the buffer to match against
	 * @param container_bufsize size (in bytes) of the buffer to match against
	 * @param nouseformats comma separated string of formats not to use
	 * @param useformats comma separated string of formats to use
	 */
	public Fido(boolean recurse, boolean zip, boolean nocontainer, int bufsize, int container_bufsize, String nouseformats, String useformats) {
		this.recurse = recurse;
		this.zip = zip;
		this.nocontainer = nocontainer;
		this.bufsize = bufsize;
		this.container_bufsize = container_bufsize;
		this.nouseformats = nouseformats;
		this.useformats = useformats;
	}

	/**
	 * Enable/disable recursiveness
	 * @param recurse
	 */
	public void setRecurse(boolean recurse) {
		this.recurse = recurse;
	}
	
	/**
	 * Enable/disable recursiveness into zip and tar files
	 * @param zip
	 */
	public void setZip(boolean zip) {
		this.zip = zip;
	}

	/**
	 * Enable/disable deep scan of container documents
	 * @param nocontainer
	 */
	public void setNocontainer(boolean nocontainer) {
		this.nocontainer = nocontainer;
	}

	/**
	 * size of the buffer to match against
	 * @param bufsize the new buffer size
	 */
	public void setBufsize(int bufsize) {
		this.bufsize = bufsize;
	}

	/**
	 * size of the buffer to match against
	 * @param container_bufsize the new buffer size
	 */
	public void setContainer_bufsize(int container_bufsize) {
		this.container_bufsize = container_bufsize;
	}

	/**
	 * Set which formats not to use
	 * @param nouseformats comma separated string of formats not to use
	 */
	public void setNouseformats(String nouseformats) {
		this.nouseformats = nouseformats;
	}

	/**
	 * Set which formats to use
	 * @param useformats comma separated string of formats to use
	 */
	public void setUseformats(String useformats) {
		this.useformats = useformats;
	}

	/**
	 * Inspect the files
	 */
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
		
		this.thread = new FidoWorkThread(fidoConf, this, optionsArray, pathToInspect);
		this.thread.start();
	}
	
	/**
	 * Wait for the process to end before continuing
	 * @throws InterruptedException
	 */
	public void join() throws InterruptedException {
		thread.join();
	}

	/**
	 * Add a callback to the list
	 * @param callback The callback
	 */
	public synchronized void addCallback(IFidoResultCallback callback) {
		synchronized (listenersToCall) {
			if(!listenersToCall.contains(callback))
				listenersToCall.add(callback);
		}
	}

	/**
	 * Remove a callback from the list
	 * @param callback The callback
	 */
	public synchronized void removeCallback(IFidoResultCallback callback) {
		synchronized (listenersToCall) {
			if(listenersToCall.contains(callback))
				listenersToCall.remove(callback);
		}
	}

	@Override
	public void fidoResults(HashMap<String, List<FidoResult>> recognizedFiles, HashMap<String, List<FidoResult>> unRecognizedFiles, String path) {
		for(IFidoResultCallback callback : listenersToCall)
			callback.fidoResults(recognizedFiles, unRecognizedFiles, path);
	}
}
