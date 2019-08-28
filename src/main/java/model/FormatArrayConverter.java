package model;

import javax.persistence.AttributeConverter;

public class FormatArrayConverter implements AttributeConverter<String[], String> {

    private final String delimeter = ",";

    public String convertToDatabaseColumn(String[] strings) {

        return String.join(delimeter, strings);
    }

    public String[] convertToEntityAttribute(String s) {

        return s.split(",");
    }
}
