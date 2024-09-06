package me.lauriichan.minecraft.minestom.server.util.logger.slf4j;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import me.lauriichan.minecraft.minestom.server.util.logger.SysOutSimpleLogger;

final class LogCache implements AutoCloseable {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("YYYY-MM-dd_HH-mm-ss");

    private final File logFolder;
    private final File logFile;

    private final PrintStream stream;

    public LogCache() {
        this(null);
    }

    public LogCache(final File parentFolder) {
        this.logFolder = parentFolder;
        this.logFile = new File(parentFolder, "latest.log");
        PrintStream stream;
        try {
            if (parentFolder != null && !parentFolder.exists()) {
                parentFolder.mkdirs();
            }
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            stream = new PrintStream(logFile);
        } catch (final IOException exp) {
            SysOutSimpleLogger.INSTANCE.error(exp);
            stream = null;
        }
        this.stream = stream;
    }

    public File getLogFolder() {
        return logFolder;
    }

    public File getLogFile() {
        return logFile;
    }

    public PrintStream getStream() {
        return stream;
    }

    public void println(final String message) {
        stream.println(message);
        stream.flush();
    }

    public void archive() throws IOException {
        try {
            if (!logFile.exists()) {
                return;
            }
            Helper.zip(nextZipFile(), logFile);
            try {
                Helper.delete(logFile);
            } catch (final Exception ignore) {
            }
        } finally {
            Helper.createFile(logFile);
        }
    }

    private File nextZipFile() {
        final String time = FORMATTER.format(LocalDateTime.now());
        File file = new File(logFolder, time + ".zip");
        final int index = 0;
        while (file.exists()) {
            file = new File(logFolder, time + "-" + index + ".zip");
        }
        return file;
    }

    @Override
    public void close() throws IOException {
        try {
            archive();
        } finally {
            if (stream == null) {
                return;
            }
            stream.flush();
            stream.close();
        }
    }

}
