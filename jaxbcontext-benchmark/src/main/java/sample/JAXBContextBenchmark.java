package sample;

import org.openjdk.jmh.annotations.*;
import sample.model.Adreça;
import sample.model.Persona;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.time.LocalDate;

@State(Scope.Benchmark)
public class JAXBContextBenchmark {

    private static final JAXBContext JAXB_CONTEXT;
    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(Persona.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private Persona persona;

    @Setup(Level.Trial)
    public void setup() {
        persona = new Persona();
        persona.setId("662953b3-aa90-49fd-b744-d5b16249ce83");
        persona.setNom("Nom de la persona");
        persona.setPrimerCognom("Primer Cognom");
        persona.setSegonCognom("Segon Cognom");
        persona.setDataNaixement(LocalDate.of(2000, 1, 1));
        persona.setDocument("99999999R");
        Adreça adreça = new Adreça();
        adreça.setCarrer("Carrer Qualsevol");
        adreça.setNumero(99);
        adreça.setCodiPostal("07000");
        persona.setAdreça(adreça);
    }


    @Benchmark
    public String createJAXBContextAndMarshall() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Persona.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.marshal(persona, writer);
        return writer.toString();
    }

    @Benchmark
    public String reuseJAXBContextAndMarshall() throws JAXBException {
        Marshaller marshaller = JAXB_CONTEXT.createMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.marshal(persona, writer);
        return writer.toString();
    }
}
