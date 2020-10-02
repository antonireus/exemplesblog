package sample;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import sample.factory.SimpleJAXBContextFactory;
import sample.factory.SingletonJAXBContextFactory;
import sample.model.Adreça;
import sample.model.Persona;
import sample.transformer.PersonaTransformer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class JAXBContextBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {

        Persona persona;

        @Setup(Level.Trial)
        public void setup() {
            persona = new Persona();
            persona.setId(UUID.randomUUID().toString());
            persona.setNom("Nom de la persona");
            persona.setPrimerCognom("Primer Cognom");
            persona.setSegonCognom("Segon Cognom");
            persona.setDataNaixement(LocalDate.now());
            persona.setDocument("99999999R");
            Adreça adreça = new Adreça();
            adreça.setCarrer("Carrer Qualsevol");
            adreça.setNumero(99);
            adreça.setCodiPostal("07000");
            persona.setAdreça(adreça);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public String createJAXBContextAndMarshall(BenchmarkState state) {
        PersonaTransformer personaTransformer = new PersonaTransformer(new SimpleJAXBContextFactory());
        return personaTransformer.marshall(state.persona);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public String reuseJAXBContextAndMarshall(BenchmarkState state) {
        PersonaTransformer personaTransformer = new PersonaTransformer(new SingletonJAXBContextFactory());
        return personaTransformer.marshall(state.persona);
    }
}
