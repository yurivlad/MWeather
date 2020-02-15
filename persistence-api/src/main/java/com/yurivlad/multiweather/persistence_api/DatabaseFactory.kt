package com.yurivlad.multiweather.persistence_api

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yurivlad.multiweather.domainModel.model.ForecastSource
import com.yurivlad.multiweather.domainModel.model.ForecastWithDayParts
import com.yurivlad.multiweather.persistence_impl.DataBaseImpl
import com.yurivlad.multiweather.persistence_model.DatabaseDomain

/**
 *
 */
fun createForecastWithDayPartsDatabase(context: Context): DatabaseDomain<ForecastWithDayParts, ForecastSource> =
    Room.databaseBuilder(
        context,
        DataBaseImpl::class.java, "forecasts_day_parts"
    )
        .addMigrations(object : Migration(1, Int.MAX_VALUE) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("drop table ForecastForDayWithDayPartEntity")
            }
        })
        .build()