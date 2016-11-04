package org.tmf.dsmapi.commons;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author bahman.barzideh, fdelavega
 *
 */
public class ParsedVersion implements Serializable {
    private final static long serialVersionUID = 1L;

    public static final ParsedVersion ROOT_CATALOG_VERSION = new ParsedVersion("", "");

    private static final int MAX_MAJOR_VERSION = 999999;
    private static final DecimalFormat majorVersionInternalFormat = new DecimalFormat("000000");

    private static final int MAX_MINOR_VERSION = 999;
    private static final DecimalFormat minorVersionInternalFormat = new DecimalFormat("000");

    private Integer majorVersion;
    private List<Integer> minorVersions;

    private String externalView;
    private String internalView;

    private boolean valid;

    private ParsedVersion(String internalView, String externalView) {
        this.majorVersion = null;
        this.minorVersions = new ArrayList<>();

        this.externalView = externalView;
        this.internalView = internalView;

        this.valid = true;
    }

    public ParsedVersion(String version) throws IllegalArgumentException {
        initialize_();
        if (!load_(version)) {
            throw new IllegalArgumentException("'" + version + "' is not a valid version.");
        }

        if (majorVersion > MAX_MAJOR_VERSION) {
            throw new IllegalArgumentException ("Major version, " + majorVersion + ", is too large; maximum value=" + MAX_MAJOR_VERSION);
        }

        List<Integer> invMinors = this.minorVersions.stream()
                .filter((minorVersion) -> minorVersion > MAX_MINOR_VERSION)
                .collect(Collectors.toList());

        if (invMinors.size() > 0) {
            String errMinors = "";
            invMinors.stream().map((minor) -> "," + minor.toString()).reduce(errMinors, String::concat);

            throw new IllegalArgumentException ("Minor versions, " + errMinors + ", are too large; maximum value=" + MAX_MINOR_VERSION);
        }

        this.externalView = createExternalView_();
        this.internalView = createInternalView_();

        this.valid = true;
    }

    public String getExternalView() {
        return this.externalView;
    }

    public String getInternalView() {
        return this.internalView;
    }

    public Integer getMajorVersion() {
        return majorVersion;
    }

    public List<Integer> getMinorVersions() {
        return minorVersions;
    }

    public boolean isValid() {
        return this.valid;
    }

    public boolean isGreaterThan(ParsedVersion other) {
        if (!this.isValid()) {
            throw new IllegalArgumentException ("invalid version object");
        }

        if (other == null || !other.isValid()) {
            throw new IllegalArgumentException ("invalid other version object");
        }

        if (this == ROOT_CATALOG_VERSION) {
            return false;
        }

        if (other == ROOT_CATALOG_VERSION) {
            return true;
        }

        // Compare major version
        int compare = this.majorVersion.compareTo(other.getMajorVersion());
        if (compare < 0) {
            return false;
        }

        if (compare > 0) {
            return true;
        }

        // Compare minor versions
        int minorCompare = 0;
        Iterator<Integer> localIt = this.minorVersions.iterator();
        Iterator<Integer> otherIt = other.getMinorVersions().iterator();

        while(minorCompare == 0 && localIt.hasNext() && otherIt.hasNext()) {
            minorCompare = localIt.next().compareTo(otherIt.next());

            // Version may have different number of minor versions
            if (minorCompare == 0 && !otherIt.hasNext()) {
                minorCompare = 1;
            } else if (minorCompare == 0 && !localIt.hasNext()) {
                minorCompare = -1;
            }
        }

        return minorCompare > 0;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 89 * hash + (this.majorVersion != null ? this.majorVersion.hashCode() : 0);

        if (this.minorVersions.size() > 0) {
            for (Integer minorVersion: this.minorVersions) {
                hash = 89 * hash + minorVersion.hashCode();
            }
        } else {
            hash = 89 * hash + 0;
        }

        hash = 89 * hash + (this.externalView != null ? this.externalView.hashCode() : 0);
        hash = 89 * hash + (this.internalView != null ? this.internalView.hashCode() : 0);
        hash = 89 * hash + (this.valid == true ? 1 : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final ParsedVersion other = (ParsedVersion) object;

        if(this.valid != other.isValid()) {
            return false;
        }

        if (!Utilities.areEqual(this.majorVersion, other.getMajorVersion())) {
            return false;
        }

        if (!Utilities.areEqual(this.externalView, other.getExternalView())) {
            return false;
        }

        if (!Utilities.areEqual(this.internalView, other.getInternalView())) {
            return false;
        }

        if (this.minorVersions.size() != other.getMinorVersions().size()) {
            return false;
        }

        boolean result = true;
        int i = 0;
        while(result && i < this.minorVersions.size()) {
            if (!Utilities.areEqual(this.minorVersions.get(i), other.getMinorVersions().get(i))) {
                result = false;
            }
        }

        return result;
    }

    @Override
    public String toString() {
        String minorVersion = "";
            this.minorVersions.stream().map((minor) -> "," + minor.toString())
                    .reduce(minorVersion, String::concat);

        return "ParsedVersion{" + "majorVersion=" + majorVersion + ", minorVersion=" + minorVersion + ", externalView=" + externalView + ", internalView=" + internalView + ", valid=" + valid + '}';
    }

    private String createInternalView_() {
        String view = majorVersionInternalFormat.format(majorVersion);

        view = this.minorVersions.stream().map((minorVersion) -> 
                "." + minorVersionInternalFormat.format(minorVersion))
                .reduce(view, String::concat);

        return view;
    }

    private String createExternalView_() {
        String view = this.majorVersion.toString();

        view = this.minorVersions.stream().map((minorVersion) -> 
                "." + minorVersion.toString()).reduce(view, String::concat);

        return view;
    }

    private boolean load_(String input) {
        if (input == null) {
            return false;
        }

        // Check that the string is not starting or ending by .
        if (input.startsWith(".") || input.endsWith(".")) {
            return false;
        }

        String parts [] = input.split ("\\.");
        if (parts.length < 2) {
            return false;
        }

        try {
            this.majorVersion = Integer.parseInt(parts [0]);
            List<String> minorParts = new ArrayList<>(Arrays.asList(parts));
            minorParts.remove(0);

            this.minorVersions.addAll(minorParts.stream().map((minor) -> {
                return Integer.parseInt(minor);
            }).collect(Collectors.toList()));

        } catch (Exception ex) {
            return false;
        }

        return true;
    }

    private void initialize_() {
        this.majorVersion = null;
        this.minorVersions = new ArrayList<>();

        this.externalView = null;
        this.internalView = null;

        this.valid = false;
    }

}
