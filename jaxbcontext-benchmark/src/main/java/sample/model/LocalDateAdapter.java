package sample.model;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    public LocalDate unmarshal(String value) {
        return LocalDate.parse(value);
    }

    public String marshal(LocalDate value) {
        return value.toString();
    }
}