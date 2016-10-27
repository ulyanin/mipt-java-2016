package ru.mipt.java2016.homework.g594.sharuev.task2;

import org.objenesis.ObjenesisStd;

import java.io.*;
import java.lang.reflect.Field;
import java.rmi.activation.UnknownObjectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/*
 * Сериализатор для простых данных.
 * Простыми считаются Integer/int, Long/long, Boolean/boolean, Double/double,
 * String, Date и классы, содержащие поля только из этих типов. Поля предков тоже сериализуются.
 * Подклассов - нет.
 */
public class PODSerializer<Value> implements SerializationStrategy<Value> {

    public PODSerializer(Class clazzVal) {
        clazz = clazzVal;
    }

    public void serializeToStream(Value value,
                                  OutputStream outputStream) throws SerializationException {
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        try {
            serializePOD(value, dataOutputStream);
        } catch (UnknownObjectException e) {
            // Это не тип, который сериализует serializePOD. Может, это класс с такими полями?
            //Field[] fields = value.getClass().getDeclaredFields();
            ArrayList<Field> fields = new ArrayList<>();
            Class classToSerialize = clazz;
            do {
                Collections.addAll(fields, classToSerialize.getDeclaredFields());
                classToSerialize = classToSerialize.getSuperclass();
            } while (classToSerialize != null);

            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    serializePOD(field.get(value), dataOutputStream);
                } catch (UnknownObjectException | SerializationException | IllegalAccessException e2) {
                    throw new SerializationException("Can't access member to serialize it", e2);
                }
            }
        }

    }

    public Value deserializeFromStream(InputStream inputStream) throws SerializationException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        try {
            return (Value) deserializePOD(clazz, dataInputStream);
        } catch (UnknownObjectException e) {
            //Field[] fields = clazz.getDeclaredFields();
            ArrayList<Field> fields = new ArrayList<>();
            Class toSerialize = clazz;
            do {
                Collections.addAll(fields, toSerialize.getDeclaredFields());
                toSerialize = toSerialize.getSuperclass();
            } while (toSerialize != null);

            Value ret = (Value) (new ObjenesisStd()).getInstantiatorOf(clazz).newInstance();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    field.set(ret, deserializePOD(field.getType(), dataInputStream));
                } catch (IllegalAccessException | UnknownObjectException e2) {
                    throw new SerializationException("Can't access member to serialize it", e2);
                }
            }
            return ret;
        }

    }

    private void serializePOD(Object o, DataOutputStream dataOutputStream)
            throws SerializationException, UnknownObjectException {
        try {
            if (o instanceof Integer) {
                dataOutputStream.writeInt(((Integer) o));
            } else if (o instanceof Double) {
                dataOutputStream.writeDouble((Double) o);
            } else if (o instanceof Boolean) {
                dataOutputStream.writeBoolean((Boolean) o);
            } else if (o instanceof String) {
                dataOutputStream.writeUTF((String) o);
            } else if (o instanceof Date) {
                dataOutputStream.writeLong(((Date) o).getTime());
            } else {
                throw new UnknownObjectException("Unknown POD type");
            }
        } catch (IOException e) {
            throw new SerializationException("IO POD serialization error", e);
        }
    }

    private Object deserializePOD(Class o, DataInputStream dataInputStream)
            throws SerializationException, UnknownObjectException {
        try {
            if (o == Integer.class || o == int.class) {
                return dataInputStream.readInt();
            } else if (o == Double.class || o == double.class) {
                return dataInputStream.readDouble();
            } else if (o == Boolean.class || o == boolean.class) {
                return dataInputStream.readBoolean();
            } else if (o == String.class) {
                return dataInputStream.readUTF();
            } else if (o == Date.class) {
                return new Date(dataInputStream.readLong());
            } else {
                throw new UnknownObjectException("Unknown POD type");
            }
        } catch (IOException e) {
            throw new SerializationException("IO POD serialization error", e);
        }
    }

    private Class clazz;

}
