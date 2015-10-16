// This file was generated by Mendix Business Modeler.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package java_library.actions;

import java.util.Collection;
import scala.collection.Iterator;
import scala.collection.immutable.List;
import com.mendix.core.Core;
import com.mendix.core.cache.CacheSessionStatistics;
import com.mendix.core.cache.MxObjectCache;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixIdentifier;
import com.mendix.systemwideinterfaces.core.ISession;
import com.mendix.webui.CustomJavaAction;

/**
 * analyze all objects in memory.
 * 
 * If the lognode "MemoryCheck" is enabled to print trace messages all details are printed into the log.
 * 
 * If you pass the boolean print info (or level debug), it will print per session how many objects are in memory in total
 * If you pass the boolean print details (or level trace), it will print the GUID of every object in memory
 */
public class CheckMemory591 extends CustomJavaAction<Boolean>
{
	private Boolean AlwaysPrintDetails;
	private Boolean AlwaysPrintInfo;

	public CheckMemory591(IContext context, Boolean AlwaysPrintDetails, Boolean AlwaysPrintInfo)
	{
		super(context);
		this.AlwaysPrintDetails = AlwaysPrintDetails;
		this.AlwaysPrintInfo = AlwaysPrintInfo;
	}

	@Override
	public Boolean executeAction() throws Exception
	{
		// BEGIN USER CODE
		Collection<? extends ISession> sessions = Core.getActiveSessions();
		if( this.AlwaysPrintDetails )
			this.AlwaysPrintInfo = true;
		
		
		if( _logNode.isDebugEnabled() || this.AlwaysPrintInfo) {
			MxObjectCache cache = this.getComponent().mxObjectCache();
			Iterator<String> map = cache.getSessionStatistics().keysIterator();
			while( map.hasNext() )  {
				
				
				String sessionID = map.next();
				CacheSessionStatistics stats = cache.getSessionStatistics().get( sessionID ).get();
				
				List<IMendixIdentifier> cachedIds = stats.identifiers();
				IMendixIdentifier id ;
				if( this.AlwaysPrintInfo )
					_logNode.info( sessionID + " / [" + getUserName(sessions, sessionID) + "] nr records: " +cachedIds.size() );
				else 
					_logNode.debug( sessionID + " / [" + getUserName(sessions, sessionID) + "] nr records: " +cachedIds.size() );
				
				if( cachedIds.size() > 0 && (_logNode.isTraceEnabled() || this.AlwaysPrintDetails) ) {
					Iterator<IMendixIdentifier> iter = cachedIds.iterator();
					while( iter.hasNext() ) {
						
						id = iter.next();
						if( this.AlwaysPrintDetails )
							_logNode.info( sessionID + " / " +  id.getObjectType() + "-"+ id.toLong() );
						else
							_logNode.trace( sessionID + " / " +  id.getObjectType() + "-"+ id.toLong() );
					}
				}
			}
		}
		
		return true;
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 */
	@Override
	public String toString()
	{
		return "CheckMemory591";
	}

	// BEGIN EXTRA CODE

	private static ILogNode _logNode = Core.getLogger("MemoryCheck");
//	public static ArrayList<CacheManager> initializedManagers = new ArrayList<CacheManager>();
	
	public static String getUserName( Collection<? extends ISession> sessions, String sessionId ) {
		for( ISession session : sessions ) {
			if( session.getId().toString().equals(sessionId) )
				return session.getUser().getName();
		}
		return "(System)";
	}

//	public static void initializeManager(CacheManager cm) {
//
//		if( !initializedManagers.contains(cm) ) {
//			MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
//			ManagementService.registerMBeans(cm, mBeanServer, false, false, false, true);
//
//			initializedManagers.add( cm );
//		}
//	}
	
	// END EXTRA CODE
}
