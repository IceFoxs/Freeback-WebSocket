package net.freeback.server;

import net.freeback.configure.FBConfigure;
import net.freeback.connection.MongoHelper;
import net.freeback.entries.FBConfigureProto;
import net.freeback.monitor.FBErrorMonitor;
import net.freeback.observer.common.FBPhotoObserver;
import net.freeback.observer.orders.FBOrdersObserver;
import net.freeback.observer.store.FBStoreObserver;
import net.freeback.observer.system.FBSystemObserver;
import net.freeback.observer.user.FBLoginObserver;
import net.freeback.observer.user.FBRegisterObserver;
import net.freeback.observer.user.FBUserObserver;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by IntelliJ IDEA.
 * User: 赵天宇
 * Date: 12-10-29
 * Time: 下午4:20
 */
public class FreebackMain {

	static void registerNotification(Class cls) {
		try {
			Class[] types = null;
			Object[] objects = null;
			Constructor constructor = cls.getConstructor(types);
			constructor.newInstance(objects);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static void registerNotification() {
		registerNotification(FBSystemObserver.class);
		registerNotification(FBLoginObserver.class);
		registerNotification(FBRegisterObserver.class);
		registerNotification(FBUserObserver.class);
		registerNotification(FBStoreObserver.class);
		registerNotification(FBOrdersObserver.class);
		registerNotification(FBPhotoObserver.class);
	}

	public static void registerMonitor(){
	   registerNotification(FBErrorMonitor.class);
	}

	/**
	 * @param args
	 * @throws java.io.IOException
	 */
	public static void main(String[] args) throws IOException {
		FreebackMain.registerNotification();
		FreebackMain.registerMonitor();

		DOMConfigurator.configure("out/production/freeback/log4jconfig.xml");
		FBConfigureProto.FBConfigure configure = FBConfigure.buildConfigure();
		MinaServer fbServer = MinaServer.sharedInstance();
		boolean connected = fbServer.connect(configure);
		String listener = "freeback server connect failed";
		if (connected) {
			listener = String.format("listener : %s:%d", configure.getServerHost(), configure.getServerPort());
//			MongoHelper.sharedInstance();
		}
		System.out.println(listener);
	}
}
