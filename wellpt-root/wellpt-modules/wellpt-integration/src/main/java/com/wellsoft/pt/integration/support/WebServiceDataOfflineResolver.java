/*
 * Copyright  1999-2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.wellsoft.pt.integration.support;

import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.resolver.ResourceResolverContext;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.apache.xml.utils.URI;
import org.w3c.dom.Attr;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * This class helps us home users to resolve http URIs without a network
 * connection
 *
 * @author $Author: blautenb $
 */
public class WebServiceDataOfflineResolver extends ResourceResolverSpi {

    /**
     * {@link org.apache.commons.logging} logging facility
     */
    static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(
            WebServiceDataOfflineResolver.class
                    .getName());
    /**
     * Field _uriMap
     */
    static Map _uriMap = null;
    /**
     * Field _mimeMap
     */
    static Map _mimeMap = null;

    static {
        org.apache.xml.security.Init.init();

        WebServiceDataOfflineResolver._uriMap = new HashMap();
        WebServiceDataOfflineResolver._mimeMap = new HashMap();

        WebServiceDataOfflineResolver.register("http://www.w3.org/TR/xml-stylesheet",
                "data/org/w3c/www/TR/xml-stylesheet.html",
                "text/html");
        WebServiceDataOfflineResolver.register("http://www.w3.org/TR/2000/REC-xml-20001006",
                "data/org/w3c/www/TR/2000/REC-xml-20001006", "text/xml");
        WebServiceDataOfflineResolver.register("http://www.nue.et-inf.uni-siegen.de/index.html",
                "data/org/apache/xml/security/temp/nuehomepage", "text/html");
        WebServiceDataOfflineResolver.register(
                "http://www.nue.et-inf.uni-siegen.de/~geuer-pollmann/id2.xml",
                "data/org/apache/xml/security/temp/id2.xml", "text/xml");
        WebServiceDataOfflineResolver.register("http://xmldsig.pothole.com/xml-stylesheet.txt",
                "data/com/pothole/xmldsig/xml-stylesheet.txt", "text/xml");
        WebServiceDataOfflineResolver.register(
                "http://www.w3.org/Signature/2002/04/xml-stylesheet.b64",
                "data/ie/baltimore/merlin-examples/merlin-xmldsig-twenty-three/xml-stylesheet.b64",
                "text/plain");
    }

    /**
     * Method register
     *
     * @param URI
     * @param filename
     * @param MIME
     */
    private static void register(String URI, String filename, String MIME) {
        WebServiceDataOfflineResolver._uriMap.put(URI, filename);
        WebServiceDataOfflineResolver._mimeMap.put(URI, MIME);
    }

    /**
     * Method engineResolve
     *
     * @param uri
     * @param BaseURI
     * @throws ResourceResolverException
     */
    public XMLSignatureInput engineResolve(Attr uri,
                                           String BaseURI) throws ResourceResolverException {

        try {
            String URI = uri.getNodeValue();

            if (WebServiceDataOfflineResolver._uriMap.containsKey(URI)) {
                String newURI = (String) WebServiceDataOfflineResolver._uriMap.get(URI);

                log.debug("Mapped " + URI + " to " + newURI);

                InputStream is = new FileInputStream(newURI);

                log.debug("Available bytes = " + is.available());

                XMLSignatureInput result = new XMLSignatureInput(is);

                // XMLSignatureInput result = new XMLSignatureInput(inputStream);
                result.setSourceURI(URI);
                result.setMIMEType((String) WebServiceDataOfflineResolver._mimeMap.get(URI));

                return result;
            } else {
                Object exArgs[] = {"The URI " + URI + " is not configured for offline work"};

                throw new ResourceResolverException("generic.EmptyMessage", exArgs,
                        uri.getNodeValue(), BaseURI);
            }
        } catch (IOException ex) {
            throw new ResourceResolverException("generic.EmptyMessage", ex, uri.getNodeValue(),
                    BaseURI);
        }
    }

    /**
     * We resolve http URIs <I>without</I> fragment...
     *
     * @param uri
     * @param BaseURI
     */
    public boolean engineCanResolve(Attr uri, String BaseURI) {

        String uriNodeValue = uri.getNodeValue();

        if (uriNodeValue.equals("") || uriNodeValue.startsWith("#")) {
            return false;
        }

        try {
            URI uriNew = new URI(new URI(BaseURI), uri.getNodeValue());

            if (uriNew.getScheme().equals("http")) {
                log.debug("I state that I can resolve " + uriNew.toString());

                return true;
            }

            log.debug("I state that I can't resolve " + uriNew.toString());
        } catch (URI.MalformedURIException ex) {
        }

        return false;
    }

    @Override
    public XMLSignatureInput engineResolveURI(
            ResourceResolverContext resourceResolverContext) throws ResourceResolverException {
        return this.engineResolve(resourceResolverContext.attr, resourceResolverContext.baseUri);
    }

    @Override
    public boolean engineCanResolveURI(ResourceResolverContext resourceResolverContext) {
        return this.engineCanResolve(resourceResolverContext.attr, resourceResolverContext.baseUri);
    }
}
