package de.uksh.medic.mpiproxy.routes;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.uksh.medic.mpiproxy.mpi.MpiResolver;
import de.uksh.medic.mpiproxy.proxy.FhirProxy;
import de.uksh.medic.mpiproxy.settings.Settings;

@RestController
public class XmlRoute {

    private final static String EXPR = "//Patient/identifier[type/coding/system/@value='"
            + Settings.getPatientIdentifierCodingSystem() + "' and type/coding/code/@value='"
            + Settings.getPatientIdentifierCodingCode() + "']/value/@value";
    DocumentBuilder f;
    XPath xPath = XPathFactory.newInstance().newXPath();
    TransformerFactory tf;
    Transformer trans;
    StringWriter sw;

    public XmlRoute() throws ParserConfigurationException, TransformerConfigurationException {
        f = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        tf = TransformerFactory.newInstance();
        trans = tf.newTransformer();
    }

    @RequestMapping(value = { "fhir/**" }, method = { RequestMethod.POST,
            RequestMethod.PUT }, consumes = {
                    "application/fhir+xml",
                    MediaType.APPLICATION_XML_VALUE }, produces = { "application/fhir+xml",
                            MediaType.APPLICATION_XML_VALUE })
    public String patient(InputStream is, HttpServletRequest request, HttpServletResponse response)
            throws SAXException, IOException, XPathExpressionException, TransformerException {
        Document d = f.parse(is);
        NodeList nodeList = (NodeList) xPath.evaluate(EXPR, d, XPathConstants.NODESET);
        IntStream.range(0, nodeList.getLength())
                .mapToObj(nodeList::item).forEach(n -> {
                    n.setNodeValue(MpiResolver.resolve(n.getNodeValue()));
                });
        Settings.getExclude().forEach(ex -> {
            try {
                removeNode(d, ex);
            } catch (XPathExpressionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        sw = new StringWriter();
        trans.transform(new DOMSource(d), new StreamResult(sw));
        ResponseEntity<String> r = FhirProxy.forward(sw.toString(),
                Settings.getClinicalDataServerUrl(), request);
        response.setStatus(r.getStatusCode().value());
        return r.getBody();
    }

    @RequestMapping(value = { "fhir/**" }, method = { RequestMethod.GET }, produces = {
            "application/fhir+xml", MediaType.APPLICATION_XML_VALUE })
    public String patient(HttpServletRequest request, HttpServletResponse response)
            throws XPathExpressionException, TransformerException, SAXException, IOException {
        ResponseEntity<String> r = FhirProxy.forward(null,
                Settings.getClinicalDataServerUrl(), request);
        Document d = f.parse(new InputSource(new StringReader(r.getBody())));
        NodeList nodeList = (NodeList) xPath.evaluate(EXPR, d, XPathConstants.NODESET);
        IntStream.range(0, nodeList.getLength())
                .mapToObj(nodeList::item).forEach(n -> {
                    n.setNodeValue(MpiResolver.reverse(n.getNodeValue()));
                });
        sw = new StringWriter();
        trans.transform(new DOMSource(d), new StreamResult(sw));
        response.setStatus(r.getStatusCode().value());
        return sw.toString();
    }

    private void removeNode(Document d, String name) throws XPathExpressionException {
        NodeList nl = (NodeList)xPath.evaluate(EXPR + "/../../../" + name, d, XPathConstants.NODESET);
        for(int i = 0; i < nl.getLength(); i++) {
            nl.item(i).getParentNode().removeChild(nl.item(i));
        }
    }

}
