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
}
