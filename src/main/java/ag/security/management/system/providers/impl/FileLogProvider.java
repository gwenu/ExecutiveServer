package ag.security.management.system.providers.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ag.security.management.system.providers.DataProvider;

public class FileLogProvider implements DataProvider {
	private static Logger logger = LoggerFactory.getLogger(FileLogProvider.class);
	
	private String logPath;
	
	public FileLogProvider(String logPath) {
		this.logPath = logPath;
	}
	
	public InputStream getDataStream() {
		InputStream iStream = null;
		File logFile = new File(logPath);
		
		try {
			iStream = new FileInputStream(logFile);
		} catch (FileNotFoundException fileNotFoundException) {
			logger.error("Cannot open log file: " + 
					fileNotFoundException.getMessage());
		}
		return iStream;
	}
}
