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
            FileWriter(File(packageFile, fileName)).use {
                val daoVariableName = element.simpleName.toString()
                        .let { name -> name[0].toLowerCase() + name.drop(1) + "Dao" }

                it.write("""
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

    @Insert
    fun addItems(items: List<${element.simpleName}>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveItem(item: ${element.simpleName})

    @Query("SELECT * FROM ${element.simpleName} WHERE id = :itemId")
    fun getItem(itemId: Long): ${element.simpleName}

    @Query("SELECT * FROM ${element.simpleName} WHERE ${element.getAnnotation(GenerateDao::class.java).searchField} LIKE '%' || :search || '%' LIMIT :count OFFSET :offset")
    fun getItems(search: String, offset: Int, count: Int): List<${element.simpleName}>

    @Query("DELETE FROM ${element.simpleName}")
    fun deleteAllItems()
}

fun ${element.simpleName}Dao.wrap() = ${element.simpleName}DaoWrapper(this)

class ${element.simpleName}DaoWrapper(private val $daoVariableName: ${element.simpleName}Dao) : DaoInterface<${element.simpleName}> {
    override fun addItems(items: List<${element.simpleName}>) = $daoVariableName.addItems(items)
    override fun saveItem(item: ${element.simpleName}) = $daoVariableName.saveItem(item)
    override fun getItem(itemId: Long): ${element.simpleName} = $daoVariableName.getItem(itemId)
    override fun getItems(search: String, offset: Int, count: Int): List<${element.simpleName}> = $daoVariableName.getItems(search, offset, count)
    override fun deleteAllItems() = $daoVariableName.deleteAllItems()
}
                """.trimIndent())
            }
        }

        return true
    }
}