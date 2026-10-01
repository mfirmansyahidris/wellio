package fi.dev.wellio.data

import android.content.Context
import androidx.room.*
import fi.dev.wellio.domain.*
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val stateJson = Json { ignoreUnknownKeys = true; encodeDefaults = true }
@Entity(tableName = "wellness_state") data class StateEntity(@PrimaryKey val id: Int = 1, val payload: String)
@Dao interface StateDao {
    @Query("SELECT * FROM wellness_state WHERE id = 1") fun observe(): kotlinx.coroutines.flow.Flow<StateEntity?>
    @Query("SELECT * FROM wellness_state WHERE id = 1") suspend fun read(): StateEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun write(state: StateEntity)
}
@Database(entities = [StateEntity::class], version = 1, exportSchema = true)
abstract class WellioDatabase : RoomDatabase() {
    abstract fun state(): StateDao
    companion object {
        @Volatile private var instance: WellioDatabase? = null
        fun get(context: Context): WellioDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, WellioDatabase::class.java, "wellio.db").build().also { instance = it }
        }
    }
}
class WellioStore(private val db: WellioDatabase) {
    private fun decode(entity: StateEntity?) = entity?.let { stateJson.decodeFromString<WellnessState>(it.payload) } ?: WellnessState()
    val state = db.state().observe().map(::decode)
    suspend fun read(): WellnessState = decode(db.state().read())
    suspend fun update(transform: (WellnessState) -> WellnessState) {
        db.withTransaction { db.state().write(StateEntity(payload = stateJson.encodeToString(transform(read())))) }
    }
}
