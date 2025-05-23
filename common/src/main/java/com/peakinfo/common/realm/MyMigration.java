package com.peakinfo.common.realm;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by huy  on 2022/12/21.
 */
public class MyMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        if (oldVersion == 1) {
            schema.get("Street")
                    .addField("prepayDuration", Double.class);
            oldVersion++;
        }
        if (oldVersion == 2) {
            schema.get("Street")
                    .addField("appId", String.class,FieldAttribute.REQUIRED)
                    .addField("password", String.class,FieldAttribute.REQUIRED);
            schema.get("Street").transform(obj -> {
                obj.set("appId", "");
                obj.set("password", "");
            });
            oldVersion++;
        }
    }
}
