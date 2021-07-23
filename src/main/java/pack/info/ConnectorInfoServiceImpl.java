package pack.info;

import org.identityconnectors.framework.api.*;
import org.identityconnectors.framework.api.operations.ValidateApiOp;
import org.identityconnectors.framework.common.objects.AttributeInfo;
import org.identityconnectors.framework.common.objects.ObjectClassInfo;
import org.identityconnectors.framework.common.objects.Schema;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ConnectorInfoServiceImpl implements ConnectorInfoService {
    @Override
    public void printConnectorInfo(ConnectorInfo connectorInfo) {
        System.out.println("- connectorDisplayName: " + connectorInfo.getConnectorDisplayName());
        System.out.println("  connectorCategory: " + connectorInfo.getConnectorCategory());
        System.out.println("  connectorKey:");
        System.out.println("    bundleName: " + connectorInfo.getConnectorKey().getBundleName());
        System.out.println("    bundleVersion: " + connectorInfo.getConnectorKey().getBundleVersion());
        System.out.println("    connectorName: " + connectorInfo.getConnectorKey().getConnectorName() + "\n");
    }

    @Override
    public void printConnectorProperties(List<String> propertyNames, ConfigurationProperties properties) {
        for (String propertyName : propertyNames) {
            System.out.println("  - propertyName: " + propertyName);
            ConfigurationProperty property = properties.getProperty(propertyName);
            System.out.println("    type: " + property.getType().toString());
            System.out.println("    isRequired: " + property.isRequired());
            System.out.println("    displayName: '" + property.getDisplayName(propertyName) + "'");
            System.out.println("    group: '" + property.getGroup(propertyName) + "'");
            System.out.println("    helpMessage: '" + property.getHelpMessage(propertyName) + "'" + "\n");
        }
    }

    @Override
    public void printObjectInfo(APIConfiguration apiConfig) {
        ConnectorFacade connectorFacade = ConnectorFacadeFactory.getInstance().newInstance(apiConfig);

        if (apiConfig.getSupportedOperations().contains(ValidateApiOp.class))
            connectorFacade.validate();

        System.out.println("  Объекты:");

        Schema schema = connectorFacade.schema();
        Set<ObjectClassInfo> objectClasses = schema.getObjectClassInfo();

        for (ObjectClassInfo oci : objectClasses) {
            System.out.println("  - " + oci.getType());
            Set<AttributeInfo> attributeInfos = oci.getAttributeInfo();
            System.out.println("    Аттрибуты:");
            for (AttributeInfo attributeInfo : attributeInfos) {
                System.out.println("    - name:" + attributeInfo.getName());
                System.out.println("      type:" + attributeInfo.getType());
                System.out.println("      nativeName:" + attributeInfo.getNativeName());
                System.out.println("      isRequired:" + attributeInfo.isRequired());
            }
        }
    }

    //    private void justMethod(String[] args, List<String> propertyNames, ConfigurationProperties properties) {
//        String configurationFilePath = args[5];
//        String resourceClass = args[6];
//
//        BaseConfiguration baseConfiguration = null;
//
//        try {
//            ConfigurationServiceImpl cs = new ConfigurationServiceImpl(Paths.get(configurationFilePath).getParent().toString() + "/configuration_service.conf");
//            baseConfiguration = cs.createConfigurationObject(resourceClass);
//            cs.loadConfiguration(configurationFilePath, baseConfiguration);
//        } catch (IOException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        if (isNull(baseConfiguration))
//            return;
//
//        BaseConfiguration connectorProperties = (BaseConfiguration) baseConfiguration.getProperty("properties");
//
//        for (String propertyName : propertyNames) {
//            ConfigurationProperty property = properties.getProperty(propertyName);
//            Object configParamValue = null;
//
//            try {
//                if (property.getType() == GuardedString.class) {
//                    String str = (String) connectorProperties.getProperty(propertyName);
//                    configParamValue = new GuardedString(str.toCharArray());
//                } else {
//                    configParamValue = connectorProperties.getProperty(propertyName);
//                }
//            } catch (RuntimeException e) {
//                System.out.println("Свойство: " + propertyName);
//            }
//
//            if (configParamValue != null) {
//                properties.setPropertyValue(propertyName, configParamValue);
//            } else if (property.isRequired()) {
//                throw new RuntimeException("Не задано свойство: " + propertyName);
//            }
//        }
//    }
}
