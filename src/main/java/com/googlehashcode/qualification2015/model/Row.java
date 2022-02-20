package com.googlehashcode.qualification2015.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Row {

    public static class FreeSegment {

        public final int begin;
        public final int length;

        public FreeSegment(int begin, int length) {
            this.begin = begin;
            this.length = length;
        }

        @Override
        public String toString() {
            return "FreeSegment{" +
                    "begin=" + begin +
                    ", length=" + length +
                    '}';
        }
    }

    private static final int UNAVAILABLE_SLOT = Integer.MIN_VALUE;
    private static final int FREE_SLOT = Integer.MAX_VALUE;

    public final int id;
    public int nSlots;
    private final int[] slots;

    public Row(int id, int nSlots, List<Integer> unavailableSlots) {
        this.id = id;
        this.nSlots = nSlots;

        // Initialize slots
        slots = new int[nSlots];
        for (int i = 0; i < nSlots; i++) {
            slots[i] = FREE_SLOT;
        }
        for (int unavailableSlot : unavailableSlots) {
            slots[unavailableSlot] = UNAVAILABLE_SLOT;
        }
    }

    private boolean isUnavailableSlot(int slot) {
        return slots[slot] == UNAVAILABLE_SLOT;
    }

    private boolean isFreeSlot(int slot) {
        return slots[slot] == FREE_SLOT;
    }

    private boolean isServerHead(int slot) {
        int content = slots[slot];
        return content >= 0 && content != FREE_SLOT;
    }

    private boolean isServerBody(int slot) {
        int content = slots[slot];
        return content < 0 && content != UNAVAILABLE_SLOT;
    }

    private boolean isServer(int slot) {
        return isServerHead(slot) || isServerBody(slot);
    }

    private int freeLength(int slot) {
        int free = 0;
        while (slot < nSlots && isFreeSlot(slot)) {
            free++;
            slot++;
        }
        return free;
    }

    public boolean canAddServer(int slot, Server server) {
        return freeLength(slot) >= server.size;
    }

    public boolean addServer(int slot, Server server) {
        if (!canAddServer(slot, server)) return false;
        slots[slot] = server.id;
        for (int i = 1, d = -1; i < server.size; i++, d--) slots[slot + i] = d;
        server.setRow(id);
        server.setSlot(slot);
        return true;
    }

    public int getServerAt(int slot) {
        if (isUnavailableSlot(slot) || isFreeSlot(slot)) return -1;
        if (isServerHead(slot)) return slots[slot];
        return slots[slot - slots[slot]];
    }

    public List<Integer> getServers() {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < nSlots; i++) {
            if (isServerHead(i)) ids.add(slots[i]);
        }
        return ids;
    }

    public List<FreeSegment> getFreeSegments() {
        return getFreeSegments(1);
    }

    public List<FreeSegment> getFreeSegments(int minLength) {
        if (minLength <= 0)
            return new ArrayList<>();
        List<FreeSegment> freeSegments = new ArrayList<>();
        int i = 0;
        while (i < nSlots) {
            int length = freeLength(i);
            if (length > 0) {
                if (length >= minLength) freeSegments.add(new FreeSegment(i, length));
                i += length;
            } else {
                i++;
            }
        }
        return freeSegments;
    }

    @Override
    public String toString() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < nSlots; i++) {
            if (isUnavailableSlot(i)) list.add("U");
            else if (isFreeSlot(i)) list.add("F");
            else list.add(String.valueOf(slots[i]));
        }
        return list.toString();
    }
}
