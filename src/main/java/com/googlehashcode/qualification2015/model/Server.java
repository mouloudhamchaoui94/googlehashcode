package com.googlehashcode.qualification2015.model;

public class Server {

    public final int id;
    public final int size;
    public final int capacity;
    private int row;
    private int slot;
    private int pool;

    public Server(int id, int size, int capacity) {
        this.id = id;
        this.size = size;
        this.capacity = capacity;
        row = -1;
        slot = -1;
        pool = -1;
    }

    public int getRow() {
        return row;
    }

    public int getSlot() {
        return slot;
    }

    public int getPool() {
        return pool;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void setPool(int pool) {
        this.pool = pool;
    }

    @Override
    public String toString() {
        return "Server_" + id + " = {" +
                "size=" + size +
                ", capacity=" + capacity +
                ", row=" + row +
                ", slot=" + slot +
                '}';
    }
}
