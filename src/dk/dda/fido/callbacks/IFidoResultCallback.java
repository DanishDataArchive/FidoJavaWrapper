package dk.dda.fido.callbacks;

import java.util.HashMap;
import java.util.List;

import dk.dda.fido.pojos.FidoResult;

/**
 * The callback interface
 * @author Martin Jensby
 *
 */
public interface IFidoResultCallback {
	/**
	 * The method the results are delivered in
	 * @param recognizedFiles A list containing the path with it results
	 * @param unRecognizedFiles A list of files that wasn't recognized
	 * @param path The path that was inspected
	 */
	public void fidoResults(HashMap<String, List<FidoResult>> recognizedFiles, HashMap<String, List<FidoResult>> unRecognizedFiles, String path);
}
