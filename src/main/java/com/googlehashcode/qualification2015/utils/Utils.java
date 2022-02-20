package com.googlehashcode.qualification2015.utils;

import com.googlehashcode.qualification2015.model.DataCenter;
import com.googlehashcode.qualification2015.model.Pool;
import com.googlehashcode.qualification2015.model.Row;
import com.googlehashcode.qualification2015.model.Server;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class Utils {

    private Utils() {
    }

    public static final int FREE_SLOT = Integer.MAX_VALUE;

    public static final int UNAVAILABLE_SLOT = Integer.MIN_VALUE;

    public static final int INVALID_VALUE = -1;

    public static final String VALUE_SEPARATOR = " ";

    public static final String LINE_SEPARATOR = "\n";

    public static final String SERVER_NOT_ALLOCATED = "x";

    public static @NotNull DataCenter readData(@NotNull String inputFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {

            // Read file header
            int R, S, U, P, M;
            String line = br.readLine();
            String[] header = line.split(VALUE_SEPARATOR);
            R = Integer.parseInt(header[0]);
            S = Integer.parseInt(header[1]);
            U = Integer.parseInt(header[2]);
            P = Integer.parseInt(header[3]);
            M = Integer.parseInt(header[4]);

            Row[] rows = new Row[R];
            Server[] servers = new Server[M];
            Pool[] pools = new Pool[P];

            // Read unavailable slots
            List<List<Integer>> unavailableSlots = new ArrayList<>();
            for (int i = 0; i < R; i++)
                unavailableSlots.add(new ArrayList<>());

            for (int i = 0; i < U; i++) {
                line = br.readLine();
                String[] unavailable = line.split(VALUE_SEPARATOR);
                unavailableSlots.get(Integer.parseInt(unavailable[0])).add(Integer.parseInt(unavailable[1]));
            }

            // Initialise rows using unavailable slots
            for (int i = 0; i < R; i++)
                rows[i] = new Row(i, S, unavailableSlots.get(i));

            // Read and initialise servers
            for (int i = 0; i < M; i++) {
                line = br.readLine();
                String[] server = line.split(VALUE_SEPARATOR);
                servers[i] = new Server(i, Integer.parseInt(server[0]), Integer.parseInt(server[1]));
            }

            // Initialise pools
            for (int i = 0; i < P; i++)
                pools[i] = new Pool(i);

            return new DataCenter(rows, servers, pools);
        }
    }

    public static void saveResult(@NotNull DataCenter dataCenter, String outputFile) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            for (Server server : dataCenter.servers) {
                if (server.getRow() == INVALID_VALUE || server.getSlot() == INVALID_VALUE) {
                    bw.write(SERVER_NOT_ALLOCATED + LINE_SEPARATOR);
                } else {
                    bw.write(server.getRow() + VALUE_SEPARATOR + server.getSlot() + VALUE_SEPARATOR + server.getPool() + LINE_SEPARATOR);
                }
            }
        }
    }
}
