package ag.security.management.system;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ag.security.management.system.providers.DataProvider;
import ag.security.management.system.providers.impl.FileLogProvider;
import ag.security.management.system.services.Executable;
import ag.security.management.system.services.impl.LogService;

public class ExecutiveServer {

	private static Logger logger = LoggerFactory.getLogger(ExecutiveServer.class);

	@SuppressWarnings("serial")
	private static List<DataProvider> logProviders = new ArrayList<DataProvider>() {{
			add(new FileLogProvider(getLogFilePath("log.txt")));
			add(new FileLogProvider(getLogFilePath("log2.txt")));
	}};

	public static void main(String[] args) {
		setupServer();
		startServer();
	}

	private static void setupServer() {
		logger.debug("Inside server setup");
	}

	private static void startServer() {
		logger.debug("Start server");

		Executable logService = new LogService(logProviders);
		logService.proceed();
		logService.shutdownAfterCompletion();
	}

	private static String getLogFilePath(String fileName) {
		ClassLoader classLoader = ExecutiveServer.class.getClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());

		return file.getPath();
	}
}
