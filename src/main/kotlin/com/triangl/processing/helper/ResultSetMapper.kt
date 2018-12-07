package com.triangl.processing.helper

import org.apache.commons.beanutils.BeanUtils
import java.lang.reflect.InvocationTargetException
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity

class ResultSetMapper<T> {
    fun mapResultSetToObject(rs: ResultSet?, outputClass: Class<*>): List<T> {
        val outputList = ArrayList<T>()
        try {
            // make sure resultset is not null
            if (rs != null) {
                // check if outputClass has 'Entity' annotation
                if (outputClass.isAnnotationPresent(Entity::class.java)) {
                    // get the resultset metadata
                    val rsmd = rs.metaData
                    // get all the attributes of outputClass
                    val fields = outputClass.declaredFields
                    while (rs.next()) {
                        val bean = outputClass.newInstance() as T
                        for (_iterator in 0 until rsmd
                                .columnCount) {
                            // getting the SQL column name
                            val columnName = rsmd
                                    .getColumnName(_iterator + 1)
                            // reading the value of the SQL column
                            val columnValue = rs.getObject(_iterator + 1)
                            // iterating over outputClass attributes to check if any attribute has 'Column' annotation with matching 'name' value
                            for (field in fields) {
                                if (field.isAnnotationPresent(Column::class.java)) {
                                    val column = field
                                            .getAnnotation(Column::class.java)
                                    if (column.name.equals(
                                                    columnName, ignoreCase = true) && columnValue != null) {
                                        BeanUtils.setProperty(bean, field
                                                .name, columnValue)
                                        break
                                    }
                                }
                            }
                        }
                        outputList.add(bean)
                    }

                } else {
                    System.console().printf("Entity annotation missing.")
                }
            }
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }

        return outputList
    }
}