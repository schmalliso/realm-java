package io.realm

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.realm.entities.MixedIndexed
import io.realm.entities.MixedNotIndexed
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.bson.types.Decimal128
import org.bson.types.ObjectId
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue



@RunWith(AndroidJUnit4::class)
class MixedTests {
    private lateinit var realmConfiguration: RealmConfiguration
    private lateinit var realm: Realm

    @Rule
    @JvmField
    val folder = TemporaryFolder()

    init {
        Realm.init(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Before
    fun setUp() {
        realmConfiguration = RealmConfiguration
                .Builder(InstrumentationRegistry.getInstrumentation().targetContext)
                .directory(folder.newFolder())
                .schema(MixedNotIndexed::class.java,
                        MixedIndexed::class.java)
                .build()

        realm = Realm.getInstance(realmConfiguration)
    }

    @After
    fun tearDown() {
        realm.close()
    }

    // Unmanaged
    @Test
    fun unmanaged_longValue() {
        val mixed = Mixed.valueOf(10)
        assertEquals(10, mixed.asInteger())
        assertEquals(MixedType.INTEGER, mixed.type)
    }

    @Test
    fun unmanaged_booleanValue() {
        val mixed = Mixed.valueOf(true)
        assertEquals(true, mixed.asBoolean())
        assertEquals(MixedType.BOOLEAN, mixed.type)
    }

    @Test
    fun unmanaged_stringValue() {
        val mixed = Mixed.valueOf("hello world")
        assertEquals("hello world", mixed.asString())
        assertEquals(MixedType.STRING, mixed.type)
    }

    @Test
    fun unmanaged_binaryValue() {
        val mixed = Mixed.valueOf(byteArrayOf(0, 1, 0))
        assertTrue(Arrays.equals(byteArrayOf(0, 1, 0), mixed.asBinary()))
        assertEquals(MixedType.BINARY, mixed.type)
    }

    @Test
    fun unmanaged_dateValue() {
        val mixed = Mixed.valueOf(Date(10))
        assertEquals(Date(10), mixed.asDate())
        assertEquals(MixedType.DATE, mixed.type)
    }

    @Test
    fun unmanaged_decimal128Value() {
        val mixed = Mixed.valueOf(Decimal128.fromIEEE754BIDEncoding(10, 10))
        assertEquals(Decimal128.fromIEEE754BIDEncoding(10, 10), mixed.asDecimal128())
        assertEquals(MixedType.DECIMAL128, mixed.type)
    }

    @Test
    fun unmanaged_doubleValue() {
        val mixed = Mixed.valueOf(10.0)
        assertEquals(10.0, mixed.asDouble())
        assertEquals(MixedType.DOUBLE, mixed.type)
    }

    @Test
    fun unmanaged_floatValue() {
        val mixed = Mixed.valueOf(10.0f)
        assertEquals(10.0f, mixed.asFloat())
        assertEquals(MixedType.FLOAT, mixed.type)
    }

    @Test
    fun unmanaged_objectIdValue() {
        val mixed = Mixed.valueOf(ObjectId(TestHelper.generateObjectIdHexString(0)))

        assertEquals(ObjectId(TestHelper.generateObjectIdHexString(0)), mixed.asObjectId())
        assertEquals(MixedType.OBJECT_ID, mixed.type)
    }

    @Test
    fun unmanaged_null() {
        val aLong: Long? = null

        val mixed = Mixed.valueOf(aLong)

        assertTrue(mixed.isNull)
        assertEquals(MixedType.NO_TYPE, mixed.type)
    }

    // Managed
    @Test
    fun managed_longValue() {
        realm.executeTransaction {
            val mixedObject = realm.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.valueOf(10)
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertEquals(10, mixedObject?.mixed?.asInteger())
        assertEquals(MixedType.INTEGER, mixedObject?.mixed?.type)
    }

    @Test
    fun managed_booleanValue() {
        realm.executeTransaction {
            val mixedObject = realm.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.valueOf(true)
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertEquals(true, mixedObject?.mixed?.asBoolean())
        assertEquals(MixedType.BOOLEAN, mixedObject?.mixed?.type)
    }

    @Test
    fun managed_stringValue() {
        realm.executeTransaction {
            val mixedObject = realm.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.valueOf("hello world")
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertEquals("hello world", mixedObject?.mixed?.asString())
        assertEquals(MixedType.STRING, mixedObject?.mixed?.type)
    }

    @Test
    fun managed_binaryValue() {
        realm.executeTransaction {
            val mixedObject = realm.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.valueOf(byteArrayOf(0, 1, 0))
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertTrue(Arrays.equals(byteArrayOf(0, 1, 0), mixedObject?.mixed?.asBinary()))
        assertEquals(MixedType.BINARY, mixedObject?.mixed?.type)
    }

    @Test
    fun managed_dateValue() {
        realm.executeTransaction {
            val mixedObject = realm.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.valueOf(Date(10))
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertEquals(Date(10), mixedObject?.mixed?.asDate())
        assertEquals(MixedType.DATE, mixedObject?.mixed!!.type)
    }

    @Test
    fun managed_decimal128Value() {
        realm.executeTransaction {
            val mixedObject = realm.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.valueOf(Decimal128(10))
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertEquals(Decimal128(10), mixedObject!!.mixed!!.asDecimal128())
        assertEquals(MixedType.DECIMAL128, mixedObject.mixed!!.type)
    }

    @Test
    fun managed_doubleValue() {
        realm.executeTransaction {
            val mixedObject = realm.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.valueOf(10.0)
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertEquals(10.0, mixedObject!!.mixed!!.asDouble())
        assertEquals(MixedType.DOUBLE, mixedObject.mixed!!.type)
    }

    @Test
    fun managed_floatValue() {
        realm.executeTransaction {
            val mixedObject = realm.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.valueOf(10f)
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertEquals(10f, mixedObject!!.mixed!!.asFloat())
        assertEquals(MixedType.FLOAT, mixedObject.mixed!!.type)
    }

    @Test
    fun managed_objectIdValue() {
        realm.executeTransaction {
            val mixedObject = realm.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.valueOf(ObjectId(TestHelper.generateObjectIdHexString(0)))
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertEquals(ObjectId(TestHelper.generateObjectIdHexString(0)), mixedObject!!.mixed!!.asObjectId())
        assertEquals(MixedType.OBJECT_ID, mixedObject.mixed!!.type)
    }

    @Test
    fun managed_null() {
        realm.executeTransaction {
            val mixedObject = realm.createObject<MixedNotIndexed>()
            mixedObject.mixed = null
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertTrue(mixedObject!!.mixed!!.isNull)
        assertEquals(MixedType.NO_TYPE, mixedObject.mixed!!.type)
    }

    @Test
    fun managed_nullMixed() {
        realm.executeTransaction {
            val mixedObject = realm.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.nullValue()
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertTrue(mixedObject!!.mixed!!.isNull)
        assertEquals(MixedType.NO_TYPE, mixedObject.mixed!!.type)
    }
}