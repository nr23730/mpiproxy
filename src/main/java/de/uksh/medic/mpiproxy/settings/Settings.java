package de.uksh.medic.mpiproxy.settings;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Settings {
    
    @JsonProperty("clinicalDataServerUrl")
    private static String clinicalDataServerUrl;
    @JsonProperty("patientIdentifierCodingSystem")
    private static String patientIdentifierCodingSystem;
    @JsonProperty("patientIdentifierCodingCode")
    private static String patientIdentifierCodingCode;
    @JsonProperty("exclude")
    private static List<String> exclude = new ArrayList<String>();

    /**
     * @return the clinicalDataServerUrl
     */
    @JsonProperty("clinicalDataServerUrl")
    public static String getClinicalDataServerUrl() {
        return clinicalDataServerUrl;
    }

    /**
     * @param clinicalDataServerUrl the clinicalDataServerUrl to set
     */
    @JsonProperty("clinicalDataServerUrl")
    public void setClinicalDataServerUrl(String newClinicalDataServerUrl) {
        clinicalDataServerUrl = newClinicalDataServerUrl;
    }

    /**
     * @return the patientIdentifierCodingSystem
     */
    @JsonProperty("patientIdentifierCodingSystem")
    public static String getPatientIdentifierCodingSystem() {
        return patientIdentifierCodingSystem;
    }

    /**
     * 
     * @param patientIdentifierCodingSystem the patientIdentifierCodingSystem to set
     */
    @JsonProperty("patientIdentifierCodingSystem")
    public void setPatientIdentifierCodingSystem(String newPatientIdentifierCodingSystem) {
        patientIdentifierCodingSystem = newPatientIdentifierCodingSystem;
    }

    /**
     * @return the patientIdentifierCodingCode
     */
    @JsonProperty("patientIdentifierCodingSystem")
    public static String getPatientIdentifierCodingCode() {
        return patientIdentifierCodingCode;
    }

    /**
     * @param patientIdentifierCodingCode the patientIdentifierCodingCode to set
     */
    @JsonProperty("patientIdentifierCodingCode")
    public void setPatientIdentifierCodingCode(String newPatientIdentifierCodingCode) {
        patientIdentifierCodingCode = newPatientIdentifierCodingCode;
    }

    /**
     * @return the exclude
     */
    @JsonProperty("exclude")
    public static List<String> getExclude() {
        return exclude;
    }

    /**
     * @param exclude the exclude to set
     */
    @JsonProperty("exclude")
    public void setExclude(List<String> exclude) {
        Settings.exclude = exclude;
    }
}
