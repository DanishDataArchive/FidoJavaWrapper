package dk.dda.fido;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dk.dda.fido.callbacks.IFidoResultCallback;
import dk.dda.fido.enums.Identifier;
import dk.dda.fido.pojos.FidoConfig;
import dk.dda.fido.pojos.FidoResult;

/**
 * The FIDO work thread
 * @author Martin Jensby
 *
 */
public class FidoWorkThread extends Thread implements Runnable {

	private FidoConfig fidoConf;
	private IFidoResultCallback parent;

	private String path;
	private String[] options;

	private HashMap<String, List<FidoResult>> recognizedFiles;
	private HashMap<String, List<FidoResult>> unRecognizedFiles;

	/**
	 * Constructor
	 * @param fidoConf The FIDO Configuration
	 * @param callback Who to call when process finishes
	 * @param options The options to use
	 * @param path The path to inspect
	 */
	public FidoWorkThread(FidoConfig fidoConf, IFidoResultCallback callback, String[] options, String path) {
		this.fidoConf = fidoConf;
		this.parent = callback;
		this.options = options;
		this.path = path;
		recognizedFiles = new HashMap<String, List<FidoResult>>();
		unRecognizedFiles = new HashMap<String, List<FidoResult>>();
	}

	private FidoResult parseFidoResultLine(String fidoLine) {
		String[] resultSplit = fidoLine.split(",");
		Identifier ident = Identifier.UNKNOWN;

		if(resultSplit[resultSplit.length-1].replace("\"", "").equals("signature"))
			ident = Identifier.SIGNATURE;
		else if(resultSplit[resultSplit.length-1].replace("\"", "").equals("extension"))
			ident = Identifier.EXTENSION;
		else if(resultSplit[resultSplit.length-1].replace("\"", "").equals("container"))
			ident = Identifier.CONTAINER;

		return new FidoResult(resultSplit[6].replace("\"", ""), resultSplit[2].replace("\"", ""), resultSplit[3].replace("\"", ""), resultSplit[4].replace("\"", ""), resultSplit[7].replace("\"", ""), ident);
	}

	@Override
	public void run() {
		String args = "";

		if(options != null) {
			for(String s : options) {
				args += s + " ";
			}
		}

		args += path;

		if(args.length() > 0) {
			try {
				ProcessBuilder pb = new ProcessBuilder((fidoConf.getPythonPath() != null ? fidoConf.getPythonPath() + " " : "") + fidoConf.getPath(), args);
				Process p = pb.start();
				p.waitFor();

				BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

				String line = null;

				while((line = br.readLine()) != null) {
					if(line.startsWith("OK")) {
						FidoResult result = parseFidoResultLine(line);

						if(recognizedFiles.containsKey(result.getFile()))
							recognizedFiles.get(result.getFile()).add(result);
						else {
							ArrayList<FidoResult> list = new ArrayList<FidoResult>();
							list.add(result);
							recognizedFiles.put(result.getFile(), list);
						}
					}
					else if(line.startsWith("KO")) {
						FidoResult result = parseFidoResultLine(line);

						if(unRecognizedFiles.containsKey(result.getFile()))
							unRecognizedFiles.get(result.getFile()).add(result);
						else {
							ArrayList<FidoResult> list = new ArrayList<FidoResult>();
							list.add(result);
							unRecognizedFiles.put(result.getFile(), list);
						}
					}
				}

				p.destroy();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		parent.fidoResults(recognizedFiles, unRecognizedFiles, path);
	}
}
