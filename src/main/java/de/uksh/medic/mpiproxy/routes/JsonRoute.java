package de.uksh.medic.mpiproxy.routes;

import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.uksh.medic.mpiproxy.mpi.MpiResolver;
import de.uksh.medic.mpiproxy.proxy.FhirProxy;
import de.uksh.medic.mpiproxy.settings.Settings;

@RestController
public class JsonRoute {

    private final static String EXPR = "$..[?(@.resourceType == 'Patient' && @.identifier[?(@.type.coding[?(@.system=='"
            + Settings.getPatientIdentifierCodingSystem() + "' && @.type.coding[0].code=='"
            + Settings.getPatientIdentifierCodingCode() + "')])])].identifier[?(@.type.coding[?(@.system=='"
            + Settings.getPatientIdentifierCodingSystem() + "' && @.type.coding[0].code=='"
            + Settings.getPatientIdentifierCodingCode() + "')])]";
    private final static String EXPR2 = "$..[?(@.resourceType == 'Patient' && @.identifier[?(@.type.coding[?(@.system=='"
            + Settings.getPatientIdentifierCodingSystem() + "' && @.type.coding[0].code=='"
            + Settings.getPatientIdentifierCodingCode() + "')])])]";

    @SuppressWarnings("unchecked")
    @RequestMapping(value = { "/fhir", "/fhir/Patient/**" }, method = { RequestMethod.POST,
            RequestMethod.PUT }, consumes = {
                    "application/fhir+json",
                    MediaType.APPLICATION_JSON_VALUE }, produces = { "application/fhir+json",
                            MediaType.APPLICATION_JSON_VALUE })
    public String patient(InputStream input, HttpServletRequest request, HttpServletResponse response) {
        DocumentContext dc = JsonPath.parse(input);
        dc.put(EXPR, "value", MpiResolver.resolve(((List<String>) dc.read(EXPR + ".value")).get(0)));
        Settings.getExclude().forEach(ex -> dc.delete(EXPR2 + "." + ex));

        ResponseEntity<String> r = FhirProxy.forward(dc.jsonString(),
                Settings.getClinicalDataServerUrl(), request);
        response.setStatus(r.getStatusCode().value());
        return r.getBody();
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = { "fhir/**" }, method = { RequestMethod.GET }, produces = {
            "application/fhir+json", MediaType.APPLICATION_JSON_VALUE })
    public String patient(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity<String> r = FhirProxy.forward(null,
                Settings.getClinicalDataServerUrl(), request);
        DocumentContext dc = JsonPath.parse(r.getBody());
        List<String> l = ((List<String>) dc.read(EXPR + ".value"));
        if (l != null && !l.isEmpty()) {
            dc.put(EXPR, "value", MpiResolver.reverse(l.get(0)));
        }
        response.setStatus(r.getStatusCode().value());
        return dc.jsonString();
    }

}
