package com.yurivlad.multiweather.persistence_impl

import androidx.room.*
import com.yurivlad.multiweather.domainModel.model.ForecastSource

/**
 *
 */
@Dao
interface ForecastWithDayPartsDao {

    @Transaction
    @Insert(
        entity = ForecastForDayWithDayPartEntity::class,
        onConflict = OnConflictStrategy.REPLACE
    )
    fun insert(values: List<ForecastForDayWithDayPartEntity>)

    @Query("SELECT * FROM ForecastForDayWithDayPartEntity WHERE forecastSource == :source")
    fun getBySource(source: ForecastSource): List<ForecastForDayWithDayPartEntity>?

    @Query("DELETE from ForecastForDayWithDayPartEntity WHERE forecastSource == :source")
    fun deleteBySource(source: ForecastSource)

}