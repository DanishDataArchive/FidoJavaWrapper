package dk.dda.fido;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dk.dda.fido.callbacks.IFidoResultCallback;
import dk.dda.fido.enums.Identifier;
import dk.dda.fido.pojos.FidoResult;

public class FidoWorkThread extends Thread implements Runnable {

	private String fido;
	private IFidoResultCallback parent;

	private String path;
	private String[] options;

	private HashMap<String, List<FidoResult>> recognizedFiles;
	private ArrayList<String> unRecognizedFiles;

	public FidoWorkThread(String fido, IFidoResultCallback parent, String[] options, String path) {
		this.fido = fido;
		this.parent = parent;
		this.options = options;
		this.path = path;
		recognizedFiles = new HashMap<String, List<FidoResult>>();
		unRecognizedFiles = new ArrayList<String>();
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
				Process p = Runtime.getRuntime().exec(fido + " " + args);
				p.waitFor();

				BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

				String line = null;

				while((line = br.readLine()) != null) {
					if(line.startsWith("OK")) {
						String[] resultSplit = line.split(",");
						Identifier ident = Identifier.UNKNOWN;

						if(resultSplit[resultSplit.length-1].replace("\"", "").equals("signature"))
							ident = Identifier.SIGNATURE;
						else if(resultSplit[resultSplit.length-1].replace("\"", "").equals("extension"))
							ident = Identifier.EXTENSION;
						else if(resultSplit[resultSplit.length-1].replace("\"", "").equals("container"))
							ident = Identifier.CONTAINER;

						FidoResult result = new FidoResult(resultSplit[6].replace("\"", ""), resultSplit[2].replace("\"", ""), resultSplit[3].replace("\"", ""), resultSplit[4].replace("\"", ""), resultSplit[7].replace("\"", ""), ident);

						if(recognizedFiles.containsKey(result.getFile()))
							recognizedFiles.get(result.getFile()).add(result);
						else {
							ArrayList<FidoResult> list = new ArrayList<FidoResult>();
							list.add(result);
							recognizedFiles.put(result.getFile(), list);
						}
					}
					else if(line.startsWith("KO"))
						unRecognizedFiles.add(line);
				}

				p.destroy();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		parent.fidoResults(recognizedFiles, unRecognizedFiles.toArray(new String[unRecognizedFiles.size()]), path);
	}
}
