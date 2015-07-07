package com.jtool.docbuilderplugin.util;

import java.io.*;


public class FileUtil {
	public static void deleteDirectory(File directory) throws IOException {
		if (!directory.exists()) {
			return;
		}

		cleanDirectory(directory);

		if (!directory.delete()) {
			String message = "Unable to delete directory " + directory + ".";
			throw new IOException(message);
		}
	}
	
	public static void writeToFile(String content, File file)
			throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			bw.write(content);
		}
	}

	public static String readFormFile(String fileName) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream resourceAsStream = loader.getResourceAsStream(fileName);

		StringBuilder sb = new StringBuilder();

		try (BufferedInputStream bufferedInput = new BufferedInputStream(
				resourceAsStream)) {

			byte[] buffer = new byte[1024];

			int bytesRead = 0;

			while ((bytesRead = bufferedInput.read(buffer)) != -1) {
				String chunk = new String(buffer, 0, bytesRead);
				sb.append(chunk);
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return sb.toString();
	}

	public static String readFormFile(File file) throws FileNotFoundException {
		InputStream resourceAsStream = new FileInputStream(file);

		StringBuilder sb = new StringBuilder();

		try (BufferedInputStream bufferedInput = new BufferedInputStream(
				resourceAsStream)) {

			byte[] buffer = new byte[1024];

			int bytesRead = 0;

			while ((bytesRead = bufferedInput.read(buffer)) != -1) {
				String chunk = new String(buffer, 0, bytesRead);
				sb.append(chunk);
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return sb.toString();
	}

	private static void cleanDirectory(File directory) throws IOException {
		if (!directory.exists()) {
			String message = directory + " does not exist";
			throw new IllegalArgumentException(message);
		}

		if (!directory.isDirectory()) {
			String message = directory + " is not a directory";
			throw new IllegalArgumentException(message);
		}

		File[] files = directory.listFiles();
		if (files == null) { // null if security restricted
			throw new IOException("Failed to list contents of " + directory);
		}

		IOException exception = null;
		for (File file : files) {
			try {
				forceDelete(file);
			} catch (IOException ioe) {
				exception = ioe;
			}
		}

		if (null != exception) {
			throw exception;
		}
	}

	private static void forceDelete(File file) throws IOException {
		if (file.isDirectory()) {
			deleteDirectory(file);
		} else {
			boolean filePresent = file.exists();
			if (!file.delete()) {
				if (!filePresent) {
					throw new FileNotFoundException("File does not exist: "
							+ file);
				}
				String message = "Unable to delete file: " + file;
				throw new IOException(message);
			}
		}
	}
	
	public static void makeOutputDir(String outputPath) {
		File outputDir = new File(outputPath);

		if (outputDir.isDirectory()) {
			try {
				FileUtil.deleteDirectory(outputDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		outputDir.mkdirs();
	}
}
