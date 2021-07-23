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

import java.util.HashSet;
import java.util.Set;

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
        String restConnectorBundleName = "net.tirasa.connid.bundles.rest";
        String restConnectorBundleVersion = "1.0.6-SNAPSHOT";
        String restConnectorName = "net.tirasa.connid.bundles.rest.RESTConnector";
        ConnectorKey restConnectorKey = new ConnectorKey(restConnectorBundleName, restConnectorBundleVersion, restConnectorName);

        ConnectorInfo restConnectorInfo = connectorInfoManager.findConnectorInfo(restConnectorKey);
        APIConfiguration apiConfig = restConnectorInfo.createDefaultAPIConfiguration();
        ConfigurationProperties restConnectorProperties = apiConfig.getConfigurationProperties();

        setConnectorProperties(restConnectorProperties);

        ConnectorFacade connectorFacade = ConnectorFacadeFactory.getInstance().newInstance(apiConfig);
        OperationOptions operationOptions = new OperationOptionsBuilder().build();

        connectorFacade.validate();
        Schema schema = connectorFacade.schema();

        Uid uid = connectorFacade.authenticate(ObjectClass.ACCOUNT, "admin", new GuardedString("projectRSIAM2015".toCharArray()), operationOptions);

        Uid createUid = create(connectorFacade, operationOptions);

        connectorFacade.delete(ObjectClass.ACCOUNT, createUid, operationOptions);

//        connectorFacade.test();

//        SearchResult searchResult = connectorFacade.search(ObjectClass.ACCOUNT, filter, resultsHandler, operationOptions);

//        Uid getUid = new Uid("jGPfuzGRTevZSp4Ce");
//        ConnectorObject user = connectorFacade.getObject(ObjectClass.ACCOUNT, getUid, operationOptions);
//
//        connectorFacade.sync();

//        Uid updateUid = connectorFacade.update(ObjectClass.ACCOUNT, uid, attrs, operationOptions);
//        connectorFacade.delete(ObjectClass.ACCOUNT, uid, operationOptions);
    }

    private Uid create(ConnectorFacade connectorFacade, OperationOptions operationOptions) {
        Set<Attribute> attributes = new HashSet<>();

        attributes.add(AttributeBuilder.build("name", "Vania"));
        attributes.add(AttributeBuilder.build("email", "Vania@mail.ru"));
        attributes.add(AttributeBuilder.build("password", "Vania"));
        attributes.add(AttributeBuilder.build("username", "VaniaVania"));

        return connectorFacade.create(ObjectClass.ACCOUNT, attributes, operationOptions);
    }

    void setConnectorProperties(ConfigurationProperties restConnectorProperties) {
        String path = "/opt/connid-connector-server/scripts/";
        restConnectorProperties.setPropertyValue("baseAddress", "http://10.0.14.54:3000");
        restConnectorProperties.setPropertyValue("reloadScriptOnExecution", true);

        restConnectorProperties.setPropertyValue("schemaScriptFileName", path + "SchemaScript.groovy");
        restConnectorProperties.setPropertyValue("authenticateScriptFileName", path + "AuthenticateScript.groovy");
        restConnectorProperties.setPropertyValue("createScriptFileName", path + "CreateScript.groovy");
        restConnectorProperties.setPropertyValue("deleteScriptFileName", path + "DeleteScript.groovy");

//        restConnectorProperties.setPropertyValue("cliendId", "ANrfMv9N4B7dHJGcg"); // эти параметры вообще не передаются!
//        restConnectorProperties.setPropertyValue("clientSecret", "WmmXhiyxZYEb0P4jfNC4m4b7Ff4KPwiIZM9ELl06cgZ");

//        restConnectorProperties.setPropertyValue("contentType", "application/x-www-form-urlencoded");// почему то устанавливает заголовок Accept!
//        restConnectorProperties.setPropertyValue("accept", "application/x-www-form-urlencoded");// почему то устанавливает заголовок contentType!

//        restConnectorProperties.setPropertyValue("username", "admin");
//        restConnectorProperties.setPropertyValue("password", new GuardedString("projectRSIAM2015".toCharArray()));

//        restConnectorProperties.setPropertyValue("searchScriptFileName", path + "SearchScript.groovy");
//        restConnectorProperties.setPropertyValue("testScriptFileName", path + "TestScript.groovy");

//        restConnectorProperties.setPropertyValue("schemaScript", "SchemaScript");
//        restConnectorProperties.setPropertyValue("syncScriptFileName", "");
//        restConnectorProperties.setPropertyValue("resolveUsernameScriptFileName", "");
//        restConnectorProperties.setPropertyValue("updateScriptFileName", "");

//        restConnectorProperties.setPropertyValue("testScript", "");
//        restConnectorProperties.setPropertyValue("syncScript", "");
//        restConnectorProperties.setPropertyValue("resolveUsernameScript", "");
//        restConnectorProperties.setPropertyValue("authenticateScript", "");
//        restConnectorProperties.setPropertyValue("searchScript", "");
//        restConnectorProperties.setPropertyValue("deleteScript", "");
//        restConnectorProperties.setPropertyValue("updateScript", "");
//        restConnectorProperties.setPropertyValue("createScript", "");
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
}

