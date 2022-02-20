package com.googlehashcode.qualification2015.model;

public class DataCenter {
    public final Row[] rows;
    public final Server[] servers;
    public final Pool[] pools;

    public DataCenter(Row[] rows, Server[] servers, Pool[] pools) {
        this.rows = rows;
        this.servers = servers;
        this.pools = pools;
    }
}