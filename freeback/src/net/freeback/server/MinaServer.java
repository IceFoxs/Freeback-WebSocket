package net.freeback.server;

import net.freeback.codec.FBCodecFactory;
import net.freeback.entries.FBConfigureProto;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoEventType;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.firewall.ConnectionThrottleFilter;
import org.apache.mina.filter.logging.LogLevel;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;

public class MinaServer {
	private IoAcceptor acceptor = null;
	private HashMap<String, Long> userSessions = new HashMap<String, Long>();

	private MinaServer() {
	}

	static private MinaServer _MinaServer = null;

	static public MinaServer sharedInstance() {
		if (_MinaServer == null) {
			synchronized (MinaServer.class) {
				if (_MinaServer == null) {
					_MinaServer = new MinaServer();
				}
			}
		}
		return _MinaServer;
	}

	public boolean connect(FBConfigureProto.FBConfigure configure) throws IOException {
		IoEventType[] eventTypes = new IoEventType[]{
				IoEventType.MESSAGE_RECEIVED,
				IoEventType.MESSAGE_SENT
		};
//        acceptor = new NioSocketAcceptor(FBConfigure.MAX_PROCESSOR)
		int maxConnection = configure.getServerMaxConnection();
		int minBufferSize = configure.getServerMinReadBufferSize();
		int maxBufferSize = configure.getServerMaxReadBufferSize();

		acceptor = new NioSocketAcceptor();
		acceptor.addListener(new ServiceListener());
		acceptor.getSessionConfig().setMinReadBufferSize(minBufferSize);
		acceptor.getSessionConfig().setMaxReadBufferSize(maxBufferSize);
		acceptor.getFilterChain().addLast("throttle", new ConnectionThrottleFilter(2000));
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new FBCodecFactory(maxBufferSize)));
		acceptor.getFilterChain().addLast("executor", new ExecutorFilter(eventTypes));
		//日志
		LoggingFilter filter = new LoggingFilter();
		filter.setExceptionCaughtLogLevel(LogLevel.DEBUG);
		filter.setMessageReceivedLogLevel(LogLevel.DEBUG);
		filter.setMessageSentLogLevel(LogLevel.DEBUG);
		filter.setSessionClosedLogLevel(LogLevel.DEBUG);
		filter.setSessionCreatedLogLevel(LogLevel.DEBUG);
		filter.setSessionIdleLogLevel(LogLevel.DEBUG);
		filter.setSessionOpenedLogLevel(LogLevel.DEBUG);
		acceptor.getFilterChain().addLast("logger", filter);
		//acceptor.setHandler(new MinaHandler(maxConnection));
		acceptor.setHandler(new WebSocketIoHandler());

		acceptor.bind(new InetSocketAddress(configure.getServerHost(), configure.getServerPort()));
		return true;
	}

	static public final String USER_KEY = "USRKEY";

	public boolean bindUserSession(String key, IoSession session) {
		if (this.userSessions.containsKey(key)) {
			return false;
		} else {
			this.userSessions.put(key, session.getId());
			session.setAttribute(USER_KEY, key);
			return true;
		}
	}

	public IoSession getUserSession(String key) {
		if (this.userSessions.containsKey(key)) {
			long sessionId = this.userSessions.get(key);
			return this.acceptor.getManagedSessions().get(sessionId);
		}
		return null;
	}

	public void unBindUserSession(IoSession session) {
		if (session.getAttribute(USER_KEY) != null) {
			String key = session.getAttribute(USER_KEY).toString();
			if (key == null || key.length() == 0) return;

			if (this.userSessions.containsKey(key)) {
				this.userSessions.remove(key);
			}
		}
	}


//    public static Date strToDate(String strDate) {
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        ParsePosition pos = new ParsePosition(0);
//        return formatter.parse(strDate, pos);
//    }
}
