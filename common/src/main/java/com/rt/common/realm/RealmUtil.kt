package com.rt.common.realm

import io.realm.*

class RealmUtil {
    private val versionCode = 1
    private var transaction: RealmAsyncTask? = null
    private val config: RealmConfiguration = RealmConfiguration.Builder() // 文件名
        .name("rt.realm") // 版本号
        .schemaVersion(versionCode.toLong())
        .allowWritesOnUiThread(true)
        .allowQueriesOnUiThread(true)
        .migration(MyMigration())
        .build()
    val realm: Realm
        get() = Realm.getDefaultInstance()
    private val realmTask: RealmAsyncTask?
        private get() = transaction

    companion object {
        private var realmUtil: RealmUtil? = null

        @get:Synchronized
        val instance: RealmUtil?
            get() {
                if (realmUtil == null) realmUtil = RealmUtil()
                return realmUtil
            }
        val instanceBlock: RealmUtil?
            get() {
                if (realmUtil == null) synchronized(RealmUtil::class.java) {
                    if (realmUtil == null) realmUtil = RealmUtil()
                }
                return realmUtil
            }
    }

    init {
        Realm.setDefaultConfiguration(config)
    }

    fun addRealmAsync(realmObject: RealmObject) {
        transaction =
            realm.executeTransactionAsync { realm -> realm.copyToRealmOrUpdate(realmObject) }
    }

    fun addRealm(realmObject: RealmObject) {
        realm.executeTransaction { realm -> realm.copyToRealmOrUpdate(realmObject) }
    }

    fun addRealmAsyncList(realmObjectList: List<RealmObject>) {
        transaction =
            realm.executeTransactionAsync { realm -> realm.copyToRealmOrUpdate(realmObjectList) }
    }

    fun deleteRealmAsync(realmObject: RealmObject) {
        transaction = realm.executeTransactionAsync { realm ->
            realmObject.deleteFromRealm()
        }
    }

    fun deleteRealmAsync(realmList: RealmResults<RealmObject>) {
        realm.executeTransactionAsync {
            realmList.deleteAllFromRealm()
        }
    }

}