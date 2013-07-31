package dk.dda.fido.pojos;

import dk.dda.fido.enums.Identifier;

public class FidoResult {
	private String file;
	private String puid;
	private String formatName;
	private String signatureName;
	private String mimeType;

	private Identifier identifiedBy;

	public FidoResult(String file, String puid, String formatName, String signatureName, String mimeType, Identifier identifiedBy) {
		this.file = file;
		this.puid = puid;
		this.formatName = formatName;
		this.signatureName = signatureName;
		this.mimeType = mimeType;
		this.identifiedBy = identifiedBy;
	}

	public String getFile() {
		return file;
	}

	public String getPuid() {
		return puid;
	}

	public Identifier getIdentifiedBy() {
		return identifiedBy;
	}

	public String getFormatName() {
		return formatName;
	}

	public String getSignatureName() {
		return signatureName;
	}

	public String getMimeType() {
		return mimeType;
	}
}
