package me.lauriichan.minecraft.minestom.util.logger.slf4j;

import java.io.File;
import java.io.IOException;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.helpers.NOPMDCAdapter;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

import me.lauriichan.minecraft.minestom.MinestomArguments;
import me.lauriichan.minecraft.minestom.util.logger.SysOutSimpleLogger;

public class SimpleLoggerProvider implements SLF4JServiceProvider {

    private LogCache logCache;
    
    private ILoggerFactory loggerFactory;
    private IMarkerFactory markerFactory;
    private MDCAdapter mdcAdapter;

    @Override
    public ILoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    @Override
    public IMarkerFactory getMarkerFactory() {
        return markerFactory;
    }

    @Override
    public MDCAdapter getMDCAdapter() {
        return mdcAdapter;
    }

    @Override
    public String getRequestedApiVersion() {
        return "2.0.7";
    }

    @Override
    public void initialize() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        logCache = new LogCache(new File(MinestomArguments.LOG_DIR.value()));
        loggerFactory = new SimpleLoggerFactory(logCache);
        markerFactory = new BasicMarkerFactory();
        mdcAdapter = new NOPMDCAdapter();
    }
    
    private void shutdown() {
        try {
            logCache.close();
        } catch (IOException exp) {
            SysOutSimpleLogger.INSTANCE.error(exp);
        }
    }

}