package com.rt.common.realm

import com.rt.base.bean.Street
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

    /**
     *  选中streetList
     */
    fun updateStreetChoosed(street: Street) {
        realm.executeTransaction {
            street.ischeck = true
        }
    }

    fun updateCurrentStreet(new: Street, old: Street?) {
        realm.executeTransaction {
            if (old != null) {
                old.isCurrent = false
            }
            new.isCurrent = true
        }
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

    fun findAllStreetList(): List<Street> {
        return realm.where(Street::class.java).findAll()
    }


    fun findCheckedStreetList(): List<Street> {
        return realm.where(Street::class.java).equalTo("ischeck", true).findAll()
    }

    fun findCurrentStreet(): Street? {
        return realm.where(Street::class.java).equalTo("isCurrent", true).findAll().first()
    }

    /**
     *删除所有street
     */
    fun deleteAllStreet() {
        realm.executeTransaction {
            it.delete(Street::class.java)
        }
    }
}