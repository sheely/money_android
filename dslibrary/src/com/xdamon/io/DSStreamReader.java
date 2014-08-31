package com.xdamon.io;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;

import com.xdamon.util.NumberUtils;

public class DSStreamReader implements Closeable{

    private ByteArrayInputStream stream;

    public DSStreamReader(byte[] buf) {
        stream = new ByteArrayInputStream(buf);
    }

    public DSStreamReader(byte[] buf, int offset, int length) {
        stream = new ByteArrayInputStream(buf, offset, length);
    }

    public byte readByte() throws StreamException {
        if (stream.available() < 1)
            throw new StreamException(1);
        int value = stream.read();
        return (byte) value;
    }

    public byte[] readBytes(int lenght) throws StreamException, IOException {
        if (stream.available() < lenght)
            throw new StreamException(1);
        byte[] buffer = new byte[lenght];
        stream.read(buffer);
        return buffer;
    }

    public int readInt() throws IOException, StreamException {
        byte[] temp = new byte[4];
        if (stream.available() < 4)
            throw new StreamException(4);
        stream.read(temp);
        return NumberUtils.convertToInt(temp);
    }

    public long readLong() throws IOException, StreamException {
        byte[] temp = new byte[8];
        if (stream.available() < 8)
            throw new StreamException(8);
        stream.read(temp);
        return NumberUtils.convertToLong(temp);
    }

    public float readFloat() throws IOException, StreamException {
        byte[] temp = new byte[4];
        if (stream.available() < 4)
            throw new StreamException(4);
        stream.read(temp);
        return NumberUtils.convertToFloat(temp);
    }

    public double readDouble() throws IOException, StreamException {
        byte[] temp = new byte[8];
        if (stream.available() < 8)
            throw new StreamException(8);
        stream.read(temp);
        return NumberUtils.convertToDouble(temp);
    }

    public String readString() throws IOException, StreamException {
        int len = readInt();
        if (len <= 0) {
            return null;
        }
        if (stream.available() < len)
            throw new StreamException(len);
        byte[] tmp = new byte[len];
        stream.read(tmp);
        return new String(tmp, "utf-8");
    }
    
    @Override
    public void close() throws IOException{
        stream.close();
    }

}
