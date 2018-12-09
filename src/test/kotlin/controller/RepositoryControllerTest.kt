package controller

import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.times
import com.triangl.processing.controller.RepositoryController
import com.triangl.processing.outputEntity.*
import com.triangl.processing.repository.RepositoryExecutor
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import testSupport.OutputOperations

@RunWith(MockitoJUnitRunner::class)
class RepositoryControllerTest {

    @Mock
    private lateinit var repositoryExecutor: RepositoryExecutor

    private val outputOperations= OutputOperations()

    @Test
    fun `should APPLY in the right order`() {
        // given
        val outputOperation = outputOperations.customerOutputOperation()
        val repositoryController = RepositoryController(outputOperation, repositoryExecutor)

        // when
        repositoryController.applyOutputOperations()

        // then
        inOrder (repositoryExecutor) {
            verify(repositoryExecutor, times(1)).run<CustomerOutput>(
                eq(outputOperation),
                eq("Customer")
            )
            verify(repositoryExecutor, times(1)).run<MapOutput>(
                eq(outputOperation.children[0]),
                eq("Map")
            )
            verify(repositoryExecutor, times(1)).run<RouterOutput>(
                eq(outputOperation.children[0].children[0]),
                eq("Router")
            )
            verify(repositoryExecutor, times(1)).run<CoordinateOutput>(
                eq(outputOperation.children[0].children[0].children[0]),
                eq("Coordinate")
            )
            verify(repositoryExecutor, times(1)).run<AreaOutput>(
                eq(outputOperation.children[0].children[1]),
                eq("Area")
            )
            verify(repositoryExecutor, times(1)).run<CoordinateOutput>(
                eq(outputOperation.children[0].children[1].children[0]),
                eq("Coordinate")
            )
        }
    }

    @Test
    fun `should DELETE in the right order`() {
        // given
        val outputOperation = outputOperations.customerOutputOperation(true)
        val repositoryController = RepositoryController(outputOperation, repositoryExecutor)

        // when
        repositoryController.applyOutputOperations()

        // then
        inOrder (repositoryExecutor) {
            verify(repositoryExecutor, times(1)).run<CoordinateOutput>(
                    eq(outputOperation.children[0].children[0].children[0]),
                    eq("Coordinate")
            )
            verify(repositoryExecutor, times(1)).run<RouterOutput>(
                    eq(outputOperation.children[0].children[0]),
                    eq("Router")
            )
            verify(repositoryExecutor, times(1)).run<CoordinateOutput>(
                    eq(outputOperation.children[0].children[1].children[0]),
                    eq("Coordinate")
            )
            verify(repositoryExecutor, times(1)).run<AreaOutput>(
                    eq(outputOperation.children[0].children[1]),
                    eq("Area")
            )
            verify(repositoryExecutor, times(1)).run<MapOutput>(
                    eq(outputOperation.children[0]),
                    eq("Map")
            )
            verify(repositoryExecutor, times(1)).run<CustomerOutput>(
                    eq(outputOperation),
                    eq("Customer")
            )
        }
    }
}