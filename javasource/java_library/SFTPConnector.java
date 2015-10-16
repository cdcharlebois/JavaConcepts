package java_library;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import system.proxies.FileDocument;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.mendix.core.Core;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;

public class SFTPConnector {
	
	private static ILogNode _lognode = Core.getLogger("FTPConnector");
	public static List<IMendixObject> getFilesFromFTP(IContext context, String ftpHost, String ftpUserName, String ftpPassword, String ftpRemoteDirectory ) {
		return getFilesFromFTP(context, ftpHost, ftpUserName, ftpPassword, ftpRemoteDirectory, 22, null);
	}
	public static List<IMendixObject> getFilesFromFTP(IContext context, String ftpHost, String ftpUserName, String ftpPassword, String ftpRemoteDirectory, Integer ftpPort ) {
		return getFilesFromFTP(context, ftpHost, ftpUserName, ftpPassword, ftpRemoteDirectory, ftpPort, null);
	}
	
    public static List<IMendixObject> getFilesFromFTP(IContext context, String ftpHost, String ftpUserName, String ftpPassword, String ftpRemoteDirectory, Integer ftpPort, Boolean ftpRemoveAfterDownload ) {
    	ArrayList<IMendixObject> returnList = new ArrayList<IMendixObject>();
    	try {
            //
            //First Create a JSch session
            //
    		_lognode.debug("Creating session.");
            JSch jsch = new JSch();
            Session session = null;
            Channel channel = null;
            ChannelSftp c = null;

            //
            //Now connect and SFTP to the SFTP Server
            //
            try {
                //Create a session sending through our username and password
                session = jsch.getSession(ftpUserName, ftpHost, ftpPort);
                _lognode.trace("Session created.");
                session.setPassword(ftpPassword);
                //Security.addProvider(new com.sun.crypto.provider.SunJCE());

                //
                //Setup Strict HostKeyChecking to no so we dont get the
                //unknown host key exception
                //
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                session.connect();
                _lognode.trace("Session connected.");

                //
                //Open the SFTP channel
                //
                _lognode.trace("Opening Channel.");
                channel = session.openChannel("sftp");
                channel.connect();
                c = (ChannelSftp)channel;
            } catch (Exception e) {
            	_lognode.error("Unable to connect to FTP server. "+e.toString());
                throw e;
            }            

            //
            //Change to the remote directory
            //
            _lognode.debug("Changing to FTP remote dir: " + ftpRemoteDirectory);
            c.cd(ftpRemoteDirectory);            

            //
            //Send the file we generated
            //
//            try {
//                File f = new File(fileToTransmit);
//                System.out.println("Storing file as remote filename: " + f.getName());
//                c.put(new FileInputStream(f), f.getName());
//            } catch (Exception e) {
//                System.err.println("Storing remote file failed. "+e.toString());
//                throw e;
//            }

            //
            //Get the list of files in the remote server directory
            //
            Vector<?> files = c.ls(ftpRemoteDirectory);

            //
            //Log if we have nothing to download
            //
            if (files.size() == 0) {
            	_lognode.debug("No files are available for download.");
            }
            //
            //Otherwise download all files except for the . and .. entries
            //
            else {
                for (int i=0; i<files.size(); i++) {
                    com.jcraft.jsch.ChannelSftp.LsEntry lsEntry = (com.jcraft.jsch.ChannelSftp.LsEntry) files.get(i);

                    if (!lsEntry.getFilename().equals(".") && !lsEntry.getFilename().equals("..")) {
                    	String filename = lsEntry.getFilename();
                    	_lognode.debug("Downloading file "+ filename);

    					IMendixObject fileDoc = Core.instantiate(context, FileDocument.getType());
    					Core.storeFileDocumentContent(context, fileDoc,filename, c.get(filename));
                        
                        //
                        //Remove the file from the server
                        //
                        if( ftpRemoveAfterDownload == true ) {
                        	_lognode.debug("Removing file " + filename);
                        	c.rm(filename);
                        }
                    }
                }
            }

            //
            //Disconnect from the FTP server
            //
            try {
                c.quit();
            } catch (Exception exc) {
                _lognode.error("Unable to disconnect from FTP server. " + exc.toString());
            }            

        } catch (Exception e) {
        	_lognode.error("Error: "+e.toString());
        }

        _lognode.debug("Process Complete.");
        return returnList;
    }

	
}
