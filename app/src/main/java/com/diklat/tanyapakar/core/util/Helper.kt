package com.diklat.tanyapakar.core.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.diklat.tanyapakar.core.data.source.model.Expertise
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object Helper {
    fun convertExpertiseNamesToString(idList: List<String>, expertiseList: List<Expertise>): String {
        val matchingExpertiseNames = expertiseList.filter { expertise -> idList.contains(expertise.id_expertise) }
            .map { it.name?.uppercase() } // Capitalize each name
            .joinToString(", ") // Separate with comma
        return matchingExpertiseNames
    }
    fun convertExpertiseNamesToListExpertise(idList: List<String>, expertiseList: List<Expertise>): MutableList<Expertise> {
        val matchingExpertiseNames = expertiseList.filter { expertise -> idList.contains(expertise.id_expertise) }
        return matchingExpertiseNames.toMutableList()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertTimestampToString(timestamp: Long): String {
        val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        return dateTime.format(formatter)
    }
}