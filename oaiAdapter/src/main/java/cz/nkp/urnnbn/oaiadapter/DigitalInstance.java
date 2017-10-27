/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

import java.util.logging.Logger;

import cz.nkp.urnnbn.xml.apiv3.unmarshallers.DigitalInstanceUnmarshaller;
import cz.nkp.urnnbn.xml.apiv3.unmarshallers.validation.LimitedLengthEnhancer;

/**
 * @author hanis
 */
public class DigitalInstance {

    private static final Logger logger = Logger.getLogger(DigitalInstance.class.getName());

    private String id;
    private Long digitalLibraryId;
    private String url;
    private String format;
    private String accessibility;

    public DigitalInstance() {
        this.id = "";
        this.format = "";
        this.accessibility = "";
    }

    public Long getDigitalLibraryId() {
        return digitalLibraryId;
    }

    public void setDigitalLibraryId(Long digitalLibraryId) {
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
        this.accessibility = new LimitedLengthEnhancer(DigitalInstanceUnmarshaller.ACCESSIBILITY_MAX_LENGTH).toEnhancedValueOrNull(accessibility);
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
        StringBuilder builder = new StringBuilder();
        builder.append("DigitalInstance{");
        boolean someFieldAlreadyPresent = false;
        if (id != null && !id.isEmpty()) {
            if (someFieldAlreadyPresent) {
                builder.append(", ");
            }
            builder.append("id='").append(id).append('\'');
            someFieldAlreadyPresent = true;
        }
        if (digitalLibraryId != null) {
            if (someFieldAlreadyPresent) {
                builder.append(", ");
            }
            builder.append("digitalLibraryId='").append(digitalLibraryId).append('\'');
            someFieldAlreadyPresent = true;
        }
        if (url != null && !url.isEmpty()) {
            if (someFieldAlreadyPresent) {
                builder.append(", ");
            }
            builder.append("url='").append(url).append('\'');
            someFieldAlreadyPresent = true;
        }
        if (format != null && !format.isEmpty()) {
            if (someFieldAlreadyPresent) {
                builder.append(", ");
            }
            builder.append("format='").append(format).append('\'');
            someFieldAlreadyPresent = true;
        }
        if (accessibility != null && !accessibility.isEmpty()) {
            if (someFieldAlreadyPresent) {
                builder.append(", ");
            }
            builder.append("accessibility='").append(accessibility).append('\'');
            someFieldAlreadyPresent = true;
        }
        builder.append('}');
        return builder.toString();
    }

    public DigitalInstance withMergedAccessibilityAndFormat(DigitalInstance oldDi) {
        DigitalInstance merged = new DigitalInstance();
        merged.setDigitalLibraryId(digitalLibraryId);
        merged.setUrl(url);
        merged.setAccessibility(accessibility != null && !accessibility.isEmpty() ? accessibility : oldDi.getAccessibility());
        merged.setFormat(format != null && !format.isEmpty() ? format : oldDi.getFormat());
        return merged;
    }

    public cz.nkp.urnnbn.core.dto.DigitalInstance toCoreDigitalInstance() {
        cz.nkp.urnnbn.core.dto.DigitalInstance result = new cz.nkp.urnnbn.core.dto.DigitalInstance();
        result.setAccessibility(accessibility);
        result.setFormat(format);
        result.setLibraryId(digitalLibraryId);
        result.setUrl(url);
        return result;
    }
}
