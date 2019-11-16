package main.webapp.java.com.customparams;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class MyDateConverterProvider implements ParamConverterProvider {


    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType.getName().equals(MyDate.class.getName())) {
            return new MyDate();
        }
        return null;
    }
}
