package com.googlehashcode.qualification2015.algorithm;

import com.googlehashcode.qualification2015.model.DataCenter;
import com.googlehashcode.qualification2015.model.Pool;
import com.googlehashcode.qualification2015.model.Row;
import com.googlehashcode.qualification2015.model.Server;
import com.googlehashcode.qualification2015.utils.Utils;
import jdk.jshell.execution.Util;

import java.util.*;
import java.util.stream.Collectors;

public final class OptimizeDataCenter {

    private static final Random random = new Random();

    private OptimizeDataCenter() {
    }

    public static int solve(DataCenter dataCenter) {
        Server[] servers = dataCenter.servers;
        Row[] rows = dataCenter.rows;
        Pool[] pools = dataCenter.pools;

        fillGrid(servers, rows, pools);

        int[] gcCache = new int[pools.length];
        fillPools(servers, rows, pools, gcCache);

        return gcPools(servers, rows, pools, gcCache);
    }

    private static void fillGrid(Server[] servers, Row[] rows, Pool[] pools) {
        // Copy servers and sort it !
        Server[] serverList = new Server[servers.length];
        System.arraycopy(servers, 0, serverList, 0, servers.length);
        Arrays.sort(serverList, (s1, s2) -> s2.capacity / s2.size - s1.capacity / s1.size);

        // Make each server in the best place
        for (Server server : serverList) {
            List<Row> minRows = Arrays.stream(rows).sorted(Comparator.comparingInt(r -> costRow(r, servers))).collect(Collectors.toList());

            for (Row row : minRows) {
                List<Row.FreeSegment> freeSegments = row.getFreeSegments(server.size);
                if (!freeSegments.isEmpty()) {
                    Collections.shuffle(freeSegments);
                    freeSegments.sort(Comparator.comparingInt(e -> e.length));
                    Row.FreeSegment freeSegment = freeSegments.get(0);
                    row.addServer(freeSegment.begin, server);
                    break;
                }
            }
        }
    }

    private static void fillPools(Server[] servers, Row[] rows, Pool[] pools, int[] gcCache) {

        // Make each server in a pool randomly
        for (Server server : servers)
            if (server.getRow() != Utils.INVALID_VALUE && server.getSlot() != Utils.INVALID_VALUE)
                pools[random.nextInt(pools.length)].addServer(server);

        // Initialise cache
        Arrays.fill(gcCache, Utils.INVALID_VALUE);

        // Apply swap optimisation
        for (int i = 0; i < 1000000; i++) {
            swap(servers, rows, pools, gcCache);
        }

        // Initialise cache
        Arrays.fill(gcCache, Utils.INVALID_VALUE);

        // Apply move optimisation
        for (int i = 0; i < 10000000; i++) {
            move(servers, rows, pools, gcCache);
        }
    }

    private static void move(Server[] servers, Row[] rows, Pool[] pools, int[] gcCache) {
        int n = random.nextInt(7);

        Pool src, dst;
        int count = 0;
        do {
            src = pools[random.nextInt(pools.length)];
            if (++count > 10) return;
        } while (src.getServerIds().size() < n);
        count = 0;
        do {
            dst = pools[random.nextInt(pools.length)];
            if (++count > 10) return;
        } while (dst == src);

        List<Server> serverList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Server server = servers[src.getServerIds().get(random.nextInt(src.getServerIds().size()))];
            if (!serverList.contains(server)) serverList.add(server);
        }

        // GC before move
        int costBefore = gcPools(servers, rows, pools, gcCache);
        int gcSrcBefore = gcCache[src.id];
        int gcDstBefore = gcCache[dst.id];

        // Move server from src to dst
        for (Server server : serverList) {
            src.remServer(server.id);
            dst.addServer(server);
        }

        // GC after move
        gcCache[src.id] = Utils.INVALID_VALUE;
        gcCache[dst.id] = Utils.INVALID_VALUE;
        int costAfter = gcPools(servers, rows, pools, gcCache);

        if (costBefore > costAfter) {
            for (Server server : serverList) {
                dst.remServer(server.id);
                src.addServer(server);
            }
            gcCache[src.id] = gcSrcBefore;
            gcCache[dst.id] = gcDstBefore;
        }
    }

    private static void swap(Server[] servers, Row[] rows, Pool[] pools, int[] gcCache) {

        // Choose a pool with a minimum n1 servers
        int n1 = random.nextInt(7);
        Pool pool1;
        int count = 0;
        do {
            pool1 = pools[random.nextInt(pools.length)];
            if (++count > 10) {
                return;
            }
        } while (pool1.getServerIds().size() < n1);

        Set<Integer> serversIds1 = new TreeSet<>();
        int i = 0;
        while (i < n1) {
            if (serversIds1.add(pool1.getServerIds().get(random.nextInt(pool1.getServerIds().size())))) {
                i++;
            }
        }

        // Choose a pool with a minimum n2 servers
        int n2 = random.nextInt(7);
        Pool pool2;
        count = 0;
        do {
            pool2 = pools[random.nextInt(pools.length)];
            if (++count > 10) {
                return;
            }
        } while (pool1 == pool2 || pool2.getServerIds().size() < n2);

        Set<Integer> serversIds2 = new TreeSet<>();
        i = 0;
        while (i < n2) {
            if (serversIds2.add(pool2.getServerIds().get(random.nextInt(pool2.getServerIds().size())))) {
                i++;
            }
        }

        // GC before swapping
        int gcBefore = gcPools(servers, rows, pools, gcCache);
        int pool1GcBefore = gcCache[pool1.id];
        int pool2GcBefore = gcCache[pool2.id];

        // Swap servers between pool1 and pool2
        for (Integer index : serversIds1) {
            pool2.getServerIds().add(index);
            pool1.getServerIds().remove(index);
        }

        for (Integer index : serversIds2) {
            pool1.getServerIds().add(index);
            pool2.getServerIds().remove(index);
        }

        // GC after swapping
        gcCache[pool1.id] = Utils.INVALID_VALUE;
        gcCache[pool2.id] = Utils.INVALID_VALUE;
        int gcAfter = gcPools(servers, rows, pools, gcCache);

        // If the swap does not optimize the global GC then go back
        if (gcBefore > gcAfter) {
            for (Integer index : serversIds1) {
                pool1.getServerIds().add(index);
                pool2.getServerIds().remove(index);
            }

            for (Integer index : serversIds2) {
                pool2.getServerIds().add(index);
                pool1.getServerIds().remove(index);
            }

            gcCache[pool1.id] = pool1GcBefore;
            gcCache[pool2.id] = pool2GcBefore;
        }
    }

    private static int costRow(Row row, Server[] servers) {
        return row.getServers().stream().map(e -> servers[e]).mapToInt(s -> s.capacity).sum();
    }

    private static int gcPool(Pool pool, Server[] servers, Row[] rows) {
        return pool.getServerIds().stream().mapToInt(id -> servers[id].capacity).sum() - Arrays.stream(rows).mapToInt(row -> row.getServers().stream().map(id -> servers[id]).filter(s -> s.getPool() == pool.id).mapToInt(s -> s.capacity).sum()).max().orElse(0);
    }

    private static int gcPools(Server[] servers, Row[] rows, Pool[] pools, int[] gcCache) {
        for (int i = 0; i < gcCache.length; i++)
            if (gcCache[i] == Utils.INVALID_VALUE) gcCache[i] = gcPool(pools[i], servers, rows);
        return Arrays.stream(gcCache).min().orElse(0);
    }
}
