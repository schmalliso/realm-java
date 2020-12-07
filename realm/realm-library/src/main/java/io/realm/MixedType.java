package io.realm;


import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import java.util.Date;

import javax.annotation.Nullable;

import static io.realm.RealmFieldTypeConstants.MAX_CORE_TYPE_VALUE;


public enum MixedType {
    INTEGER(RealmFieldType.INTEGER, Long.class),
    BOOLEAN(RealmFieldType.BOOLEAN, Boolean.class),
    STRING(RealmFieldType.STRING, String.class),
    BINARY(RealmFieldType.BINARY, Byte[].class),
    DATE(RealmFieldType.DATE, Date.class),
    FLOAT(RealmFieldType.FLOAT, Float.class),
    DOUBLE(RealmFieldType.DOUBLE, Double.class),
    DECIMAL128(RealmFieldType.DECIMAL128, Decimal128.class),
    OBJECT_ID(RealmFieldType.OBJECT_ID, ObjectId.class),
    OBJECT(RealmFieldType.TYPED_LINK, RealmModel.class),
    NULL(null, null);

    private static final MixedType[] realmFieldToMixedTypeMap = new MixedType[MAX_CORE_TYPE_VALUE + 1];

    static {
        for (MixedType mixedType : values()) {
            if (mixedType == NULL) { continue; }

            final int nativeValue = mixedType.realmFieldType.getNativeValue();
            realmFieldToMixedTypeMap[nativeValue] = mixedType;
        }
    }

    public static MixedType fromNativeValue(int realmFieldType) {
        if (realmFieldType == -1) { return NULL; }

        return realmFieldToMixedTypeMap[realmFieldType];
    }

    private final Class<?> clazz;
    private final RealmFieldType realmFieldType;

    MixedType(@Nullable RealmFieldType realmFieldType, @Nullable Class<?> clazz) {
        this.realmFieldType = realmFieldType;
        this.clazz = clazz;
    }

    public Class<?> getTypedClass() {
        return clazz;
    }
}