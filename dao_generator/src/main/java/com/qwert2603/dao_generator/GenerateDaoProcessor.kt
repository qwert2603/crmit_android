package com.qwert2603.dao_generator

import java.io.File
import java.io.FileWriter
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("com.qwert2603.dao_generator.GenerateDao")
class GenerateDaoProcessor : AbstractProcessor() {

    companion object {

        // !!!! IMPORTANT !!!!
        // !!!! IMPORTANT !!!!
        // !!!! IMPORTANT !!!!
        // !!!! IMPORTANT !!!!

        // generated classes are used by another annotation processor (Room).
        // so we need just copy generated files to project's sources.

        private const val GENERATED_PACKAGE = "com.qwert2603.crmit_android.db.dao.anth_package"

        fun getDirName(processingEnv: ProcessingEnvironment): String {
            val dirName = processingEnv.options["kapt.kotlin.generated"]!!
                    .replace("kaptKotlin", "kapt")
                    .plus("/$GENERATED_PACKAGE")
            File(dirName).mkdirs()
            return dirName
        }
    }

    private var done = false

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (done) return true
        done = true

        roundEnv.getElementsAnnotatedWith(GenerateDao::class.java).forEach { element ->
            element as TypeElement

            val dir = getDirName(processingEnv)
            val fileName = "${element.simpleName}Dao.kt"
            FileWriter(File(dir, fileName)).use {
                it.write("""
// this file is auto-generated by com.qwert2603.dao_generator.GenerateDaoProcessor
package $GENERATED_PACKAGE

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

class ${element.simpleName}DaoWrapper(private val ${element.simpleName}Dao: ${element.simpleName}Dao) : DaoInterface<${element.simpleName}> {
    override fun addItems(items: List<${element.simpleName}>) = ${element.simpleName}Dao.addItems(items)
    override fun saveItem(item: ${element.simpleName}) = ${element.simpleName}Dao.saveItem(item)
    override fun getItem(itemId: Long): ${element.simpleName} = ${element.simpleName}Dao.getItem(itemId)
    override fun getItems(search: String, offset: Int, count: Int): List<${element.simpleName}> = ${element.simpleName}Dao.getItems(search, offset, count)
    override fun deleteAllItems() = ${element.simpleName}Dao.deleteAllItems()
}
                """.trimIndent())
            }
        }

        return true
    }
}