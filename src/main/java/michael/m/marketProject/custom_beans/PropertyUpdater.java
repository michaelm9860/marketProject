package michael.m.marketProject.custom_beans;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class PropertyUpdater {
    public void updateNonNullProperties(Object source, Object target) {
        BeanWrapper src = new BeanWrapperImpl(source);
        BeanWrapper trg = new BeanWrapperImpl(target);

        for (java.beans.PropertyDescriptor pd : src.getPropertyDescriptors()) {
            String propertyName = pd.getName();
            Object srcValue = src.getPropertyValue(propertyName);

            // Check if the source value is not null and the target has a writable property with the same name and type compatibility
            if (srcValue != null && trg.isWritableProperty(propertyName)) {
                Class<?> sourceType = src.getPropertyType(propertyName);
                Class<?> targetType = trg.getPropertyType(propertyName);
                assert sourceType != null;
                if (sourceType.equals(targetType)) {
                    trg.setPropertyValue(propertyName, srcValue);
                }
            }
        }
    }
}
