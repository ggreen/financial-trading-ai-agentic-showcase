package io.cloudNativeData.research.trader;

import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.WritablePdxInstance;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MockPdxInstance implements PdxInstance {

    private final String className;
    private final Map<String, Object> fields;
    private final Set<String> identityFields;
    private final boolean isEnum;

    public MockPdxInstance(String className, Map<String, Object> fields, Set<String> identityFields, boolean isEnum) {
        this.className = className;
        this.fields = fields != null ? fields : Map.of();
        this.identityFields = identityFields != null ? identityFields : Set.of();
        this.isEnum = isEnum;
    }

    @Override
    public String getClassName() {
        return this.className;
    }

    @Override
    public boolean isEnum() {
        return this.isEnum;
    }

    @Override
    public Object getObject() {
        // In actual PDX, this would deserialize the instance back into its domain object.
        // Returning the map backing it or throwing UnsupportedOperationException is standard for a generic wrapper.
        return this.fields;
    }

    @Override
    public boolean isIdentityField(String fieldName) {
        return this.identityFields.contains(fieldName);
    }

    @Override
    public boolean hasField(String s) {
        return false;
    }

    @Override
    public List<String> getFieldNames() {
        return List.of();
    }

    @Override
    public Object getField(String fieldName) {
        return this.fields.get(fieldName);
    }

    @Override
    public WritablePdxInstance createWriter() {
      return null;
    }

    @Override
    public boolean isDeserializable() {
        return false;
    }

    // --- Standard Java Methods based on PDX contract ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PdxInstance)) return false;
        PdxInstance other = (PdxInstance) o;

        if (!Objects.equals(this.className, other.getClassName())) return false;
        if (this.isEnum != other.isEnum()) return false;

        // PDX equality typically checks equality of identity fields, or all fields if none are marked.
        Set<String> fieldsToCheck = this.identityFields.isEmpty() ? this.fields.keySet() : this.identityFields;

        for (String field : fieldsToCheck) {
            if (!Objects.equals(this.getField(field), other.getField(field))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        // Hash code based on class name and relevant fields
        int result = Objects.hash(className, isEnum);
        Set<String> fieldsToHash = this.identityFields.isEmpty() ? this.fields.keySet() : this.identityFields;

        for (String field : fieldsToHash) {
            result = 31 * result + Objects.hashCode(this.getField(field));
        }
        return result;
    }

    @Override
    public String toString() {
        return "MapPdxInstance[className=" + className + ", fields=" + fields + "]";
    }
}