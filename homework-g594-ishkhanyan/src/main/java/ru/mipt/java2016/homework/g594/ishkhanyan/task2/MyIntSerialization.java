package ru.mipt.java2016.homework.g594.ishkhanyan.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by ${Semien} on ${30.10.16}.
 */
public class MyIntSerialization implements MySerialization<Integer> {
    @Override
    public void writeToFile(Integer object, DataOutput file) throws IOException {
        file.writeInt(object);
    }

    @Override
    public Integer readFromFile(DataInput file) throws IOException {
        return file.readInt();
    }
}
