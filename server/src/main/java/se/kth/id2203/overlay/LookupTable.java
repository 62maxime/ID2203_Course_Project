/*
 * The MIT License
 *
 * Copyright 2017 Lars Kroll <lkroll@kth.se>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package se.kth.id2203.overlay;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.TreeMultimap;
import com.larskroll.common.J6;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.bootstrapping.NodeAssignment;
import se.kth.id2203.networking.NetAddress;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

/**
 * @author Lars Kroll <lkroll@kth.se>
 */
public class LookupTable implements NodeAssignment {

    private static final long serialVersionUID = -8766981433378303267L;

    private final TreeMultimap<Integer, NetAddress> partitions = TreeMultimap.create();

    final static Logger LOG = LoggerFactory.getLogger(LookupTable.class);

    // each partition handles [previous id, )
    public Collection<NetAddress> lookup(String key) {
        int keyHash = key.hashCode();
        Integer partition = partitions.keySet().higher(keyHash); // previously floor
        if (partition == null) {
            partition = partitions.keySet().first(); // previously last
        }
        return partitions.get(partition);
    }

    public Collection<NetAddress> getNodes() {
        return partitions.values();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LookupTable(\n");
        for (Integer key : partitions.keySet()) {
            sb.append(key);
            sb.append(" -> ");
            sb.append(Iterables.toString(partitions.get(key)));
            sb.append("\n");
        }
        sb.append(")");
        return sb.toString();
    }

    public static LookupTable generate(ImmutableSet<NetAddress> nodes, int partitionGroupNumber, int replicationDelta) {
        LookupTable lut = new LookupTable();
        Random random = new Random();
        HashSet<NetAddress> netAddresses = new HashSet<>(nodes);
        for (int i = 0; i < partitionGroupNumber; i++) {
            int key = random.nextInt(Integer.MAX_VALUE);
            for (int j = 0; j < replicationDelta; j++) {
                NetAddress netAddress = J6.randomElement(netAddresses);
                netAddresses.remove(netAddress);
                lut.partitions.put(key, netAddress);
            }

        }
        return lut;
    }

    public ReplicationGroup getKey(NetAddress a) {
        for (int key :
                partitions.keySet()) {
            if (partitions.get(key).contains(a)) {
                return new ReplicationGroup(partitions.keySet().lower(key), key, partitions.get(key));
            }
        }
        return null;
    }


}
