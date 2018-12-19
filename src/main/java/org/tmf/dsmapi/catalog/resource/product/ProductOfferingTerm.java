package org.tmf.dsmapi.catalog.resource.product;

import java.io.Serializable;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.tmf.dsmapi.catalog.resource.TimeRange;
import org.tmf.dsmapi.commons.Utilities;

/**
 *
 * @author bahman.barzideh
 *
 * {
 *     "name": "12 Month",
 *     "description": "12 month contract",
 *     "duration": "12",
 *     "validFor": {
 *         "startDateTime": "2013-04-19T16:42:23-04:00",
 *         "endDateTime": "2013-06-19T00:00:00-04:00"
 *     }
 * }
 *
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Embeddable
public class ProductOfferingTerm implements Serializable {
    private final static long serialVersionUID = 1L;

    @Column(name = "OFFERING_TERM_NAME", nullable = true)
    private String name;

    @Column(name = "OFFERING_TERM_DESCRIPTION", nullable = true)
    private String description;

    @Column(name = "TYPE", nullable = true)
    private String type;

    @Column(name = "IS_FULL_CUSTOM", nullable = true)
    private Boolean isFullCustom;

    @Column(name = "EXCLUSIVITY", nullable = true)
    private String exclusivity;

    @Column(name = "REGION", nullable = true)
    private String region;

    @Column(name = "PURPOSE", nullable = true)
    private String purpose;

    @Column(name = "SECTOR", nullable = true)
    private String sector;

    @Column(name = "TRANSFERABILITY", nullable = true)
    private String transferability;

    @Column(name = "OFFERING_TERM_DURATION", nullable = true)
    private String duration;

    @AttributeOverrides({
        @AttributeOverride(name = "startDateTime", column = @Column(name = "OFFERING_TERM_START_DATE_TIME")),
        @AttributeOverride(name = "endDateTime", column = @Column(name = "OFFERING_TERM_END_DATE_TIME"))
    })
    private TimeRange validFor;

    public ProductOfferingTerm() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsFullCustom() {
        return isFullCustom;
    }

    public void setIsFullCustom(Boolean isFullCustom) {
        this.isFullCustom = isFullCustom;
    }

    public String getExclusivity() {
        return exclusivity;
    }

    public void setExclusivity(String exclusivity) {
        this.exclusivity = exclusivity;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getTransferability() {
        return transferability;
    }

    public void setTransferability(String transferability) {
        this.transferability = transferability;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public TimeRange getValidFor() {
        return validFor;
    }

    public void setValidFor(TimeRange validFor) {
        this.validFor = validFor;
    }

    @Override
    public int hashCode() {
        int hash = 3;

        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 53 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 53 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 53 * hash + (this.isFullCustom != null ? this.isFullCustom.hashCode() : 0);
        hash = 53 * hash + (this.exclusivity != null ? this.exclusivity.hashCode() : 0);
        hash = 53 * hash + (this.region != null ? this.region.hashCode() : 0);
        hash = 53 * hash + (this.purpose != null ? this.purpose.hashCode() : 0);
        hash = 53 * hash + (this.sector != null ? this.sector.hashCode() : 0);
        hash = 53 * hash + (this.transferability != null ? this.transferability.hashCode() : 0);
        hash = 53 * hash + (this.duration != null ? this.duration.hashCode() : 0);
        hash = 53 * hash + (this.validFor != null ? this.validFor.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final ProductOfferingTerm other = (ProductOfferingTerm) object;
        if (Utilities.areEqual(this.name, other.name) == false) {
            return false;
        }

        if (Utilities.areEqual(this.description, other.description) == false) {
            return false;
        }

        if (Utilities.areEqual(this.type, other.type) == false) {
            return false;
        }

        if (Utilities.areEqual(this.isFullCustom, other.isFullCustom) == false) {
            return false;
        }

        if (Utilities.areEqual(this.exclusivity, other.exclusivity) == false) {
            return false;
        }

        if (Utilities.areEqual(this.region, other.region) == false) {
            return false;
        }

        if (Utilities.areEqual(this.purpose, other.purpose) == false) {
            return false;
        }

        if (Utilities.areEqual(this.sector, other.sector) == false) {
            return false;
        }

        if (Utilities.areEqual(this.transferability, other.transferability) == false) {
            return false;
        }

        if (Utilities.areEqual(this.duration, other.duration) == false) {
            return false;
        }

        if (Utilities.areEqual(this.validFor, other.validFor) == false) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "ProductOfferingTerm{" + "name=" + name + ", description=" + description + ", type=" + type + ", full custom=" + isFullCustom 
            + ", exclusivity=" + exclusivity + ", region=" + region + ", purpose=" + purpose + ", sector=" + sector 
            + ", transferability=" + transferability + ", duration=" + duration + ", validFor=" + validFor + '}';
    }

    public static ProductOfferingTerm createProto() {
        ProductOfferingTerm productOfferingTerm = new ProductOfferingTerm();

        productOfferingTerm.name = "name";
        productOfferingTerm.description = "description";
        productOfferingTerm.duration = "12";
        productOfferingTerm.validFor = TimeRange.createProto();

        return productOfferingTerm;
    }

}