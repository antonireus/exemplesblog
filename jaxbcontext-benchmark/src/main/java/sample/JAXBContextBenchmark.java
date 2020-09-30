package sample;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

public class JAXBContextBenchmark {

    private static final JAXBContext JAXB_CONTEXT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(Persona.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {

        Persona persona;

        @Setup(Level.Trial)
        public void setup() {
            persona = new Persona();
            persona.setNom("Nom de la persona");
            persona.setPrimerCognom("Primer Cognom");
            persona.setSegonCognom("Segon Cognom");
            persona.setDataNaixement(LocalDate.now());
            persona.setDocument("99999999R");
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public String createJAXBContextAndMarshall(BenchmarkState state) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(Persona.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.marshal(state.persona, writer);
        return writer.toString();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public String reuseJAXBContextAndMarshall(BenchmarkState state) throws Exception {
        Marshaller marshaller = JAXB_CONTEXT.createMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.marshal(state.persona, writer);
        return writer.toString();
    }
}
