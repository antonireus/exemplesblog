package sample.transformer;

import sample.factory.JAXBContextFactory;
import sample.model.Persona;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

public class PersonaTransformer {

    private final JAXBContext jaxbContext;

    public PersonaTransformer(JAXBContextFactory jaxbContextFactory) {
        this.jaxbContext = jaxbContextFactory.getInstance();
    }

    public String marshall(Persona persona) {
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            StringWriter writer = new StringWriter();
            marshaller.marshal(persona, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public Persona unmarshall(String string) {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(string);
            return (Persona) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
