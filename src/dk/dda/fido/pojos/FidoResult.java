package dk.dda.fido.pojos;

import dk.dda.fido.enums.Identifier;

/**
 * The FIDO Result
 * @author Martin Jensby
 *
 */
public class FidoResult {
	private String file;
	private String puid;
	private String formatName;
	private String signatureName;
	private String mimeType;

	private Identifier identifiedBy;

	/**
	 * Constructor
	 * @param file The file for which the result is valid
	 * @param puid The PUID from the PRONOM
	 * @param formatName The format name
	 * @param signatureName The signature name
	 * @param mimeType The mime type
	 * @param identifiedBy The identification method see {@link dk.dda.fido.enums.Identifier}
	 */
	public FidoResult(String file, String puid, String formatName, String signatureName, String mimeType, Identifier identifiedBy) {
		this.file = file;
		this.puid = puid;
		this.formatName = formatName;
		this.signatureName = signatureName;
		this.mimeType = mimeType;
		this.identifiedBy = identifiedBy;
	}

	/**
	 * Get the file
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * Get the PUID
	 * @return The PUID
	 */
	public String getPuid() {
		return puid;
	}

	/**
	 * Get the identification type
	 * @return The identification type
	 */
	public Identifier getIdentifiedBy() {
		return identifiedBy;
	}

	/**
	 * Get the format name
	 * @return Format name
	 */
	public String getFormatName() {
		return formatName;
	}

	/**
	 * Get the signature name
	 * @return Signature name
	 */
	public String getSignatureName() {
		return signatureName;
	}

	/**
	 * Get the mime type
	 * @return Mime type
	 */
	public String getMimeType() {
		return mimeType;
	}
}
