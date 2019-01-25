/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.tests

import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.utils.Printer
import java.io.File
import java.io.FileWriter

class TestInfo(val name: String, val info: FqName, val file: File)

class TestFileWriter(val fileName: String, index: Int) {

    fun generate(info: List<TestInfo>) {
        FileWriter(File(fileName).also { it.parentFile.mkdirs() }).use {
            val p = Printer(it)
            p.print(FileUtil.loadFile(File("license/LICENSE.txt")))
            p.println(
                """package ${CodegenTestsOnAndroidGenerator.testClassPackage};
                |
                |import ${CodegenTestsOnAndroidGenerator.baseTestClassPackage}.${CodegenTestsOnAndroidGenerator.baseTestClassName};
                |
                |/* This class is generated by ${CodegenTestsOnAndroidGenerator.generatorName}. DO NOT MODIFY MANUALLY */
                |public class ${CodegenTestsOnAndroidGenerator.testClassName} extends ${CodegenTestsOnAndroidGenerator.baseTestClassName} {
                |
            """.trimMargin()
            )
            p.pushIndent()

            info.forEach {
                generateTestMethod(p, it.name, it.info.asString(), StringUtil.escapeStringCharacters(it.file.path))
            }

            p.popIndent()
            p.println("}")
        }


    }

    fun generateTestMethod(p: Printer, testName: String, className: String, filePath: String) {
        p.println("public void test$testName() throws Exception {")
        p.pushIndent()
        p.println("invokeBoxMethod($className.class, \"$filePath\", \"OK\");")
        p.popIndent()
        p.println("}")
        p.println()
    }

}