package com.diklat.tanyapakar.core.util

import com.diklat.tanyapakar.core.data.source.model.Expertise

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
}