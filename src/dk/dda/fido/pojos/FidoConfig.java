package dk.dda.fido.pojos;

/**
 * The FIDO Configuration class
 * @author Martin Jensby
 *
 */
public class FidoConfig {

	private String path = null;
	private String pythonPath = null;

	/**
	 * Get the path to FIDO
	 * @return The FIDO path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Set the path to FIDO
	 * @param path The new FIDO path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Get the path to the python interpreter
	 * @return The python interpreter path
	 */
	public String getPythonPath() {
		return pythonPath;
	}

	/**
	 * Set the python interpreter path
	 * @param pythonPath The new python interpreter path
	 */
	public void setPythonPath(String pythonPath) {
		this.pythonPath = pythonPath;
	}

}
