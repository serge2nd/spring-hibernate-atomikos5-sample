package ru.serge2nd.samples.hib.atmks.data;

import javax.persistence.AttributeConverter;
import java.time.LocalTime;

class TimeConverter implements AttributeConverter<LocalTime, Integer> {
    public Integer   convertToDatabaseColumn(LocalTime attr) { return attr.toSecondOfDay() / 60; }
    public LocalTime convertToEntityAttribute(Integer col)   { return LocalTime.ofSecondOfDay(col * 60); }
}
