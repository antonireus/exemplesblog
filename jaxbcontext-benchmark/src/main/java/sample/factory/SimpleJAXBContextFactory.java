package sample.factory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class SimpleJAXBContextFactory implements JAXBContextFactory {

    @Override
    public JAXBContext getInstance() {
        try {
            return JAXBContext.newInstance("sample.model");
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
