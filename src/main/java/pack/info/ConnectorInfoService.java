package pack.info;

import org.identityconnectors.framework.api.APIConfiguration;
import org.identityconnectors.framework.api.ConfigurationProperties;
import org.identityconnectors.framework.api.ConnectorInfo;

import java.util.List;

public interface ConnectorInfoService {
    void printConnectorInfo(ConnectorInfo connectorInfo);

    void printConnectorProperties(List<String> propertyNames, ConfigurationProperties properties);

    void printObjectInfo(APIConfiguration apiConfig);
}
