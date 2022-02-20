package com.googlehashcode.qualification2015.model;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Pool {

    public final int id;
    private final List<Integer> serverIds;

    public Pool(int id) {
        this.id = id;
        this.serverIds = new ArrayList<>();
    }

    public List<Integer> getServerIds() {
        return serverIds;
    }

    public void addServer(@NotNull Server server) {
        serverIds.add(server.id);
        server.setPool(id);
    }

    public void remServer(int id) {
        serverIds.remove(Integer.valueOf(id));
    }

    @Override
    public String toString() {
        return "Pool_" + id + " = " + serverIds;
    }
}
