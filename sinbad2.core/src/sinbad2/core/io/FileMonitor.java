package sinbad2.core.io;


/*******************************************************************************
 * Copyright (c) 2007 Pascal Essiembre.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Pascal Essiembre - initial API and implementation
 ******************************************************************************/

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;



/**
 * Class monitoring a {@link File} for changes.
 * 
 * @author Pascal Essiembre
 */
public class FileMonitor {

	private static final FileMonitor instance = new FileMonitor();

	private Timer timer;

	private Hashtable<String, FileMonitorTask> timerEntries;

	/**
	 * Gets the file monitor instance.
	 * 
	 * @return file monitor instance
	 */
	public static FileMonitor getInstance() {
		return instance;
	}

	/**
	 * Constructor.
	 */
	private FileMonitor() {
		// Create timer, run timer thread as daemon.
		timer = new Timer(true);
		timerEntries = new Hashtable<String, FileMonitorTask>();
	}

	/**
	 * Adds a monitored file with a {@link IFileChangeListener}.
	 * 
	 * @param listener
	 *            listener to notify when the file changed.
	 * @param fileName
	 *            name of the file to monitor.
	 * @param period
	 *            polling period in milliseconds.
	 */
	public void addFileChangeListener(IFileChangeListener listener,
			String fileName, long period) throws FileNotFoundException {
		
		File file = new File(fileName);
		file.setExecutable(true, false);
		file.setReadable(true, false);
		file.setWritable(true, false);
		
		addFileChangeListener(listener, file, period);
	}

	/**
	 * Adds a monitored file with a FileChangeListener.
	 * 
	 * @param listener
	 *            listener to notify when the file changed.
	 * @param fileName
	 *            name of the file to monitor.
	 * @param period
	 *            polling period in milliseconds.
	 */
	public void addFileChangeListener(IFileChangeListener listener, File file,
			long period) throws FileNotFoundException {
		
		removeFileChangeListener(listener, file);
		FileMonitorTask task = new FileMonitorTask(listener, file);
		timerEntries.put(file.toString() + listener.hashCode(), task);
		timer.schedule(task, period, period);
	}

	/**
	 * Remove the listener from the notification list.
	 * 
	 * @param listener
	 *            the listener to be removed.
	 */
	public void removeFileChangeListener(IFileChangeListener listener,
			String fileName) {
		removeFileChangeListener(listener, new File(fileName));
	}

	/**
	 * Remove the listener from the notification list.
	 * 
	 * @param listener
	 *            the listener to be removed.
	 */
	public void removeFileChangeListener(IFileChangeListener listener, File file) {
		FileMonitorTask task = timerEntries.remove(file.toString()
				+ listener.hashCode());
		if (task != null) {
			task.cancel();
		}
	}

	/**
	 * Fires notification that a file changed.
	 * 
	 * @param listener
	 *            file change listener
	 * @param file
	 *            the file that changed
	 */
	protected void fireFileChangeEvent(final IFileChangeListener listener,
			final File file) {
		listener.fileChanged(file);
	}

	/**
	 * File monitoring task.
	 */
	class FileMonitorTask extends TimerTask {
		IFileChangeListener listener;

		File monitoredFile;

		long lastModified;

		public FileMonitorTask(IFileChangeListener listener, File file)
				throws FileNotFoundException {

			this.listener = listener;
			this.lastModified = 0;
			
			monitoredFile = file;
			
			if (!monitoredFile.exists()) { //TODO but is it on CLASSPATH?
				URL fileURL = listener.getClass().getClassLoader()
						.getResource(monitoredFile.toString());
				
				
				if (fileURL != null) {
					monitoredFile = new File(fileURL.getFile());
				} else {
					throw new FileNotFoundException(
							"File not found" + ": " + file); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			this.lastModified = monitoredFile.lastModified();
		}

		public void run() {
			long lastModified = monitoredFile.lastModified();
			if (lastModified != this.lastModified) {
				this.lastModified = lastModified;
				fireFileChangeEvent(this.listener, monitoredFile);
			}
		}
	}
}
