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
    fun `should APPLY by inserting or updating`() {
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
        val table = "Customer"
        given(sqlQueryBuilder.insertOrUpdate(any(), eq(table))).willReturn("insertOrUpdate query")

        // when
        repositoryExecutor.run<CustomerOutput>(operation, table)

        // then
        verify(repositoryConnector, times(3)).execute(eq("insertOrUpdate query"))
    }

    @Test
    fun `should APPLY_AND_CLEAR by inserting or updating and clear`() {
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
        val table = "Map"

        given(sqlQueryBuilder.insertOrUpdate(any(), eq(table))).willReturn("insertOrUpdate query")
        given(sqlQueryBuilder.deleteNotIn(eq(operation.data), eq(table))).willReturn("deleteNotIn query")

        // when
        repositoryExecutor.run<MapOutput>(operation, table)

        // then
        verify(repositoryConnector, times(3)).execute(eq("insertOrUpdate query"))
        verify(repositoryConnector, times(1)).execute(eq("deleteNotIn query"))
    }

    @Test
    fun `should DELETE router`() {
        // given
        val operation = OutputOperationDto(
            type = OutputOperationTypeDto.DELETE,
            entity = OutputOperationEntityDto.ROUTER,
            data = listOf(
                mockData.routerOutput("r1", "m1"),
                mockData.routerOutput("r2", "m1"),
                mockData.routerOutput("r3", "m1")
            )
        )
        val table = "Router"
        given(sqlQueryBuilder.delete(anyString(), eq(table))).willReturn("delete query")

        // when
        repositoryExecutor.run<RouterOutput>(operation, table)

        // then
        verify(repositoryConnector, times(3)).execute(eq("delete query"))
    }
}