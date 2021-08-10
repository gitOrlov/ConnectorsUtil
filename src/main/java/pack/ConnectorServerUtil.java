package pack;

import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.api.*;
import org.identityconnectors.framework.common.objects.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pack.connection.ConnectorInfoManagerService;
import pack.info.ConnectorInfoService;

import java.util.*;

import static java.util.Objects.isNull;

@SpringBootApplication
public class ConnectorServerUtil implements CommandLineRunner {

    @Autowired
    ConnectorInfoManagerService connectorInfoManagerService;
    @Autowired
    ConnectorInfoService connectorInfoService;

    public static void main(String[] args) {
        SpringApplication.run(ConnectorServerUtil.class, args);
    }

    @Override
    public void run(String... args) {
        ConnectorInfoManager connectorInfoManager = connectorInfoManagerService.getConnectorInfoManager();
        if (isNull(connectorInfoManager)) return;

        workWithRestConnector(connectorInfoManager);
    }

    private void workWithRestConnector(ConnectorInfoManager connectorInfoManager) {
        String restConnectorBundleName = "ConnIdRESTConnector";
        String restConnectorBundleVersion = "1.0-SNAPSHOT";
        String restConnectorName = "ru.rtiam.connectors.rest.RESTConnector";
        ConnectorKey restConnectorKey = new ConnectorKey(restConnectorBundleName, restConnectorBundleVersion, restConnectorName);

        ConnectorInfo restConnectorInfo = connectorInfoManager.findConnectorInfo(restConnectorKey);
        APIConfiguration apiConfig = restConnectorInfo.createDefaultAPIConfiguration();
        ConfigurationProperties restConnectorProperties = apiConfig.getConfigurationProperties();

        setConnectorProperties(restConnectorProperties);

        ConnectorFacade connectorFacade = ConnectorFacadeFactory.getInstance().newInstance(apiConfig);
        OperationOptions operationOptions = new OperationOptionsBuilder().build();

        connectorFacade.validate();

        Schema schema = connectorFacade.schema();

        connectorFacade.test();

        ConnectorObject user = connectorFacade.getObject(ObjectClass.ACCOUNT, new Uid("jGPfuzGRTevZSp4Ce"), operationOptions);

        Uid createUid = create(connectorFacade, operationOptions);

        Uid updateUid = update(connectorFacade, createUid, operationOptions);

        connectorFacade.delete(ObjectClass.ACCOUNT, createUid, operationOptions);

//        SearchHandler handler = handler = new SearchHandler();
//        Filter filter = FilterBuilder.equalTo(AttributeBuilder.build("".split("=")[0].trim(), "".split("=")[1].trim()));
//
//        SearchResult searchResult = connectorFacade.search(ObjectClass.ACCOUNT, filter, handler, operationOptions);
//        List<ConnectorObject> connectorObjects = handler.getAllResult();

//        connectorFacade.sync();
    }

    private Uid create(ConnectorFacade connectorFacade, OperationOptions operationOptions) {
        Set<Attribute> attributes = new HashSet<>();

        attributes.add(AttributeBuilder.build("name", "Vania"));
        attributes.add(AttributeBuilder.build("email", "Vania@mail.ru"));
        attributes.add(AttributeBuilder.build("password", "Vania"));
        attributes.add(AttributeBuilder.build("username", "VaniaVania"));
        attributes.add(AttributeBuilder.build("roles", new String[]{"owner"}));

        return connectorFacade.create(ObjectClass.ACCOUNT, attributes, operationOptions);
    }

    private Uid update(ConnectorFacade connectorFacade, Uid uid, OperationOptions operationOptions) {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(AttributeBuilder.build("name", "Ivan"));

        return connectorFacade.update(ObjectClass.ACCOUNT, uid, attributes, operationOptions);
    }

    void setConnectorProperties(ConfigurationProperties restConnectorProperties) {
        String path = "/opt/connid-connector-server-1.5.1.0/scripts/";
        restConnectorProperties.setPropertyValue("baseAddress", "http://10.0.14.54:3000");
        restConnectorProperties.setPropertyValue("reloadScriptOnExecution", true);

        restConnectorProperties.setPropertyValue("schemaScriptFileName", path + "SchemaScript.groovy");
        restConnectorProperties.setPropertyValue("authenticateScriptFileName", path + "AuthenticateScript.groovy");
        restConnectorProperties.setPropertyValue("createScriptFileName", path + "CreateScript.groovy");
        restConnectorProperties.setPropertyValue("deleteScriptFileName", path + "DeleteScript.groovy");
        restConnectorProperties.setPropertyValue("searchScriptFileName", path + "SearchScript.groovy");
        restConnectorProperties.setPropertyValue("updateScriptFileName", path + "UpdateScript.groovy");
        restConnectorProperties.setPropertyValue("initScriptFileName", path + "InitScript.groovy");

        restConnectorProperties.setPropertyValue("username", "admin");
        restConnectorProperties.setPropertyValue("password", new GuardedString("projectRSIAM2015".toCharArray()));

//        restConnectorProperties.setPropertyValue("testScriptFileName", path + "TestScript.groovy");
//        restConnectorProperties.setPropertyValue("syncScriptFileName", "");
//        restConnectorProperties.setPropertyValue("resolveUsernameScriptFileName", "");

    }
//    private void printAllConnectorInfos(ConnectorInfoManager connectorInfoManager) {
//        List<ConnectorInfo> connectorInfos = connectorInfoManager.getConnectorInfos();
//
//        for (ConnectorInfo connectorInfo : connectorInfos) {
//            System.out.println("Параметры коннектора:");
//            connectorInfoService.printConnectorInfo(connectorInfo);
//            APIConfiguration apiConfig = connectorInfo.createDefaultAPIConfiguration();
//            ConfigurationProperties properties = apiConfig.getConfigurationProperties();
//            List<String> propertyNames = properties.getPropertyNames();
//
//            System.out.println("  Свойства:");
//            connectorInfoService.printConnectorProperties(propertyNames, properties);
//            justMethod(args, propertyNames, properties);//set config prop
//            connectorInfoService.printObjectInfo(apiConfig);
//        }
//    }

    public class SearchHandler implements ResultsHandler {

        private final List<ConnectorObject> validData = new ArrayList<>();
        private final Map<ConnectorObject, String> failedData = new HashMap<>();

        public List<ConnectorObject> getAllResult() {
            return validData;
        }

        public Map<ConnectorObject, String> getFailedResults() {
            return failedData;
        }

        protected void failed(ConnectorObject obj, String cause) {
            failedData.put(obj, cause);
        }

        @Override
        public boolean handle(ConnectorObject connectorObject) {
            if (connectorObject != null) {
                validData.add(connectorObject);
            }
            return true;
        }
    }
}

