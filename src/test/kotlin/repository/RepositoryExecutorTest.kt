package repository

import com.nhaarman.mockito_kotlin.*
import com.triangl.processing.dto.OutputOperationDto
import com.triangl.processing.dto.OutputOperationEntityDto
import com.triangl.processing.dto.OutputOperationTypeDto
import com.triangl.processing.helper.SQLQueryBuilder
import com.triangl.processing.outputEntity.CustomerOutput
import com.triangl.processing.outputEntity.MapOutput
import com.triangl.processing.outputEntity.RouterOutput
import com.triangl.processing.repository.RepositoryConnector
import com.triangl.processing.repository.RepositoryExecutor
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import testSupport.MockData

@RunWith(MockitoJUnitRunner::class)
class RepositoryExecutorTest {

    @Mock
    private lateinit var repositoryConnector: RepositoryConnector

    @Mock
    private lateinit var sqlQueryBuilder: SQLQueryBuilder

    @InjectMocks
    private lateinit var repositoryExecutor: RepositoryExecutor

    private val mockData = MockData()

    @Test
    fun `should check existing and update`() {
        // given
        val data = mockData.customerOutput("c1")
        val outputClass = CustomerOutput::class.java

        given(sqlQueryBuilder.select(anyString())).willReturn("select query")
        given(sqlQueryBuilder.update(any())).willReturn("update query")
        given(repositoryConnector.get(anyString(), eq(outputClass))).willReturn(listOf(data))

        // when
        repositoryExecutor.apply(data, outputClass)

        // then
        verify(repositoryConnector, times(1)).get(eq("select query"), eq(outputClass))
        verify(repositoryConnector, times(1)).modify(eq("update query"), eq(outputClass))
    }

    @Test
    fun `should check existing and create if missing`() {
        // given
        val data = mockData.customerOutput("c1")
        val outputClass = CustomerOutput::class.java

        given(sqlQueryBuilder.select(anyString())).willReturn("select query")
        given(sqlQueryBuilder.insert(any())).willReturn("insert query")
        given(repositoryConnector.get(anyString(), eq(outputClass))).willReturn(emptyList())

        // when
        repositoryExecutor.apply(data, outputClass)

        // then
        verify(repositoryConnector, times(1)).get(eq("select query"), eq(outputClass))
        verify(repositoryConnector, times(1)).modify(eq("insert query"), eq(outputClass))
    }

    @Test
    fun `should APPLY existing and update`() {
        // given
        val operation = OutputOperationDto(
                type = OutputOperationTypeDto.APPLY,
                entity = OutputOperationEntityDto.CUSTOMER,
                data = listOf(
                    mockData.customerOutput("c1"),
                    mockData.customerOutput("c2"),
                    mockData.customerOutput("c3")
                )
        )
        val outputClass = CustomerOutput::class.java
        val table = "Customer"
        given(sqlQueryBuilder.select(anyString())).willReturn("select query")
        given(sqlQueryBuilder.update(any())).willReturn("update query")
        given(repositoryConnector.get(anyString(), eq(outputClass))).willReturn(listOf(operation.data[0]))

        // when
        repositoryExecutor.run(operation, table, outputClass)

        // then
        verify(repositoryConnector, times(3)).get(eq("select query"), eq(outputClass))
        verify(repositoryConnector, times(3)).modify(eq("update query"), eq(outputClass))
    }

    @Test
    fun `should APPLY_AND_CLEAR missing and create`() {
        // given
        val operation = OutputOperationDto(
            type = OutputOperationTypeDto.APPLY_AND_CLEAR,
            entity = OutputOperationEntityDto.MAP,
            data = listOf(
                mockData.mapOutput("m1", "c1"),
                mockData.mapOutput("m2", "c1"),
                mockData.mapOutput("m3", "c1")
            )
        )
        val outputClass = MapOutput::class.java
        val table = "Map"
        given(sqlQueryBuilder.select(anyString())).willReturn("select query")
        given(sqlQueryBuilder.insert(any())).willReturn("insert query")
        given(sqlQueryBuilder.deleteNotIn(eq(operation.data))).willReturn(eq(listOf("deleteNotIn query")))

        // when
        repositoryExecutor.run(operation, table, outputClass)

        // then
        verify(repositoryConnector, times(3)).get(eq("select query"), eq(outputClass))
        verify(repositoryConnector, times(3)).modify(eq("insert query"), eq(outputClass))
        verify(repositoryConnector, times(1)).modify(eq("deleteNotIn query"), eq(outputClass))
    }

    @Test
    fun `should DELETE router`() {
        // given
        val operation = OutputOperationDto(
            type = OutputOperationTypeDto.DELETE,
            entity = OutputOperationEntityDto.ROUTER,
            data = listOf(
                mockData.routerOutput("r1", "c1", "m1"),
                mockData.routerOutput("r2", "c1", "m1"),
                mockData.routerOutput("r3", "c1", "m1")
            )
        )
        val outputClass = RouterOutput::class.java
        val table = "Router"
        given(sqlQueryBuilder.delete(anyString())).willReturn("delete query")

        // when
        repositoryExecutor.run(operation, table, outputClass)

        // then
        verify(repositoryConnector, times(3)).modify(eq("delete query"), eq(outputClass))
    }
}