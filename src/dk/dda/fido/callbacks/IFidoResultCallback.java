package dk.dda.fido.callbacks;

import java.util.HashMap;
import java.util.List;

import dk.dda.fido.pojos.FidoResult;

public interface IFidoResultCallback {
	public void fidoResults(HashMap<String, List<FidoResult>> recognizedFiles, String[] unRecognizedFiles, String path);
}
