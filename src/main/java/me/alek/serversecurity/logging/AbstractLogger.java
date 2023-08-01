package me.alek.serversecurity.logging;

import me.alek.serversecurity.ServerSecurityPlugin;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.File;
import java.io.IOException;

public abstract class AbstractLogger {

    private final LoggerContext context;
    private final Logger logger;
    private final File file;

    public AbstractLogger() {
        this.file = new File(ServerSecurityPlugin.get().getDataFolder(), getFileName());

        if (!file.exists()) {
            try {
                this.file.getParentFile().mkdirs();
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.context = (LoggerContext) LogManager.getContext(false);
        Configuration configuration = context.getConfiguration();

        PatternLayout layout = PatternLayout.newBuilder()
                .withPattern("[%d{MM-dd HH:mm:ss}]: %m %n")
                .build();

        FileAppender appender = FileAppender.newBuilder()
                .setConfiguration(configuration)
                .withFileName(file.getPath())
                .withAppend(true)
                .withLocking(false)
                .withLayout(layout)
                .withName(getName())
                .build();
        appender.start();
        configuration.addAppender(appender);

        LoggerConfig loggerConfig = new LoggerConfig(getName(), Level.INFO, true);
        loggerConfig.addAppender(appender, null, null);
        configuration.addLogger(getName(), loggerConfig);

        context.updateLoggers(configuration);
        this.logger = LogManager.getLogger(getName());
    }

    public void log(Level level, String str) {
        getLogger().log(level, str);
    }

    public Logger getLogger() {
        return logger;
    }

    public LoggerContext getContext() {
        return context;
    }

    public File getFile() {
        return file;
    }

    protected abstract String getFileName();

    protected abstract String getName();

}
