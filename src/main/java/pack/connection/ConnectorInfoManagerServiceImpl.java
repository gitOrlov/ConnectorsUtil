package pack.connection;

import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.api.ConnectorInfoManager;
import org.identityconnectors.framework.api.ConnectorInfoManagerFactory;
import org.identityconnectors.framework.api.RemoteFrameworkConnectionInfo;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.util.ArrayList;
import java.util.List;

@Service
class ConnectorInfoManagerServiceImpl implements ConnectorInfoManagerService{
    private static final Logger LOG = LoggerFactory.getLogger(ConnectorInfoManagerServiceImpl.class);

    public ConnectorInfoManager getConnectorInfoManager() {
        String host = "10.0.14.74";
        int port = 8759;
        GuardedString key = new GuardedString("rtiamm".toCharArray());
        boolean useSsl = false;
        int timeOut = 120 * 1000;

        RemoteFrameworkConnectionInfo remote = new RemoteFrameworkConnectionInfo(host, port, key, useSsl, getTrustManagers(), timeOut);
        ConnectorInfoManagerFactory connectorInfoManagerFactory = ConnectorInfoManagerFactory.getInstance();

        ConnectorInfoManager connectorManager = null;
        try {
            connectorManager = connectorInfoManagerFactory.getRemoteManager(remote);
        } catch (ConfigurationException e) {
            LOG.error(String.valueOf(e));
        }

        return connectorManager;
    }

    private static List<TrustManager> getTrustManagers() {
        List<TrustManager> trustManagers = new ArrayList<>();
        trustManagers.add(new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }
        });
        return trustManagers;
    }
}
