package com.xdamon.io;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;

import com.xdamon.util.NumberUtils;

public class DSStreamWriter implements Closeable{

    private ByteArrayOutputStream stream;

    public DSStreamWriter() {
        stream = new ByteArrayOutputStream();
    }

    public DSStreamWriter(int size) {
        stream = new ByteArrayOutputStream(size);
    }

    public void writeByte(byte b) {
        stream.write(b);
    }

    public void writeByte(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            writeInt(0);
            return;
        }
        writeInt(bytes.length);
        stream.write(bytes);
    }

    public void writeInt(int value) throws IOException {
        stream.write(NumberUtils.convertToBinary(value));
    }

    public void writeLong(long value) throws IOException {
        stream.write(NumberUtils.convertToBinary(value));
    }

    public void writeFloat(float value) throws IOException {
        stream.write(NumberUtils.convertToBinary(value));
    }

    public void writeDouble(double value) throws IOException {
        stream.write(NumberUtils.convertToBinary(value));
    }

    public void writeString(String value) throws IOException {
        if (value == null) {
            writeInt(0);
            return;
        }
        byte[] tmp = value.getBytes("utf-8");
        writeInt(tmp.length);
        stream.write(tmp);
    }

    public byte[] toByteArray() {
        return stream.toByteArray();
    }
    
    @Override
    public void close() throws IOException{
        stream.close();
    }

}
