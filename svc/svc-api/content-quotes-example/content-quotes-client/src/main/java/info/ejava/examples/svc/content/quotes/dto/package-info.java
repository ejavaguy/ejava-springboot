@XmlSchema(namespace = "urn:ejava.svc-controllers.quotes")
@XmlJavaTypeAdapter(type= LocalDate.class, value=JaxbTimeAdapters.LocalDateJaxbAdapter.class)
package info.ejava.examples.svc.content.quotes.dto;

import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;