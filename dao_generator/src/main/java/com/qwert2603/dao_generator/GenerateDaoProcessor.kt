package com.qwert2603.dao_generator

import java.io.File
import java.io.FileWriter
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("com.qwert2603.dao_generator.GenerateDao")
class GenerateDaoProcessor : AbstractProcessor() {

    private var done = false

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (done) return true
        done = true

        val generatedPackage = processingEnv.options["daoGenerator.package"]!!

        fun getDir(): File {
            var file = File(processingEnv.options["kapt.kotlin.generated"]!!)
            while (file.name != "app") {
                file = file.parentFile
            }
            file = File(file.path, "/src/main/java/${generatedPackage.replace('.', '/')}")
            file.mkdirs()
            return file
        }

        val packageFile = getDir()


        roundEnv.getElementsAnnotatedWith(GenerateDao::class.java).forEach { element ->
            element as TypeElement

            val fileName = "${element.simpleName}Dao.kt"
            FileWriter(File(packageFile, fileName)).use { fileWriter ->
                val daoVariableName = element.simpleName.toString()
                        .let { name -> name[0].toLowerCase() + name.drop(1) + "Dao" }

                val generateDaoAnnotation = element.getAnnotation(GenerateDao::class.java)

                val searchWhereString = generateDaoAnnotation.filters
                        .map { "${it.fieldName} = :${it.fieldName}" }
                        .let {
                            if (generateDaoAnnotation.searchField.isEmpty()) {
                                it
                            } else {
                                it + "${generateDaoAnnotation.searchField} LIKE '%' || :search || '%'"
                            }
                        }
                        .let {
                            if (it.isEmpty()) {
                                ""
                            } else {
                                "WHERE ${it.reduce { acc, s -> "$acc AND $s" }}"
                            }
                        }

                val deleteWhereString = generateDaoAnnotation.filters
                        .map { "${it.fieldName} = :${it.fieldName}" }
                        .let {
                            if (it.isEmpty()) {
                                ""
                            } else {
                                "WHERE ${it.reduce { acc, s -> "$acc AND $s" }}"
                            }
                        }

                val searchParamsString = generateDaoAnnotation.filters
                        .map { "${it.fieldName}: ${it.filterType.typeString}" }
                        .let {
                            if (generateDaoAnnotation.searchField.isEmpty()) {
                                it
                            } else {
                                it + "search: String"
                            }
                        }
                        .let {
                            if (it.isEmpty()) {
                                ""
                            } else {
                                it.reduce { acc, s -> "$acc, $s" }
                            }
                        }

                val deleteParamsString = generateDaoAnnotation.filters
                        .map { "${it.fieldName}: ${it.filterType.typeString}" }
                        .let {
                            if (it.isEmpty()) {
                                ""
                            } else {
                                it.reduce { acc, s -> "$acc, $s" }
                            }
                        }

                val constructorParamsString = generateDaoAnnotation.filters
                        .map { "private val ${it.fieldName}: ${it.filterType.typeString}, " }
                        .let {
                            if (it.isEmpty()) {
                                ""
                            } else {
                                it.reduce { acc, s -> "$acc$s" }
                            }
                        }

                val searchValuesString = generateDaoAnnotation.filters
                        .map { it.fieldName }
                        .let {
                            if (generateDaoAnnotation.searchField.isEmpty()) {
                                it
                            } else {
                                it + "search"
                            }
                        }
                        .let {
                            if (it.isEmpty()) {
                                ""
                            } else {
                                it.reduce { acc, s -> "$acc, $s" }
                            }
                        }

                val deleteValuesString = generateDaoAnnotation.filters
                        .map { it.fieldName }
                        .let {
                            if (it.isEmpty()) {
                                ""
                            } else {
                                it.reduce { acc, s -> "$acc, $s" }
                            }
                        }

                val constructorValuesString = generateDaoAnnotation.filters
                        .map { "${it.fieldName}, " }
                        .let {
                            if (it.isEmpty()) {
                                ""
                            } else {
                                it.reduce { acc, s -> "$acc$s" }
                            }
                        }

                fileWriter.write("""
// this file is auto-generated by com.qwert2603.dao_generator.GenerateDaoProcessor
package $generatedPackage

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.qwert2603.crmit_android.db.DaoInterface
import ${element.qualifiedName}

@Dao
interface ${element.simpleName}Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addItems(items: List<${element.simpleName}>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveItem(item: ${element.simpleName})

    @Query("SELECT * FROM ${element.simpleName} WHERE id = :itemId")
    fun getItem(itemId: Long): ${element.simpleName}?

    @Query(
        " SELECT *" +
        " FROM ${element.simpleName}" +
        " $searchWhereString" +
        " ORDER BY ${generateDaoAnnotation.orderBy}" +
        " LIMIT :count" +
        " OFFSET :offset"
    )
    fun getItems($searchParamsString, offset: Int, count: Int): List<${element.simpleName}>

    @Query("DELETE FROM ${element.simpleName} $deleteWhereString")
    fun deleteAllItems($deleteParamsString)

    @Query("DELETE FROM ${element.simpleName}")
    fun clearTable()
}

fun ${element.simpleName}Dao.wrap($deleteParamsString): DaoInterface<${element.simpleName}> = ${element.simpleName}DaoWrapper($constructorValuesString this)

private class ${element.simpleName}DaoWrapper($constructorParamsString private val $daoVariableName: ${element.simpleName}Dao) : DaoInterface<${element.simpleName}> {
    override fun addItems(items: List<${element.simpleName}>) = $daoVariableName.addItems(items)
    override fun saveItem(item: ${element.simpleName}) = $daoVariableName.saveItem(item)
    override fun getItem(itemId: Long): ${element.simpleName}? = $daoVariableName.getItem(itemId)
    override fun getItems(search: String, offset: Int, count: Int): List<${element.simpleName}> = $daoVariableName.getItems($searchValuesString, offset, count)
    override fun deleteAllItems() = $daoVariableName.deleteAllItems($deleteValuesString)
}
                """.trimIndent())
            }
        }

        return true
    }
}