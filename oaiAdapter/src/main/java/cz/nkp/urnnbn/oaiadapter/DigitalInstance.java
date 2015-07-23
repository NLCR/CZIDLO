/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

import java.util.logging.Logger;

import cz.nkp.urnnbn.xml.unmarshallers.DigitalInstanceUnmarshaller;
import cz.nkp.urnnbn.xml.unmarshallers.validation.LimitedLengthEnhancer;

/**
 *
 * @author hanis
 */
public class DigitalInstance {

	private static final Logger logger = Logger.getLogger(DigitalInstance.class.getName());

	private String id;
	private String digitalLibraryId;
	private String url;
	private String format;
	private String accessibility;

	public DigitalInstance() {
		this.id = "";
		this.format = "";
		this.accessibility = "";
	}

	public String getDigitalLibraryId() {
		return digitalLibraryId;
	}

	public void setDigitalLibraryId(String digitalLibraryId) {
		this.digitalLibraryId = digitalLibraryId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = new LimitedLengthEnhancer(DigitalInstanceUnmarshaller.ACCESSIBILITY_MAX_LENGTH).toEnhancedValueOrNull(format);
	}

	public String getAccessibility() {
		return accessibility;
	}

	public void setAccessibility(String accessibility) {
		this.accessibility = new LimitedLengthEnhancer(DigitalInstanceUnmarshaller.ACCESSIBILITY_MAX_LENGTH)
				.toEnhancedValueOrNull(accessibility);
	}

	public boolean isChanged(DigitalInstance di) {
		return !(this.url.equals(di.getUrl()) && this.format.equals(di.getFormat()) && this.accessibility.equals(di.getAccessibility()));
	}

	public String getDiff(DigitalInstance oldDi) {
		boolean urlsSame = url.equals(oldDi.getUrl());
		boolean formatSame = format.equals(oldDi.getFormat());
		boolean accessibilitySame = accessibility.equals(oldDi.getAccessibility());
		StringBuilder builder = new StringBuilder();
		if (!urlsSame) {
			builder.append(String.format("current url: '%s', new url: '%s'; ", oldDi.getUrl(), url));
		}
		if (!formatSame) {
			builder.append(String.format("current format: '%s', new format: '%s'; ", oldDi.getFormat(), format));
		}
		if (!accessibilitySame) {
			builder.append(String.format("current accessibility: '%s', new accessibility: '%s'; ", oldDi.getAccessibility(), accessibility));
		}
		return builder.toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ID: " + id + ", Library ID: " + digitalLibraryId + ", url: " + url + ", format: " + format + "accessibility: "
				+ accessibility;
	}

}
