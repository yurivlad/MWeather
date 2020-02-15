package com.yurivlad.multiweather.persistence_impl

import androidx.room.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.yurivlad.multiweather.domainModel.model.*
import org.koin.core.KoinComponent
import org.koin.core.get
import java.util.*

/**
 *
 */
@Entity(indices = [Index("forecastSource")])
@TypeConverters(ForecastForDayWithDayPartEntityConverters::class)
class ForecastForDayWithDayPartEntity(
    @PrimaryKey var id: String,
    var forecastSource: ForecastSource,
    var date: Date,
    @Embedded(
        prefix = "night_"
    )
    var nightForecast: ForecastForDayPartEmbedded?,
    @Embedded(
        prefix = "morning_"
    )
    var morningForecast: ForecastForDayPartEmbedded?,
    @Embedded(
        prefix = "day_"
    )
    var dayForecast: ForecastForDayPartEmbedded?,
    @Embedded(
        prefix = "evening_"
    )
    var eveningForecast: ForecastForDayPartEmbedded?
) {

    companion object {
        fun convertFromDayPart(
            part: ForecastForDayPart?
        ): ForecastForDayPartEmbedded? = part?.let {
            ForecastForDayPartEmbedded(
                part.dayPart,
                part.summary,
                part.temperature,
                part.windMetersPerSecond,
                part.weatherList
            )
        }


        fun convertToForecastForDayPart(from: ForecastForDayPartEmbedded?) =
            from?.let {
                ForecastForDayPart(
                    from.dayPart,
                    from.summary,
                    from.temperature,
                    from.windMetersPerSecond,
                    from.weatherList
                )
            }
    }
}

class ForecastForDayPartEmbedded(
    var dayPart: DayPart,
    var summary: String,
    var temperature: ForecastTemperature,
    var windMetersPerSecond: ForecastWind,
    var weatherList: WeatherList
)

class ForecastForDayWithDayPartEntityConverters : KoinComponent {
    @TypeConverter
    fun fromStringDayPart(string: String): DayPart = DayPart.valueOf(string)

    @TypeConverter
    fun toStringDayPart(source: DayPart): String = source.name

    @TypeConverter
    fun fromStringForecastTemperature(string: String): ForecastTemperature {
        val values = string.split("#")
        return ForecastTemperature(values[0].toInt(), values[1].toInt())
    }

    @TypeConverter
    fun toStringForecastTemperature(source: ForecastTemperature): String =
        "${source.from}#${source.to}"

    @TypeConverter
    fun fromStringForecastWind(string: String): ForecastWind {
        val values = string.split("#")
        return ForecastWind(values[0].toDouble(), values[1].toDouble())
    }

    @TypeConverter
    fun toStringForecastWind(source: ForecastWind): String = "${source.from}#${source.to}"

    @TypeConverter
    fun fromStringWeatherList(string: String): WeatherList {
        val type = Types.newParameterizedType(Set::class.java, WeatherType::class.java)
        val weatherSet = get<Moshi>().adapter<Set<WeatherType>>(type).fromJson(string)!!

        return WeatherList(weatherSet)
    }

    @TypeConverter
    fun toStringWeatherList(source: WeatherList): String {
        val type = Types.newParameterizedType(Set::class.java, WeatherType::class.java)

        return get<Moshi>().adapter<Set<WeatherType>>(type).toJson(source.list)
    }


    @TypeConverter
    fun fromStringForecastSource(string: String): ForecastSource =
        ForecastSource.valueOf(string)

    @TypeConverter
    fun toStringForecastSource(source: ForecastSource): String = source.name

    @TypeConverter
    fun fromLongDate(long: Long): Date = Date(long)

    @TypeConverter
    fun toLongDate(date: Date): Long = date.time
}