package com.yurivlad.multiweather.persistence_impl

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yurivlad.multiweather.domainModel.model.ForecastForDayWithDayParts
import com.yurivlad.multiweather.domainModel.model.ForecastSource
import com.yurivlad.multiweather.domainModel.model.ForecastWithDayParts
import com.yurivlad.multiweather.persistence_model.ForecastForDayWithDayPartsDatabaseDomain
import trikita.log.Log
import java.util.*

/**
 *
 */
@Database(
    entities = [ForecastForDayWithDayPartEntity::class],
    version = 1
)
@TypeConverters(ForecastForDayWithDayPartEntityConverters::class)
abstract class DataBaseImpl : RoomDatabase(), ForecastForDayWithDayPartsDatabaseDomain {
    abstract val forecastWithDayPartsDao: ForecastWithDayPartsDao

    override suspend fun put(item: ForecastWithDayParts) {
        try {
            forecastWithDayPartsDao.insert(item.map { forecastDomain ->
                ForecastForDayWithDayPartEntity(
                    forecastDomain.id,
                    item.source,
                    forecastDomain.date,
                    ForecastForDayWithDayPartEntity.convertFromDayPart(forecastDomain.nightForecast),
                    ForecastForDayWithDayPartEntity.convertFromDayPart(forecastDomain.morningForecast),
                    ForecastForDayWithDayPartEntity.convertFromDayPart(forecastDomain.dayForecast),
                    ForecastForDayWithDayPartEntity.convertFromDayPart(forecastDomain.eveningForecast)
                )
            })
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.e(e)
        }
    }

    override suspend fun get(id: ForecastSource): ForecastWithDayParts? = try {
        val forecasts =
            forecastWithDayPartsDao.getBySource(id)?.sortedBy { it.date.time }

        if (forecasts.isNullOrEmpty()) null
        else ForecastWithDayParts(
            id,
            forecasts.firstOrNull()?.date ?: Date(0),
            forecasts.lastOrNull()?.date ?: Date(0),
            forecasts.map { forecastEntity ->
                ForecastForDayWithDayParts(
                    forecastEntity.id,
                    forecastEntity.date,
                    ForecastForDayWithDayPartEntity.convertToForecastForDayPart(forecastEntity.nightForecast),
                    ForecastForDayWithDayPartEntity.convertToForecastForDayPart(forecastEntity.morningForecast),
                    ForecastForDayWithDayPartEntity.convertToForecastForDayPart(forecastEntity.dayForecast),
                    ForecastForDayWithDayPartEntity.convertToForecastForDayPart(forecastEntity.eveningForecast)
                )
            }
        )
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e(e)
        null
    }

    override suspend fun remove(id: ForecastSource) {
        try {
            forecastWithDayPartsDao.deleteBySource(id)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.e(e)
        }
    }
}