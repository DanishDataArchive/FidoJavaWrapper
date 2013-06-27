package dk.dda.fido.cli;

import java.util.HashMap;
import java.util.List;

import dk.dda.fido.pojos.FidoResult;

public class DDAFidoResultsPrinter {

	public DDAFidoResultsPrinter(HashMap<String, List<FidoResult>> recognizedFiles, String[] unRecognizedFiles, String path) {
		System.out.println("Results for " + path);

		for(List<FidoResult> results : recognizedFiles.values()) {
			System.out.println(results.get(0).getFile());
			for(FidoResult fr : results) {
				System.out.println("\tPuid: " + fr.getPuid() + "\n" +
						"\tFormat name: " + fr.getFormatName() + "\n" +
						"\tSignature name: " + fr.getSignatureName() + "\n" +
						"\tmimeType: " + fr.getMimeType() + "\n" +
						"\tIdentified by: " + fr.getIdentifiedBy() + "\n" +
						"");
			}
		}
	}
}
