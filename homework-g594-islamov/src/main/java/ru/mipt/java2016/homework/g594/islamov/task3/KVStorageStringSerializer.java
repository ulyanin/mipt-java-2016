package ru.mipt.java2016.homework.g594.islamov.task3;

import ru.mipt.java2016.homework.g594.islamov.task2.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Iskander Islamov on 13.11.2016.
 */

class KVStorageStringSerializer implements KVSSerializationInterface<String> {
    @Override
    public String serialize(String object) {
        return object;
    }

    @Override
    public String deserialize(String object) {
        return object;
    }
}