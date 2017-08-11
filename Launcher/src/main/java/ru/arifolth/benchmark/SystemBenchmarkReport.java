package ru.arifolth.benchmark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 27.05.2017.
 */
public class SystemBenchmarkReport extends BenchmarkReport {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemBenchmarkReport.class);

    public static final int GB = 1048576;

    private OperatingSystemMXBean operatingSystemMXBean = java.lang.management.ManagementFactory.getOperatingSystemMXBean();
    private ThreadMXBean threadMXBean = java.lang.management.ManagementFactory.getThreadMXBean();
    private RuntimeMXBean runtimeMXBean = java.lang.management.ManagementFactory.getRuntimeMXBean();

    private int cores = Runtime.getRuntime().availableProcessors();

    private String userName = System.getProperty("user.name");

    private long freeMemory = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getFreePhysicalMemorySize() / GB;
    private long totalMemory = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize() / GB;
    private long swapFile = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalSwapSpaceSize() / GB;

    private String osArch = operatingSystemMXBean.getArch();
    private String osName = operatingSystemMXBean.getName();
    private String osVersion = operatingSystemMXBean.getVersion();
    private double loadAvg = operatingSystemMXBean.getSystemLoadAverage();

    private int threadCount = threadMXBean.getThreadCount();
    private long allThreadsCpuTime = 0L;


    private long uptime = runtimeMXBean.getUptime();

    private InetAddress ip = InetAddress.getLocalHost();

    private List<String> fileSystemRoots = new ArrayList<>();
    public static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();

    public SystemBenchmarkReport() throws UnknownHostException {
        long[] threadIds = threadMXBean.getAllThreadIds();
        for ( int i = 0; i < threadIds.length; i++ ) {
            allThreadsCpuTime += threadMXBean.getThreadCpuTime( threadIds[i] );
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (Path root : FileSystems.getDefault().getRootDirectories()) {
            stringBuilder.setLength(0);

            stringBuilder.append(root).append(": ");
            try {
                FileStore store = Files.getFileStore(root);
                stringBuilder.append("available ").append(NUMBER_FORMAT.format(store.getUsableSpace())).append(", total ").append(NUMBER_FORMAT.format(store.getTotalSpace()));
            } catch (IOException e) {
                LOGGER.error("Error: " + e);
            }
            fileSystemRoots.add(stringBuilder.toString());
        }
    }

    public int getCores() {
        return cores;
    }

    public String getUserName() {
        return userName;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public long getSwapFile() {
        return swapFile;
    }

    public String getOsArch() {
        return osArch;
    }

    public String getOsName() {
        return osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public double getLoadAvg() {
        return loadAvg;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public long getAllThreadsCpuTime() {
        return allThreadsCpuTime;
    }

    public long getUptime() {
        return uptime;
    }

    public InetAddress getIp() {
        return ip;
    }

    public List<String> getFileSystemRoots() {
        return fileSystemRoots;
    }
}
