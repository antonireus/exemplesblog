package sample.factory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class SingletonJAXBContextFactory implements JAXBContextFactory {

    private static final JAXBContext INSTANCE;
    static {
        try {
            INSTANCE = JAXBContext.newInstance("sample.model");
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JAXBContext getInstance() {
        return INSTANCE;
    }
}
