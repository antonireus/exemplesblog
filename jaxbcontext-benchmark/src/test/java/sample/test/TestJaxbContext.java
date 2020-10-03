package sample.test;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import sample.JAXBContextBenchmark;

import javax.xml.bind.JAXBException;

public class TestJaxbContext {

    private static final JAXBContextBenchmark benchmark = new JAXBContextBenchmark();

    private static final String RESULTAT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><persona>" +
            "<adreça><carrer>Carrer Qualsevol</carrer><codiPostal>07000</codiPostal><numero>99</numero></adreça>" +
            "<dataNaixement>2000-01-01</dataNaixement><document>99999999R</document>" +
            "<id>662953b3-aa90-49fd-b744-d5b16249ce83</id><nom>Nom de la persona</nom><primerCognom>Primer Cognom" +
            "</primerCognom><segonCognom>Segon Cognom</segonCognom></persona>";

    @BeforeClass
    public static void setup() {
        benchmark.setup();
    }

    @Test
    public void testCreateJAXBContextAndMarshall() throws JAXBException {
        Assert.assertEquals(RESULTAT, benchmark.createJAXBContextAndMarshall());
    }
    @Test
    public void testReuseJAXBContextAndMarshall() throws JAXBException {
        Assert.assertEquals(RESULTAT, benchmark.reuseJAXBContextAndMarshall());
    }
}
